import java.awt.*;

public class Player {
    private int x, y;
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int SPEED = 8;
    private boolean left, right, up, down;
    private Image image;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        // 注意：这里需要修改为正确的图片加载方式
        // this.image = loadImage("../images/player.png");
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
    public void setImage(Image image) { this.image = image; }
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
}