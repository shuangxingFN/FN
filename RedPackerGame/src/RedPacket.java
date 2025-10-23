import java.awt.*;

public class RedPacket {
    private int x, y;
    private int value;
    private boolean active;
    private int size;
    private int shape;
    private double speed;
    private double direction;
    private Color color;

    public RedPacket(int x, int y, int value, int size, int shape) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.size = size;
        this.shape = shape;
        this.active = true;
        this.speed = 1 + Math.random() * 3;
        this.direction = Math.random() * 2 * Math.PI;

        // 根据价值设置颜色
        if (value >= 8) {
            this.color = new Color(255, 215, 0);
        } else if (value >= 5) {
            this.color = new Color(255, 0, 0);
        } else {
            this.color = new Color(255, 100, 100);
        }
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
        } else if (x > 1000 - size) {
            x = 1000 - size;
            direction = Math.PI - direction;
        }

        if (y < 0) {
            y = 0;
            direction = -direction;
        } else if (y > 800 - size) {
            y = 800 - size;
            direction = -direction;
        }

        // 随机改变方向
        if (Math.random() < 0.2) {
            direction += (Math.random() - 0.5) * 0.5;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
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
    public int getSize() { return size; }
    public int getShape() { return shape; }
    public Color getColor() { return color; }
}