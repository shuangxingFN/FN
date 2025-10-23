

import java.awt.*;

public class GameUI {
    private Image bgImage;

    public GameUI() {
        // 注意：这里需要修改为正确的图片加载方式
        // bgImage = loadImage("../images/bg.jpg");
    }

    public void render(Graphics g, GameState gameState, Player player, RedPacketManager redPacketManager) {
        // 绘制背景
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, 1000, 800, null);
        } else {
            g.setColor(new Color(255, 240, 240));
            g.fillRect(0, 0, 1000, 800);
        }

        if (gameState.isGameOver()) {
            renderGameOver(g, gameState);
        } else {
            renderGame(g, gameState, player, redPacketManager);
        }

        // 在游戏界面右上角显示退出提示
        if (!gameState.isGameOver()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            g.drawString("按ESC退出游戏", 850, 50);
            g.drawString("按Q结束游戏", 850, 75);
        }
    }

    private void renderGame(Graphics g, GameState gameState, Player player, RedPacketManager redPacketManager) {
        // 绘制玩家
        if (player.getImage() != null) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), 50, 50, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(player.getX(), player.getY(), 50, 50);
            g.setColor(Color.WHITE);
            g.fillRect(player.getX() + 20, player.getY() - 10, 10, 10);
        }

        // 绘制红包
        for (RedPacket packet : redPacketManager.getRedPackets()) {
            if (packet != null && packet.isActive()) {
                drawRedPacket(g, packet);
            }
        }

        // 绘制UI信息
        renderHUD(g, gameState);
    }

    private void drawRedPacket(Graphics g, RedPacket packet) {
        int x = packet.getX();
        int y = packet.getY();
        int size = packet.getSize();
        int shape = packet.getShape();
        Color color = packet.getColor();

        g.setColor(color);

        switch (shape) {
            case 0:
                g.fillRoundRect(x, y, size, size, 10, 10);
                break;
            case 1:
                g.fillOval(x, y, size, size);
                break;
            case 2:
                drawHexagon(g, x + size/2, y + size/2, size/2);
                break;
            case 3:
                drawStar(g, x + size/2, y + size/2, size/2);
                break;
        }

        g.setColor(new Color(color.getRed()/2, color.getGreen()/2, color.getBlue()/2));
        switch (shape) {
            case 0:
                g.drawRoundRect(x, y, size, size, 10, 10);
                break;
            case 1:
                g.drawOval(x, y, size, size);
                break;
            case 2:
                drawHexagon(g, x + size/2, y + size/2, size/2);
                break;
            case 3:
                drawStar(g, x + size/2, y + size/2, size/2);
                break;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, Math.max(10, size/3)));
        String valueText = String.valueOf(packet.getValue());
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(valueText);
        int textHeight = fm.getHeight();
        g.drawString(valueText,
                x + size/2 - textWidth/2,
                y + size/2 + textHeight/4);

        g.setColor(new Color(255, 200, 200, 100));
        g.fillOval(x + 5, y + 5, 5, 5);
    }

    private void drawHexagon(Graphics g, int centerX, int centerY, int radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            hexagon.addPoint(x, y);
        }
        g.fillPolygon(hexagon);
    }

    private void drawStar(Graphics g, int centerX, int centerY, int radius) {
        int innerRadius = radius / 2;
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI / 10 * i;
            int r = (i % 2 == 0) ? radius : innerRadius;
            int x = (int) (centerX + r * Math.cos(angle));
            int y = (int) (centerY + r * Math.sin(angle));
            star.addPoint(x, y);
        }
        g.fillPolygon(star);
    }

    private void renderGameOver(Graphics g, GameState gameState) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 1000, 800);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("微软雅黑", Font.BOLD, 60));
        g.drawString("恭喜发财!", 300, 200);

        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 36));
        g.drawString("游戏结束!", 380, 280);

        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        g.drawString("总分: " + gameState.getScore(), 420, 380);
        g.drawString("用时: " + gameState.getGameTime() + "秒", 420, 430);
        g.drawString("收集红包: " + gameState.getTotalPacketsCollected() + "个", 420, 480);

        g.setColor(Color.GREEN);
        g.setFont(new Font("微软雅黑", Font.BOLD, 24));
        g.drawString("按R键重新开始游戏", 380, 520);

        g.setColor(Color.RED);
        g.drawString("按ESC键退出游戏", 380, 560);
    }

    private void renderHUD(Graphics g, GameState gameState) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.BOLD, 20));
        g.drawString("分数: " + gameState.getScore(), 20, 60);
        g.drawString("时间: " + gameState.getGameTime() + "秒", 20, 90);
        g.drawString("等级: " + gameState.getLevel(), 20, 120);
        g.drawString("收集: " + gameState.getTotalPacketsCollected() + "个", 20, 150);
    }

    // 设置背景图片
    public void setBgImage(Image bgImage) {
        this.bgImage = bgImage;
    }
}