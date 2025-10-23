import java.util.Random;

public class RedPacketManager {
    private RedPacket[] redPackets;
    private int packetCount;
    private Random random;

    public RedPacketManager(int initialCount) {
        this.packetCount = initialCount;
        redPackets = new RedPacket[50];
        random = new Random();
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
        int value = 1 + (int) (Math.random() * 10);

        int size;
        int shape;

        // 正相关关系：金额越高，红包越大，形状越特殊
        if (value >= 8) {
            size = 40;
            shape = 3;
        } else if (value >= 5) {
            size = 30;
            shape = 2;
        } else {
            size = 20;
            shape = random.nextInt(2);
        }

        redPackets[index] = new RedPacket(x, y, value, size, shape);
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

                        if (Math.random() < 0.7) {
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

        int targetPacketCount = 15 + gameState.getLevel() * 2;
        if (targetPacketCount > 50) targetPacketCount = 50;

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