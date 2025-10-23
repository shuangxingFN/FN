import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class RedPacketGame extends Frame {
    private GameState gameState;
    private Player player;
    private RedPacketManager redPacketManager;
    private GameUI gameUI;
    private boolean gameRunning = true;
    private Image offScreenImage = null;

    public static void main(String[] args) {
        RedPacketGame frame = new RedPacketGame();
        frame.initializeFrame();
    }

    private void initializeFrame() {
        // 初始化游戏组件
        gameState = new GameState();
        player = new Player(400, 300);

        // 加载玩家图片
        Image playerImage = loadImage("player.png");
        if (playerImage != null) {
            player.setImage(playerImage);
            System.out.println("成功加载玩家图片");
        } else {
            System.err.println("无法加载玩家图片，将使用默认图形");
        }

        redPacketManager = new RedPacketManager(20);
        gameUI = new GameUI();

        // 加载背景图片
        Image bgImage = loadImage("bg.jpg");
        if (bgImage != null) {
            gameUI.setBgImage(bgImage);
            System.out.println("成功加载背景图片");
        } else {
            System.err.println("无法加载背景图片，将使用默认背景");
        }

        // 设置窗口
        setVisible(true);
        setTitle("抢红包游戏 - 分数: 0");
        setSize(1000, 800);
        setLocation(300, 100);

        // 添加关闭窗口功能
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });

        // 添加键盘监听
        addKeyListener(new KeyMonitor());

        // 启动游戏循环
        new GameLoop(gameState, player, redPacketManager, this).start();
    }

    @Override
    public void paint(Graphics g) {
        gameUI.render(g, gameState, player, redPacketManager);
    }

    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(1000, 800);
        }
        Graphics gOff = offScreenImage.getGraphics();
        paint(gOff);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    // 键盘监听器
    class KeyMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                exitGame();
                return;
            }

            if (keyCode == KeyEvent.VK_Q && !gameState.isGameOver()) {
                endCurrentGame();
                return;
            }

            if (gameState.isGameOver() && keyCode == KeyEvent.VK_R) {
                resetGame();
            } else if (!gameState.isGameOver()) {
                switch (keyCode) {
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

        // 重新加载玩家图片
        Image playerImage = loadImage("player.png");
        if (playerImage != null) {
            player.setImage(playerImage);
        }

        redPacketManager.reset();
    }

    // 结束当前游戏
    private void endCurrentGame() {
        gameState.setGameOver(true);
    }

    // 退出游戏程序
    private void exitGame() {
        gameRunning = false;
        System.exit(0);
    }

    private Image loadImage(String imagePath) {
        try {
            File imageFile = new File("image/" + imagePath);
            if (imageFile.exists()) {
                return ImageIO.read(imageFile);
            } else {
                System.err.println("图片文件不存在: " + imageFile.getAbsolutePath());
                File imageDir = new File("image");
                if (imageDir.exists() && imageDir.isDirectory()) {
                    System.out.println("image文件夹内容:");
                    for (String file : imageDir.list()) {
                        System.out.println("  - " + file);
                    }
                } else {
                    System.err.println("image文件夹不存在!");
                }
            }
        } catch (IOException e) {
            System.err.println("无法加载图片: " + imagePath);
            e.printStackTrace();
        }
        return null;
    }

    public void updateTitle(String title) {
        setTitle(title);
    }

    // 获取游戏运行状态
    public boolean isGameRunning() {
        return gameRunning;
    }
}