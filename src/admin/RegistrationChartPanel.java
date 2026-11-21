package admin;

import javax.swing.*;
import java.awt.*;

public class RegistrationChartPanel extends JPanel {

    private final Color BACKGROUND_COLOR =
        new Color(245, 247, 250);

    private final Color TEXT_COLOR =
        new Color(30, 45, 65);

    private final Color BAR_COLOR =
        new Color(45, 105, 190);

    private final String[] months = {
        "Jan", "Feb", "Mar", "Apr",
        "May", "Jun", "Jul", "Aug",
        "Sep", "Oct", "Nov", "Dec"
    };

    private final int[] registrations = {
        45, 62, 55, 80, 72, 95,
        110, 88, 105, 120, 128, 140
    };

    public RegistrationChartPanel() {

        setLayout(new BorderLayout(0, 25));

        setBackground(BACKGROUND_COLOR);

        setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // Page heading
        add(createHeading(), BorderLayout.NORTH);

        // Chart container
        JPanel chartContainer = new JPanel(
            new BorderLayout()
        );

        chartContainer.setBackground(Color.WHITE);

        chartContainer.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(225, 230, 238)
                ),
                BorderFactory.createEmptyBorder(
                    20, 20, 20, 20
                )
            )
        );

        JLabel chartTitle = new JLabel(
            "Monthly New Registrations - 2026"
        );

        chartTitle.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        chartTitle.setForeground(TEXT_COLOR);

        chartContainer.add(
            chartTitle,
            BorderLayout.NORTH
        );

        // Custom bar chart
        JPanel chart = new BarChart();

        chartContainer.add(
            chart,
            BorderLayout.CENTER
        );

        add(chartContainer, BorderLayout.CENTER);
    }

    // Create page heading
    private JPanel createHeading() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel(
            "Registration Statistics"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JLabel description = new JLabel(
            "View monthly user registration statistics"
        );

        description.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        description.setForeground(Color.GRAY);

        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        panel.add(description);

        return panel;
    }

    // Custom bar chart
    private class BarChart extends JPanel {

        public BarChart() {

            setBackground(Color.WHITE);

            setPreferredSize(
                new Dimension(800, 450)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int left = 65;
            int right = 30;
            int top = 40;
            int bottom = 65;

            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            // Draw Y-axis labels and horizontal grid lines
            g2.setFont(new Font("Arial", Font.PLAIN, 12));

            for (int i = 0; i <= 5; i++) {

                int value = i * 30;

                int y = top + chartHeight
                    - (i * chartHeight / 5);

                g2.setColor(new Color(225, 230, 238));

                g2.drawLine(
                    left, y,
                    width - right, y
                );

                g2.setColor(Color.GRAY);

                g2.drawString(
                    String.valueOf(value),
                    20, y + 5
                );
            }

            // Calculate bar width and spacing
            int barWidth = chartWidth / 18;

            int spacing = chartWidth / 12;

            // Draw bars
            for (int i = 0; i < registrations.length; i++) {

                int barHeight =
                    registrations[i] * chartHeight / 150;

                int x = left + spacing * i
                    + (spacing - barWidth) / 2;

                int y = top + chartHeight - barHeight;

                // Bar
                g2.setColor(BAR_COLOR);

                g2.fillRoundRect(
                    x, y,
                    barWidth, barHeight,
                    8, 8
                );

                // Registration count
                g2.setColor(TEXT_COLOR);

                String value =
                    String.valueOf(registrations[i]);

                g2.drawString(
                    value,
                    x + barWidth / 2
                        - g2.getFontMetrics().stringWidth(value) / 2,
                    y - 8
                );

                // Month label
                g2.setColor(Color.GRAY);

                String month = months[i];

                g2.drawString(
                    month,
                    x + barWidth / 2
                        - g2.getFontMetrics().stringWidth(month) / 2,
                    height - 30
                );
            }

            // Y-axis title
            g2.setColor(TEXT_COLOR);

            g2.drawString(
                "Number of Users",
                10, 20
            );

            // X-axis title
            g2.drawString(
                "Month (2026)",
                width / 2 - 40,
                height - 5
            );
        }
    }
}