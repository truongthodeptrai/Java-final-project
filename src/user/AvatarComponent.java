package user;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Thành phần Avatar tròn đồ họa (AvatarComponent)
 * Hỗ trợ hiển thị chữ viết tắt họ tên (Initials), biểu tượng nhóm dạng vector sắc nét,
 * màu sắc chủ đề động và huy hiệu trạng thái Online / Offline.
 */
public class AvatarComponent extends JComponent {

    private final String name;
    private final int size;
    private final Color bgColor;
    private final boolean isGroup;
    private final Boolean isOnline; // null nếu không hiển thị chấm trạng thái

    private static final Color[] PALETTE = {
            new Color(45, 105, 190),  // Deep Blue
            new Color(15, 157, 88),   // Emerald Green
            new Color(215, 60, 50),   // Coral Red
            new Color(142, 68, 173),  // Amethyst Purple
            new Color(230, 126, 34),  // Warm Orange
            new Color(22, 160, 133),  // Teal
            new Color(52, 73, 94),    // Slate Gray
            new Color(211, 84, 0),    // Pumpkin
            new Color(41, 128, 185)   // Ocean Blue
    };

    public AvatarComponent(String name, int size, boolean isGroup, Boolean isOnline) {
        this(name, size, generateColor(name), isGroup, isOnline);
    }

    public AvatarComponent(String name, int size, Color bgColor, boolean isGroup, Boolean isOnline) {
        this.name = (name != null && !name.trim().isEmpty()) ? name : "?";
        this.size = size;
        this.bgColor = bgColor != null ? bgColor : generateColor(this.name);
        this.isGroup = isGroup;
        this.isOnline = isOnline;

        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setOpaque(false);
    }

    public static Color generateColor(String name) {
        if (name == null || name.trim().isEmpty()) {
            return PALETTE[0];
        }
        int hash = Math.abs(name.hashCode());
        return PALETTE[hash % PALETTE.length];
    }

    private String getInitials() {
        String clean = name.replaceAll("\\(.*?\\)", "").trim();
        if (clean.isEmpty()) clean = name.trim();
        String[] parts = clean.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            String first = parts[0].substring(0, 1);
            String last = parts[parts.length - 1].substring(0, 1);
            return (first + last).toUpperCase();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UserUITheme.enableAntialiasing(g2);

        int d = size - 2;
        int x = 1;
        int y = 1;

        // Vẽ vòng tròn nền
        g2.setColor(bgColor);
        g2.fill(new Ellipse2D.Float(x, y, d, d));

        // Vẽ viền ngoài nhẹ
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new Ellipse2D.Float(x, y, d, d));

        if (isGroup) {
            // Vẽ biểu tượng nhóm dạng vector người (2 người cạnh nhau)
            g2.setColor(Color.WHITE);

            // Người bên trái (phía sau một chút)
            int r1 = (int) (size * 0.16);
            int h1x = (int) (size * 0.38);
            int h1y = (int) (size * 0.34);
            g2.fill(new Ellipse2D.Float(h1x - r1, h1y - r1, r1 * 2, r1 * 2));
            g2.fillArc((int) (size * 0.18), (int) (size * 0.48), (int) (size * 0.40), (int) (size * 0.40), 0, 180);

            // Người bên phải (phía trước)
            int r2 = (int) (size * 0.18);
            int h2x = (int) (size * 0.64);
            int h2y = (int) (size * 0.40);
            g2.fill(new Ellipse2D.Float(h2x - r2, h2y - r2, r2 * 2, r2 * 2));
            g2.fillArc((int) (size * 0.44), (int) (size * 0.54), (int) (size * 0.40), (int) (size * 0.40), 0, 180);
        } else {
            // Vẽ chữ viết tắt họ tên (Initials)
            String text = getInitials();
            g2.setColor(Color.WHITE);

            int fontSize = Math.max(10, (int) (size * 0.42));
            g2.setFont(new Font(UserUITheme.FONT_FAMILY, Font.BOLD, fontSize));

            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent() - fm.getDescent();

            int textX = (size - textW) / 2;
            int textY = (size + textH) / 2 - 1;
            g2.drawString(text, textX, textY);
        }

        // Vẽ chấm trạng thái Online / Offline nếu có
        if (isOnline != null && !isGroup) {
            int dotSize = Math.max(8, (int) (size * 0.28));
            int dotX = size - dotSize - 1;
            int dotY = size - dotSize - 1;

            // Vòng viền trắng bao quanh chấm
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(dotX - 2, dotY - 2, dotSize + 4, dotSize + 4));

            // Chấm màu trạng thái
            Color dotColor = isOnline ? UserUITheme.COLOR_STATUS_ONLINE : UserUITheme.COLOR_STATUS_OFFLINE;
            g2.setColor(dotColor);
            g2.fill(new Ellipse2D.Float(dotX, dotY, dotSize, dotSize));
        }

        g2.dispose();
    }
}
