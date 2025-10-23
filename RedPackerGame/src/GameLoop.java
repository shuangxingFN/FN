

public class GameLoop extends Thread {
    private GameState gameState;
    private Player player;
    private RedPacketManager redPacketManager;
    private RedPacketGame gameFrame;

    public GameLoop(GameState gameState, Player player, RedPacketManager redPacketManager, RedPacketGame gameFrame) {
        this.gameState = gameState;
        this.player = player;
        this.redPacketManager = redPacketManager;
        this.gameFrame = gameFrame;
    }

    @Override
    public void run() {
        while (gameFrame.isGameRunning()) {
            if (!gameState.isGameOver()) {
                player.update();
                redPacketManager.update(player, gameState);
                gameState.update();
                gameFrame.updateTitle("抢红包游戏 - 分数: " + gameState.getScore() +
                        " 时间: " + gameState.getGameTime() + "秒" +
                        " 等级: " + gameState.getLevel() +
                        " 收集: " + gameState.getTotalPacketsCollected() + "个");
            }
            gameFrame.repaint();

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}