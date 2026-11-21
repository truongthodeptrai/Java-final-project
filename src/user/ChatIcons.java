package user;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Bộ Icon vector vẽ trực tiếp bằng Graphics2D (ChatIcons)
 * Đảm bảo 100% hiển thị sắc nét trên mọi hệ điều hành và độ phân giải,
 * không phụ thuộc font emoji của hệ thống, không bao giờ bị lỗi ô vuông tofu.
 */
public final class ChatIcons {

    private ChatIcons() {}

    /**
     * Biểu tượng ổ khóa (Padlock) cho Badge "Đã mã hóa đầu cuối"
     */
    public static Icon createLockIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                int shackleW = size - 6;
                int shackleH = size - 5;
                int shackleX = x + 3;
                int shackleY = y + 1;
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(shackleX, shackleY, shackleW, shackleH, 0, 180);

                int bodyY = y + size / 2 - 1;
                int bodyH = size / 2 + 2;
                g2.fill(new RoundRectangle2D.Float(x + 1, bodyY, size - 2, bodyH, 3, 3));

                g2.setColor(Color.WHITE);
                int centerX = x + size / 2;
                g2.fillOval(centerX - 1, bodyY + 3, 3, 3);
                g2.fillRect(centerX, bodyY + 4, 1, 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Kính lúp (Magnifier) cho Tìm kiếm
     */
    public static Icon createSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int circleR = size - 6;
                g2.drawOval(x + 1, y + 1, circleR, circleR);
                g2.drawLine(x + circleR - 1, y + circleR - 1, x + size - 1, y + size - 1);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Ngôi sao lấp lánh / AI Sparkle cho LLM Assist
     */
    public static Icon createAiSparkleIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                int cx = x + size / 2;
                int cy = y + size / 2;
                int r = size / 2;

                Path2D path = new Path2D.Float();
                path.moveTo(cx, cy - r);
                path.quadTo(cx, cy, cx + r, cy);
                path.quadTo(cx, cy, cx, cy + r);
                path.quadTo(cx, cy, cx - r, cy);
                path.quadTo(cx, cy, cx, cy - r);
                path.closePath();

                g2.fill(path);
                g2.fillOval(x + size - 4, y + 1, 3, 3);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Thùng rác cho Xóa lịch sử
     */
    public static Icon createTrashIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawLine(x + 2, y + 3, x + size - 2, y + 3);
                g2.drawLine(x + size / 2 - 2, y + 1, x + size / 2 + 2, y + 1);

                Path2D body = new Path2D.Float();
                body.moveTo(x + 3, y + 4);
                body.lineTo(x + 4, y + size - 1);
                body.lineTo(x + size - 4, y + size - 1);
                body.lineTo(x + size - 3, y + 4);
                g2.draw(body);

                g2.drawLine(x + size / 2 - 2, y + 6, x + size / 2 - 2, y + size - 3);
                g2.drawLine(x + size / 2 + 2, y + 6, x + size / 2 + 2, y + size - 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Cảnh báo tam giác cho Báo cáo spam
     */
    public static Icon createWarningIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D triangle = new Path2D.Float();
                triangle.moveTo(x + size / 2, y + 1);
                triangle.lineTo(x + size - 1, y + size - 1);
                triangle.lineTo(x + 1, y + size - 1);
                triangle.closePath();
                g2.draw(triangle);

                int cx = x + size / 2;
                g2.drawLine(cx, y + 5, cx, y + size - 5);
                g2.fillOval(cx - 1, y + size - 3, 2, 2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Thông tin (Chữ i trong vòng tròn)
     */
    public static Icon createInfoIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawOval(x + 1, y + 1, size - 2, size - 2);

                int cx = x + size / 2;
                g2.fillOval(cx - 1, y + 4, 2, 2);
                g2.drawLine(cx, y + 7, cx, y + size - 4);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Máy bay giấy cho Nút Gửi (Send)
     */
    public static Icon createSendIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                Path2D plane = new Path2D.Float();
                plane.moveTo(x + 1, y + 2);
                plane.lineTo(x + size - 1, y + size / 2);
                plane.lineTo(x + 1, y + size - 2);
                plane.lineTo(x + 4, y + size / 2);
                plane.closePath();
                g2.fill(plane);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Kẹp giấy cho Đính kèm tệp
     */
    public static Icon createPaperclipIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D clip = new Path2D.Float();
                clip.moveTo(x + 4, y + size - 3);
                clip.lineTo(x + size - 4, y + 5);
                clip.curveTo(x + size - 1, y + 2, x + size - 1, y + 1, x + size - 3, y + 1);
                clip.curveTo(x + size - 6, y + 1, x + size - 7, y + 2, x + size - 9, y + 4);
                clip.lineTo(x + 2, y + size - 5);
                g2.draw(clip);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Đóng (Dấu X vector)
     */
    public static Icon createCloseIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 2, y + 2, x + size - 2, y + size - 2);
                g2.drawLine(x + size - 2, y + 2, x + 2, y + size - 2);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Bánh răng (Gear / Settings) cho Quản lý nhóm
     */
    public static Icon createGearIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = x + size / 2;
                int cy = y + size / 2;
                int rOuter = size / 2 - 2;
                int rInner = size / 4;

                // Vòng tròn trung tâm
                g2.drawOval(cx - rInner, cy - rInner, rInner * 2, rInner * 2);

                // Các răng cưa
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4.0;
                    int x1 = (int) Math.round(cx + (rInner + 1) * Math.cos(angle));
                    int y1 = (int) Math.round(cy + (rInner + 1) * Math.sin(angle));
                    int x2 = (int) Math.round(cx + rOuter * Math.cos(angle));
                    int y2 = (int) Math.round(cy + rOuter * Math.sin(angle));
                    g2.drawLine(x1, y1, x2, y2);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Rời nhóm / Đăng xuất (Exit / Leave Door)
     */
    public static Icon createLeaveIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Khung cửa
                int doorW = size / 2 + 1;
                g2.drawRect(x + 1, y + 2, doorW, size - 4);

                // Mũi tên thoát ra
                int arrY = y + size / 2;
                g2.drawLine(x + doorW - 2, arrY, x + size - 1, arrY);
                g2.drawLine(x + size - 4, arrY - 3, x + size - 1, arrY);
                g2.drawLine(x + size - 4, arrY + 3, x + size - 1, arrY);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Hình ảnh (Image / Gallery)
     */
    public static Icon createImageIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Khung ảnh bo góc
                g2.drawRoundRect(x + 1, y + 2, size - 2, size - 4, 3, 3);
                // Mặt trời
                g2.fillOval(x + 4, y + 4, 3, 3);

                // Dãy núi
                Path2D mountain = new Path2D.Float();
                mountain.moveTo(x + 2, y + size - 3);
                mountain.lineTo(x + size / 2 - 1, y + size / 2 + 1);
                mountain.lineTo(x + size * 3 / 4, y + size * 3 / 4);
                mountain.lineTo(x + size - 2, y + size / 2 + 3);
                mountain.lineTo(x + size - 2, y + size - 3);
                g2.draw(mountain);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Mặt cười (Emoji / Smile)
     */
    public static Icon createEmojiIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Khuôn mặt
                g2.drawOval(x + 1, y + 1, size - 2, size - 2);
                // Mắt
                g2.fillOval(x + size / 3 - 1, y + size / 3, 2, 2);
                g2.fillOval(x + size * 2 / 3 - 1, y + size / 3, 2, 2);
                // Miệng cười
                g2.drawArc(x + size / 4 + 1, y + size / 3, size / 2 - 2, size / 2 - 2, 200, 140);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Trả lời (Reply arrow)
     */
    public static Icon createReplyIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Mũi tên uốn cong sang trái
                g2.drawLine(x + 2, y + 5, x + 6, y + 1);
                g2.drawLine(x + 2, y + 5, x + 6, y + 9);
                Path2D curve = new Path2D.Float();
                curve.moveTo(x + 2, y + 5);
                curve.lineTo(x + size / 2, y + 5);
                curve.quadTo(x + size - 2, y + 5, x + size - 2, y + size - 2);
                g2.draw(curve);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Sao chép (Copy / Duplicate)
     */
    public static Icon createCopyIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Trang sau
                g2.drawRoundRect(x + 4, y + 1, size - 5, size - 5, 2, 2);
                // Trang trước
                g2.setColor(Color.WHITE);
                g2.fillRect(x + 1, y + 4, size - 5, size - 5);
                g2.setColor(color);
                g2.drawRoundRect(x + 1, y + 4, size - 5, size - 5, 2, 2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Chuyển tiếp (Forward)
     */
    public static Icon createForwardIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cy = y + size / 2;
                // Hai chevron sang phải
                g2.drawLine(x + 2, cy - 4, x + 6, cy);
                g2.drawLine(x + 2, cy + 4, x + 6, cy);

                g2.drawLine(x + 7, cy - 4, x + 11, cy);
                g2.drawLine(x + 7, cy + 4, x + 11, cy);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Ghim tin nhắn (Pin)
     */
    public static Icon createPinIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Kim ghim
                g2.drawLine(x + 3, y + 3, x + size - 3, y + 3);
                g2.drawLine(x + 5, y + 4, x + 5, y + size / 2 + 1);
                g2.drawLine(x + size - 5, y + 4, x + size - 5, y + size / 2 + 1);
                g2.drawLine(x + 3, y + size / 2 + 1, x + size - 3, y + size / 2 + 1);
                g2.drawLine(x + size / 2, y + size / 2 + 1, x + size / 2, y + size - 1);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Đoạn chat (Speech Bubble) cho Tab Chats
     */
    public static Icon createMessageIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D bubble = new Path2D.Float();
                bubble.moveTo(x + 2, y + 2);
                bubble.lineTo(x + size - 2, y + 2);
                bubble.curveTo(x + size - 1, y + 2, x + size - 1, y + size - 5, x + size - 2, y + size - 5);
                bubble.lineTo(x + 6, y + size - 5);
                bubble.lineTo(x + 2, y + size - 1);
                bubble.lineTo(x + 3, y + size - 5);
                bubble.lineTo(x + 2, y + size - 5);
                bubble.closePath();
                g2.draw(bubble);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Bạn bè (Users) cho Tab Bạn bè
     */
    public static Icon createUsersIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Người 1 (trước)
                g2.drawOval(x + 2, y + 2, 6, 6);
                g2.drawArc(x + 1, y + 9, 8, 8, 0, 180);

                // Người 2 (sau)
                g2.drawOval(x + 8, y + 1, 5, 5);
                g2.drawArc(x + 7, y + 7, 7, 7, 0, 180);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Hộp thư (Mail / Requests) cho Tab Lời mời
     */
    public static Icon createMailIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawRoundRect(x + 1, y + 3, size - 2, size - 6, 2, 2);
                g2.drawLine(x + 1, y + 4, x + size / 2, y + size / 2 + 1);
                g2.drawLine(x + size - 1, y + 4, x + size / 2, y + size / 2 + 1);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Biểu tượng Hai dấu tích xanh (Double Checkmark) cho Trạng thái tin nhắn đã gửi & đã đọc
     * Vẽ trực tiếp bằng Graphics2D vector, tuyệt đối không bị lỗi font ô vuông tofu trên mọi hệ điều hành
     */
    public static Icon createDoubleCheckIcon(int width, int height, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cy = y + height - 3;
                // Dấu tích 1 (trái)
                g2.drawLine(x + 1, cy - 3, x + 4, cy);
                g2.drawLine(x + 4, cy, x + 9, cy - 7);

                // Dấu tích 2 (phải, song song)
                int x2 = x + 4;
                g2.drawLine(x2 + 1, cy - 3, x2 + 4, cy);
                g2.drawLine(x2 + 4, cy, x2 + 9, cy - 7);

                g2.dispose();
            }

            @Override public int getIconWidth() { return width; }
            @Override public int getIconHeight() { return height; }
        };
    }
}
