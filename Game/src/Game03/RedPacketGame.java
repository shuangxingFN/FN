package Game03;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

public class RedPacketGame extends Frame {
    private GameState gameState;
    private Player player;
    private RedPacketManager redPacketManager;
    private GameUI gameUI;

    public static void main(String[] args) {
        RedPacketGame frame = new RedPacketGame();
        frame.initializeFrame();
    }

    private void initializeFrame() {
        // 初始化游戏组件
        gameState = new GameState();
        player = new Player(400, 300);
        redPacketManager = new RedPacketManager(20); // 初始20个红包
        gameUI = new GameUI();

        // 设置窗口 - 更大的窗口
        setVisible(true);
        setTitle("抢红包游戏 - 分数: 0");
        setSize(1000, 800); // 更大的窗口
        setLocation(300, 100);

        // 添加关闭窗口功能
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // 添加键盘监听
        addKeyListener(new KeyMonitor());

        // 启动游戏循环
        new GameLoop().start();
    }

    @Override
    public void paint(Graphics g) {
        gameUI.render(g, gameState, player, redPacketManager);
    }

    // 双缓冲技术解决闪烁问题
    private Image offScreenImage = null;

    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(1000, 800);
        }
        Graphics gOff = offScreenImage.getGraphics();
        paint(gOff);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    // 游戏状态类
    class GameState {
        private boolean gameOver = false;
        private int score = 0;
        private long startTime;
        private long gameTime = 0;
        private int collectedPackets = 0;
        private int totalPacketsCollected = 0;
        private int level = 1;

        public GameState() {
            startTime = System.currentTimeMillis();
        }

        public void update() {
            if (!gameOver) {
                gameTime = (System.currentTimeMillis() - startTime) / 1000; // 转换为秒

                // 每30秒升一级
                level = 1 + (int)(gameTime / 30);
            }
        }

        public void collectPacket(int value) {
            score += value;
            collectedPackets++;
            totalPacketsCollected++;
        }

        public void reset() {
            gameOver = false;
            score = 0;
            collectedPackets = 0;
            totalPacketsCollected = 0;
            level = 1;
            startTime = System.currentTimeMillis();
            gameTime = 0;
        }

        // Getters and Setters
        public boolean isGameOver() { return gameOver; }
        public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
        public int getScore() { return score; }
        public long getGameTime() { return gameTime; }
        public int getCollectedPackets() { return collectedPackets; }
        public int getTotalPacketsCollected() { return totalPacketsCollected; }
        public int getLevel() { return level; }
    }

    // 玩家类
    class Player {
        private int x, y;
        private static final int WIDTH = 50;
        private static final int HEIGHT = 50;
        private static final int SPEED = 8;
        private boolean left, right, up, down;
        private Image image;

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
            this.image = loadImage("../images/player.png"); // 你可以替换为玩家图片
        }

        public void update() {
            if (left) x -= SPEED;
            if (right) x += SPEED;
            if (up) y -= SPEED;
            if (down) y += SPEED;

            // 边界检查
            x = Math.max(0, Math.min(1000 - WIDTH, x));
            y = Math.max(0, Math.min(800 - HEIGHT, y));
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        // Getters and Setters
        public int getX() { return x; }
        public int getY() { return y; }
        public Image getImage() { return image; }
        public void setLeft(boolean left) { this.left = left; }
        public void setRight(boolean right) { this.right = right; }
        public void setUp(boolean up) { this.up = up; }
        public void setDown(boolean down) { this.down = down; }
    }

    // 红包类
    class RedPacket {
        private int x, y;
        private int value; // 红包价值
        private boolean active;
        private static final int SIZE = 30;
        private double speed;
        private double direction; // 移动方向（弧度）

        public RedPacket(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.active = true;
            this.speed = 1 + Math.random() * 3; // 随机速度1-4
            this.direction = Math.random() * 2 * Math.PI; // 随机方向
        }

        public void update() {
            if (!active) return;

            // 移动红包
            x += (int)(speed * Math.cos(direction));
            y += (int)(speed * Math.sin(direction));

            // 边界检测和反弹
            if (x < 0) {
                x = 0;
                direction = Math.PI - direction;
            } else if (x > 1000 - SIZE) {
                x = 1000 - SIZE;
                direction = Math.PI - direction;
            }

            if (y < 0) {
                y = 0;
                direction = -direction;
            } else if (y > 800 - SIZE) {
                y = 800 - SIZE;
                direction = -direction;
            }

            // 随机改变方向（小概率）
            if (Math.random() < 0.2) {
                direction += (Math.random() - 0.5) * 0.5; // 小幅度随机改变方向
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, SIZE, SIZE);
        }

        public boolean checkCollision(Player player) {
            return active && getBounds().intersects(player.getBounds());
        }

        // Getters
        public int getX() { return x; }
        public int getY() { return y; }
        public int getValue() { return value; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    // 红包管理器
    class RedPacketManager {
        private RedPacket[] redPackets;
        private int packetCount;

        public RedPacketManager(int initialCount) {
            this.packetCount = initialCount;
            redPackets = new RedPacket[50]; // 最大50个红包
            initializeRedPackets();
        }

        private void initializeRedPackets() {
            for (int i = 0; i < packetCount; i++) {
                createNewPacket(i);
            }
        }

        private void createNewPacket(int index) {
            int x = 50 + (int) (Math.random() * 900);
            int y = 50 + (int) (Math.random() * 700);
            int value = 1 + (int) (Math.random() * 10); // 红包价值1-10分
            redPackets[index] = new RedPacket(x, y, value);
        }

        public void update(Player player, GameState gameState) {
            int activePackets = 0;

            for (int i = 0; i < redPackets.length; i++) {
                RedPacket packet = redPackets[i];
                if (packet != null) {
                    if (packet.isActive()) {
                        packet.update();
                        activePackets++;

                        if (packet.checkCollision(player)) {
                            gameState.collectPacket(packet.getValue());
                            packet.setActive(false);

                            // 被吃掉后，有一定概率生成新红包
                            if (Math.random() < 0.7) { // 70%概率生成新红包
                                // 找一个空闲位置
                                for (int j = 0; j < redPackets.length; j++) {
                                    if (redPackets[j] == null || !redPackets[j].isActive()) {
                                        createNewPacket(j);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 根据游戏等级调整红包数量
            int targetPacketCount = 15 + gameState.getLevel() * 2;
            if (targetPacketCount > 50) targetPacketCount = 50;

            // 如果活跃红包太少，补充新红包
            if (activePackets < targetPacketCount / 2) {
                for (int i = 0; i < redPackets.length && activePackets < targetPacketCount; i++) {
                    if (redPackets[i] == null || !redPackets[i].isActive()) {
                        createNewPacket(i);
                        activePackets++;
                    }
                }
            }
        }

        public void reset() {
            for (int i = 0; i < redPackets.length; i++) {
                redPackets[i] = null;
            }
            packetCount = 20;
            initializeRedPackets();
        }

        public RedPacket[] getRedPackets() { return redPackets; }
    }

    // 游戏UI渲染类
    class GameUI {
        private Image bgImage;

        public GameUI() {
            bgImage = loadImage("../images/bg.jpg"); // 你可以替换为喜庆的背景
        }

        public void render(Graphics g, GameState gameState, Player player, RedPacketManager redPacketManager) {
            // 绘制背景
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, 1000, 800, null);
            } else {
                // 如果没有背景图片，使用纯色背景
                g.setColor(new Color(255, 240, 240)); // 淡红色背景
                g.fillRect(0, 0, 1000, 800);
            }

            if (gameState.isGameOver()) {
                renderGameOver(g, gameState);
            } else {
                renderGame(g, gameState, player, redPacketManager);
            }
        }

        private void renderGame(Graphics g, GameState gameState, Player player, RedPacketManager redPacketManager) {
            // 绘制玩家
            if (player.getImage() != null) {
                g.drawImage(player.getImage(), player.getX(), player.getY(), 50, 50, null);
            } else {
                // 如果没有玩家图片，使用蓝色方块
                g.setColor(Color.BLUE);
                g.fillRect(player.getX(), player.getY(), 50, 50);

                // 添加玩家方向指示
                g.setColor(Color.WHITE);
                g.fillRect(player.getX() + 20, player.getY() - 10, 10, 10);
            }

            // 绘制红包
            for (RedPacket packet : redPacketManager.getRedPackets()) {
                if (packet != null && packet.isActive()) {
                    // 根据红包价值使用不同颜色
                    if (packet.getValue() >= 8) {
                        g.setColor(new Color(255, 215, 0)); // 金色 - 高价值红包
                    } else if (packet.getValue() >= 5) {
                        g.setColor(new Color(255, 0, 0)); // 红色 - 中价值红包
                    } else {
                        g.setColor(new Color(255, 100, 100)); // 淡红色 - 低价值红包
                    }

                    // 绘制红包
                    g.fillRoundRect(packet.getX(), packet.getY(), 30, 30, 10, 10);

                    // 绘制红包价值
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    String valueText = String.valueOf(packet.getValue());
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    int textHeight = fm.getHeight();
                    g.drawString(valueText,
                            packet.getX() + 15 - textWidth/2,
                            packet.getY() + 15 + textHeight/4);

                    // 绘制移动轨迹（小圆点）
                    g.setColor(new Color(255, 200, 200, 100));
                    g.fillOval(packet.getX() + 5, packet.getY() + 5, 5, 5);
                }
            }

            // 绘制UI信息
            renderHUD(g, gameState);
        }

        private void renderGameOver(Graphics g, GameState gameState) {
            // 半透明背景
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
        }

        private void renderHUD(Graphics g, GameState gameState) {
            // 半透明背景
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(10, 10, 280, 100);

            g.setColor(Color.WHITE);
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            g.drawString("分数: " + gameState.getScore(), 30, 70);
            g.drawString("时间: " + gameState.getGameTime() + "秒", 30, 100);
            g.drawString("等级: " + gameState.getLevel(), 30, 130);

            // 进度显示
            g.drawString("收集: " + gameState.getTotalPacketsCollected() + "个", 150, 70);
        }
    }

    // 游戏循环
    class GameLoop extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!gameState.isGameOver()) {
                    player.update();
                    redPacketManager.update(player, gameState);
                    gameState.update();
                    setTitle("抢红包游戏 - 分数: " + gameState.getScore() +
                            " 时间: " + gameState.getGameTime() + "秒" +
                            " 等级: " + gameState.getLevel() +
                            " 收集: " + gameState.getTotalPacketsCollected() + "个");
                }
                repaint();

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 键盘监听器
    class KeyMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState.isGameOver() && e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
            } else if (!gameState.isGameOver()) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        player.setLeft(true);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        player.setRight(true);
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        player.setUp(true);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        player.setDown(true);
                        break;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!gameState.isGameOver()) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        player.setLeft(false);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        player.setRight(false);
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        player.setUp(false);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        player.setDown(false);
                        break;
                }
            }
        }
    }

    // 重置游戏
    private void resetGame() {
        gameState.reset();
        player = new Player(400, 300);
        redPacketManager.reset();
    }

    // 工具方法：加载图片
    private Image loadImage(String imagePath) {
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            }
        } catch (IOException e) {
            System.err.println("无法加载图片: " + imagePath);
        }
        return null;
    }
}