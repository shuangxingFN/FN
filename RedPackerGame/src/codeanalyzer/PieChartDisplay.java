package codeanalyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Map;

public class PieChartDisplay extends JFrame {
    private Map<String, Integer> data;

    // 预定义颜色列表
    private static final Color[] COLORS = {
            new Color(255, 99, 132),    // 红色
            new Color(54, 162, 235),    // 蓝色
            new Color(255, 205, 86),    // 黄色
            new Color(75, 192, 192),    // 青色
            new Color(153, 102, 255),   // 紫色
            new Color(255, 159, 64),    // 橙色
            new Color(201, 203, 207),   // 灰色
            new Color(255, 99, 255),    // 粉色
            new Color(50, 205, 50),     // 绿色
            new Color(165, 42, 42)      // 棕色
    };

    public PieChartDisplay(String title, Map<String, Integer> data) {
        this.data = data;

        initializeUI(title);
    }

    private void initializeUI(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // 居中显示

        PieChartPanel chartPanel = new PieChartPanel();
        add(chartPanel, BorderLayout.CENTER);
    }

    private class PieChartPanel extends JPanel {
        private static final int PADDING = 50;
        private static final int LEGEND_WIDTH = 200;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 计算饼图区域
            int width = getWidth();
            int height = getHeight();
            int chartSize = Math.min(width - LEGEND_WIDTH - PADDING * 2, height - PADDING * 2);
            int chartX = PADDING;
            int chartY = (height - chartSize) / 2;

            // 绘制标题
            drawTitle(g2d, width);

            // 计算总数
            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) {
                g2d.drawString("没有数据可显示", width / 2 - 40, height / 2);
                return;
            }

            // 绘制饼图
            drawPieChart(g2d, chartX, chartY, chartSize, total);

            // 绘制图例
            drawLegend(g2d, chartX + chartSize + 20, chartY, chartSize, total);
        }

        private void drawTitle(Graphics2D g2d, int width) {
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 18));
            FontMetrics fm = g2d.getFontMetrics();
            String titleText = "代码行数统计 (总行数: " +
                    data.values().stream().mapToInt(Integer::intValue).sum() + ")";
            int titleWidth = fm.stringWidth(titleText);
            g2d.drawString(titleText, (width - titleWidth) / 2, 30);
        }

        private void drawPieChart(Graphics2D g2d, int x, int y, int size, int total) {
            double startAngle = 0;
            int colorIndex = 0;

            // 绘制饼图
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double angle = 360.0 * entry.getValue() / total;

                g2d.setColor(COLORS[colorIndex % COLORS.length]);
                g2d.fill(new Arc2D.Double(x, y, size, size, startAngle, angle, Arc2D.PIE));

                // 在扇区中心添加百分比标签
                if (angle > 5) { // 只在大扇区上显示标签
                    double midAngle = Math.toRadians(startAngle + angle / 2);
                    int labelX = (int) (x + size / 2 + (size / 4) * Math.cos(midAngle));
                    int labelY = (int) (y + size / 2 + (size / 4) * Math.sin(midAngle));

                    g2d.setColor(Color.BLACK);
                    String percentage = String.format("%.1f%%", (angle / 360.0) * 100);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(percentage);
                    g2d.drawString(percentage, labelX - textWidth / 2, labelY);
                }

                startAngle += angle;
                colorIndex++;
            }

            // 绘制边框
            g2d.setColor(Color.BLACK);
            g2d.draw(new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.PIE));
        }

        private void drawLegend(Graphics2D g2d, int x, int y, int size, int total) {
            int legendY = y;
            int colorIndex = 0;
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                // 绘制颜色方块
                g2d.setColor(COLORS[colorIndex % COLORS.length]);
                g2d.fillRect(x, legendY, 20, 15);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, legendY, 20, 15);

                // 绘制图例文本
                String legendText = String.format("%s: %d 行 (%.1f%%)",
                        entry.getKey(), entry.getValue(),
                        (entry.getValue() * 100.0 / total));
                g2d.drawString(legendText, x + 30, legendY + 12);

                legendY += 25;
                colorIndex++;
            }
        }
    }
}