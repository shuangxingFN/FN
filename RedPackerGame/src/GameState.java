
public class GameState {
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
            gameTime = (System.currentTimeMillis() - startTime) / 1000;
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