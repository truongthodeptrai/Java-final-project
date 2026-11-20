CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE OR REPLACE FUNCTION f_unaccent(text) RETURNS text
LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT AS
$$ SELECT public.unaccent('public.unaccent', $1) $$;

CREATE COLLATION IF NOT EXISTS vi_icu (provider = icu, locale = 'vi');

CREATE TYPE gender_type       AS ENUM ('male', 'female', 'other');
CREATE TYPE account_status    AS ENUM ('active', 'locked');
CREATE TYPE group_member_role AS ENUM ('member', 'admin');
CREATE TYPE spam_status       AS ENUM ('pending', 'resolved', 'dismissed');

CREATE TABLE users (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,          -- store a hash (bcrypt/argon2), NEVER the raw password
    full_name      VARCHAR(100) NOT NULL,
    first_name     VARCHAR(100) GENERATED ALWAYS AS (substring(trim(full_name) FROM '[^ ]+$')) STORED,
    address        VARCHAR(255),
    date_of_birth  DATE,
    gender         gender_type,
    email          VARCHAR(255) NOT NULL,
    status         account_status NOT NULL DEFAULT 'active',
    is_admin       BOOLEAN      NOT NULL DEFAULT FALSE,   -- TRUE = system administrator account
    locked_at      TIMESTAMPTZ,
    password_changed_at TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at     TIMESTAMPTZ,                           -- soft delete
    CONSTRAINT chk_users_dob CHECK (date_of_birth IS NULL OR date_of_birth <= CURRENT_DATE)
);

CREATE UNIQUE INDEX uq_users_username ON users (lower(username)) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_users_email    ON users (lower(email))    WHERE deleted_at IS NULL;

CREATE INDEX idx_users_full_name  ON users (lower(full_name));
CREATE INDEX idx_users_first_name ON users (first_name COLLATE "vi_icu");

CREATE INDEX idx_users_fullname_search ON users USING gin (f_unaccent(lower(full_name)) gin_trgm_ops);
CREATE INDEX idx_users_username_search ON users USING gin (f_unaccent(lower(username))  gin_trgm_ops);
CREATE INDEX idx_users_status     ON users (status);
CREATE INDEX idx_users_created_at ON users (created_at);

-- Automatically update updated_at
CREATE OR REPLACE FUNCTION trg_set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

-- ---------------------------------------------------------------------
-- 2. LOGIN HISTORY
-- ---------------------------------------------------------------------
CREATE TABLE login_history (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    logged_in_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    is_success  BOOLEAN NOT NULL DEFAULT TRUE,   -- shown as "Successful" in the UI
    failure_reason VARCHAR(100),                 -- e.g. 'wrong_password', 'account_locked' (only when is_success = FALSE)
    ip_address  INET,
    user_agent  VARCHAR(500),
    CONSTRAINT chk_login_failure_reason CHECK (is_success OR failure_reason IS NOT NULL)
);

CREATE INDEX idx_login_history_time      ON login_history (logged_in_at DESC);
CREATE INDEX idx_login_history_user_time ON login_history (user_id, logged_in_at DESC);

-- ---------------------------------------------------------------------
-- 3. FRIENDSHIPS (undirected relation, each pair stored once with user_id_1 < user_id_2)
-- ---------------------------------------------------------------------
CREATE TABLE friendships (
    user_id_1  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_id_2  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id_1, user_id_2),
    CONSTRAINT chk_friendship_order CHECK (user_id_1 < user_id_2)
);

CREATE INDEX idx_friendships_user2 ON friendships (user_id_2);

-- All friendship edges in both directions (convenient for querying)
CREATE VIEW v_friend_edges AS
    SELECT user_id_1 AS user_id, user_id_2 AS friend_id, created_at FROM friendships
    UNION ALL
    SELECT user_id_2, user_id_1, created_at FROM friendships;

-- ---------------------------------------------------------------------
-- 4. CHAT GROUPS
-- ---------------------------------------------------------------------
CREATE TABLE chat_groups (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_chat_groups_name       ON chat_groups (lower(name));
CREATE INDEX idx_chat_groups_created_at ON chat_groups (created_at);

CREATE TABLE group_members (
    group_id  BIGINT NOT NULL REFERENCES chat_groups(id) ON DELETE CASCADE,
    user_id   BIGINT NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    role      group_member_role NOT NULL DEFAULT 'member',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, user_id)
);

CREATE INDEX idx_group_members_user ON group_members (user_id);
CREATE INDEX idx_group_members_role ON group_members (group_id, role);

-- ---------------------------------------------------------------------
-- 5. MESSAGES (1-1 or group chat) - used for activity statistics
-- ---------------------------------------------------------------------
CREATE TABLE messages (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_id BIGINT REFERENCES users(id)       ON DELETE CASCADE,  -- 1-1 chat
    group_id     BIGINT REFERENCES chat_groups(id) ON DELETE CASCADE,  -- group chat
    content      TEXT NOT NULL,
    sent_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- Exactly one of the two: sent to a person or to a group
    CONSTRAINT chk_message_target CHECK (
        (recipient_id IS NOT NULL AND group_id IS NULL) OR
        (recipient_id IS NULL AND group_id IS NOT NULL)
    ),
    CONSTRAINT chk_message_not_self CHECK (recipient_id IS DISTINCT FROM sender_id)
);

CREATE INDEX idx_messages_sender_time ON messages (sender_id, sent_at);
CREATE INDEX idx_messages_sent_at     ON messages (sent_at);
CREATE INDEX idx_messages_group       ON messages (group_id) WHERE group_id IS NOT NULL;

-- ---------------------------------------------------------------------
-- 6. APP OPEN LOGS (used for active-user statistics)
-- ---------------------------------------------------------------------
CREATE TABLE app_open_logs (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    opened_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_app_open_logs_time      ON app_open_logs (opened_at);
CREATE INDEX idx_app_open_logs_user_time ON app_open_logs (user_id, opened_at);

-- ---------------------------------------------------------------------
-- 7. SPAM REPORTS
-- ---------------------------------------------------------------------
CREATE TABLE spam_reports (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reporter_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reason           TEXT,
    status           spam_status NOT NULL DEFAULT 'pending',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    handled_by       BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- admin who handled it
    handled_at       TIMESTAMPTZ,
    CONSTRAINT chk_spam_not_self CHECK (reporter_id <> reported_user_id)
);

CREATE INDEX idx_spam_created_at ON spam_reports (created_at DESC);
CREATE INDEX idx_spam_reported   ON spam_reports (reported_user_id);

-- =====================================================================
-- VIEWS & FUNCTIONS FOR REPORTING FEATURES
-- =====================================================================

-- Feature 2: login list in chronological order (time, username, full name)
CREATE VIEW v_login_list AS
SELECT lh.id, lh.logged_in_at, u.username, u.full_name, lh.is_success, lh.failure_reason, lh.ip_address
FROM login_history lh
JOIN users u ON u.id = lh.user_id
ORDER BY lh.logged_in_at DESC;

-- Feature 3: chat groups with member count / admin count / admin names (comma-separated)
CREATE VIEW v_chat_group_summary AS
SELECT g.id, g.name, g.created_at,
       COUNT(gm.user_id)                                  AS member_count,
       COUNT(gm.user_id) FILTER (WHERE gm.role = 'admin') AS admin_count,
       string_agg(u.username, ', ' ORDER BY u.username)
           FILTER (WHERE gm.role = 'admin')               AS admin_names
FROM chat_groups g
LEFT JOIN group_members gm ON gm.group_id = g.id
LEFT JOIN users u          ON u.id = gm.user_id
GROUP BY g.id;

-- Feature 4: spam report list (reported user & reporter)
CREATE VIEW v_spam_report_list AS
SELECT sr.id, sr.created_at, sr.reason, sr.status,
       ru.id AS reported_user_id, ru.username AS reported_username, ru.status AS reported_user_status,
       rp.username AS reporter_username
FROM spam_reports sr
JOIN users ru ON ru.id = sr.reported_user_id
JOIN users rp ON rp.id = sr.reporter_id;

-- Feature 7: users with direct friend count and extended friend count (friends of friends included)
--   direct_friend_count   : number of direct friends
--   extended_friend_count : number of distinct people (direct friends + friends of friends), excluding the user
CREATE VIEW v_user_friend_counts AS
WITH direct AS (
    SELECT user_id, COUNT(*) AS cnt FROM v_friend_edges GROUP BY user_id
),
reach AS (
    SELECT user_id, friend_id AS other_id FROM v_friend_edges
    UNION
    SELECT e1.user_id, e2.friend_id
    FROM v_friend_edges e1
    JOIN v_friend_edges e2 ON e2.user_id = e1.friend_id
    WHERE e2.friend_id <> e1.user_id
),
reach_cnt AS (
    SELECT user_id, COUNT(*) AS cnt FROM reach GROUP BY user_id
)
SELECT u.id AS user_id, u.username, u.full_name, u.created_at,
       COALESCE(d.cnt, 0)  AS direct_friend_count,
       COALESCE(rc.cnt, 0) AS extended_friend_count
FROM users u
LEFT JOIN direct d     ON d.user_id  = u.id
LEFT JOIN reach_cnt rc ON rc.user_id = u.id
WHERE u.deleted_at IS NULL;

-- Feature 5: new users registered within a time range
CREATE OR REPLACE FUNCTION fn_new_users(p_from TIMESTAMPTZ, p_to TIMESTAMPTZ)
RETURNS TABLE (user_id BIGINT, username VARCHAR, full_name VARCHAR, email VARCHAR, created_at TIMESTAMPTZ)
LANGUAGE sql STABLE AS $$
    SELECT u.id, u.username, u.full_name, u.email, u.created_at
    FROM users u
    WHERE u.deleted_at IS NULL
      AND u.created_at >= p_from AND u.created_at < p_to;
$$;

-- Feature 6: new user count per month for a given year (always returns all 12 months)
CREATE OR REPLACE FUNCTION fn_new_users_by_month(p_year INT)
RETURNS TABLE (month INT, new_user_count BIGINT)
LANGUAGE sql STABLE AS $$
    SELECT m.month,
           COUNT(u.id)
    FROM generate_series(1, 12) AS m(month)
    LEFT JOIN users u
           ON u.deleted_at IS NULL
          AND EXTRACT(YEAR  FROM u.created_at) = p_year
          AND EXTRACT(MONTH FROM u.created_at) = m.month
    GROUP BY m.month
    ORDER BY m.month;
$$;

-- Feature 8: active users within a time range
--   open_count   : number of times the app was opened
--   chat_people  : number of distinct people chatted with (1-1)
--   chat_groups  : number of distinct groups chatted in
CREATE OR REPLACE FUNCTION fn_active_users(p_from TIMESTAMPTZ, p_to TIMESTAMPTZ)
RETURNS TABLE (
    user_id BIGINT, username VARCHAR, full_name VARCHAR, created_at TIMESTAMPTZ,
    open_count BIGINT, chat_people BIGINT, chat_groups BIGINT
)
LANGUAGE sql STABLE AS $$
    WITH opens AS (
        SELECT l.user_id, COUNT(*) AS cnt
        FROM app_open_logs l
        WHERE l.opened_at >= p_from AND l.opened_at < p_to
        GROUP BY l.user_id
    ),
    direct_chat AS (  -- 1-1 chat partners (both sending and receiving directions)
        SELECT uid, COUNT(DISTINCT partner) AS cnt FROM (
            SELECT sender_id AS uid, recipient_id AS partner
            FROM messages
            WHERE recipient_id IS NOT NULL AND sent_at >= p_from AND sent_at < p_to
            UNION ALL
            SELECT recipient_id, sender_id
            FROM messages
            WHERE recipient_id IS NOT NULL AND sent_at >= p_from AND sent_at < p_to
        ) t GROUP BY uid
    ),
    group_chat AS (
        SELECT sender_id AS uid, COUNT(DISTINCT group_id) AS cnt
        FROM messages
        WHERE group_id IS NOT NULL AND sent_at >= p_from AND sent_at < p_to
        GROUP BY sender_id
    )
    SELECT u.id, u.username, u.full_name, u.created_at,
           COALESCE(o.cnt, 0), COALESCE(dc.cnt, 0), COALESCE(gc.cnt, 0)
    FROM users u
    LEFT JOIN opens       o  ON o.user_id  = u.id
    LEFT JOIN direct_chat dc ON dc.uid     = u.id
    LEFT JOIN group_chat  gc ON gc.uid     = u.id
    WHERE u.deleted_at IS NULL
      AND (o.cnt IS NOT NULL OR dc.cnt IS NOT NULL OR gc.cnt IS NOT NULL);
$$;

-- Feature 9: number of users who opened the app per month for a given year (all 12 months)
CREATE OR REPLACE FUNCTION fn_active_users_by_month(p_year INT)
RETURNS TABLE (month INT, active_user_count BIGINT)
LANGUAGE sql STABLE AS $$
    SELECT m.month,
           COUNT(DISTINCT l.user_id)
    FROM generate_series(1, 12) AS m(month)
    LEFT JOIN app_open_logs l
           ON EXTRACT(YEAR  FROM l.opened_at) = p_year
          AND EXTRACT(MONTH FROM l.opened_at) = m.month
    GROUP BY m.month
    ORDER BY m.month;
$$;

-- Dashboard: the 4 stat cards (single row)
--   total_users_growth_pct  : growth of total users over the last 30 days (%)
--   active_users_growth_pct : active users today vs. yesterday (%)
--   (growth is NULL when the previous value is 0, to avoid division by zero)
CREATE OR REPLACE FUNCTION fn_dashboard_stats()
RETURNS TABLE (
    total_users BIGINT, total_users_growth_pct NUMERIC,
    active_users_today BIGINT, active_users_growth_pct NUMERIC,
    total_groups BIGINT, pending_spam_reports BIGINT
)
LANGUAGE sql STABLE AS $$
    WITH u AS (
        SELECT COUNT(*) AS total,
               COUNT(*) FILTER (WHERE created_at < now() - interval '30 days') AS prev_total
        FROM users
        WHERE deleted_at IS NULL
    ),
    a AS (
        SELECT COUNT(DISTINCT user_id) FILTER (WHERE opened_at >= current_date
                                                 AND opened_at <  current_date + 1) AS cnt_today,
               COUNT(DISTINCT user_id) FILTER (WHERE opened_at >= current_date - 1
                                                 AND opened_at <  current_date)     AS cnt_yesterday
        FROM app_open_logs
        WHERE opened_at >= current_date - 1
    )
    SELECT u.total,
           ROUND((u.total - u.prev_total) * 100.0 / NULLIF(u.prev_total, 0), 1),
           a.cnt_today,
           ROUND((a.cnt_today - a.cnt_yesterday) * 100.0 / NULLIF(a.cnt_yesterday, 0), 1),
           (SELECT COUNT(*) FROM chat_groups),
           (SELECT COUNT(*) FROM spam_reports WHERE status = 'pending')
    FROM u, a;
$$;

-- =====================================================================
-- SAMPLE QUERIES FOR EACH FEATURE (can be deleted if not needed)
-- =====================================================================
/*
-- 1a. Accent-insensitive filter by name/username/status; sort by name / creation date
--     (searching "nguyen" matches "Nguyễn"; keep the f_unaccent(lower(...)) expression so the GIN index is used)
SELECT * FROM users
WHERE deleted_at IS NULL
  AND (f_unaccent(lower(username))  LIKE '%' || f_unaccent(lower(:kw)) || '%'
    OR f_unaccent(lower(full_name)) LIKE '%' || f_unaccent(lower(:kw)) || '%')
  AND (:status IS NULL OR status = :status::account_status)
ORDER BY full_name COLLATE "vi_icu" ASC;     -- or ORDER BY first_name COLLATE "vi_icu"
                                              -- or ORDER BY created_at DESC

-- Dashboard cards
SELECT * FROM fn_dashboard_stats();

-- 1c. Lock / unlock
UPDATE users SET status = 'locked', locked_at = now() WHERE id = :id;
UPDATE users SET status = 'active', locked_at = NULL  WHERE id = :id;

-- 1d. Update password
UPDATE users SET password_hash = :hash, password_changed_at = now() WHERE id = :id;

-- 1e. Login history of one user
SELECT * FROM login_history WHERE user_id = :id ORDER BY logged_in_at DESC;

-- 1f. Friend list of one user
SELECT u.id, u.username, u.full_name
FROM v_friend_edges e JOIN users u ON u.id = e.friend_id
WHERE e.user_id = :id;

-- 3c/3d. Members / admins of one group
SELECT u.username, u.full_name, gm.role
FROM group_members gm JOIN users u ON u.id = gm.user_id
WHERE gm.group_id = :gid;                         -- add AND gm.role = 'admin' to see admins only

-- 7c. Filter by direct friend count (=, <, >)
SELECT * FROM v_user_friend_counts WHERE direct_friend_count > :n ORDER BY full_name;

-- 8c. Filter by activity count
SELECT * FROM fn_active_users('2026-01-01', '2026-12-31') WHERE open_count >= :n;

-- 6 & 9. Charts
SELECT * FROM fn_new_users_by_month(2026);
SELECT * FROM fn_active_users_by_month(2026);
*/