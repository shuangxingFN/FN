import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainLauncher extends JFrame {

    public MainLauncher() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("项目启动器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("欢迎使用项目启动器", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));

        // 项目1按钮
        JButton project1Button = new JButton("运行 RedPackerGame");
        project1Button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        project1Button.setPreferredSize(new Dimension(200, 60));

        // 项目2按钮
        JButton project2Button = new JButton("运行代码统计分析工具");
        project2Button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        project2Button.setPreferredSize(new Dimension(200, 60));

        // 退出按钮
        JButton exitButton = new JButton("退出程序");
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 添加按钮到面板
        buttonPanel.add(project1Button);
        buttonPanel.add(project2Button);

        // 添加组件到主面板
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(exitButton, BorderLayout.SOUTH);

        // 添加事件监听器
        project1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runRedPackerGame();
            }
        });

        project2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCodeAnalyzer();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(mainPanel);
    }

    private void runRedPackerGame() {
        JOptionPane.showMessageDialog(this, "启动 RedPackerGame...", "信息", JOptionPane.INFORMATION_MESSAGE);
        // 调用原有的 RedPacketGame
        RedPacketGame.main(new String[]{});
    }

    private void runCodeAnalyzer() {
        // 启动代码分析工具的图形界面
        codeanalyzer.CodeAnalyzerGUI analyzerGUI = new codeanalyzer.CodeAnalyzerGUI();
        analyzerGUI.setVisible(true);
    }

    public static void main(String[] args) {
        // 在事件分发线程中创建GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainLauncher().setVisible(true);
            }
        });
    }
}