package codeanalyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class CodeAnalyzerGUI extends JFrame {
    private CodeLineCounter lineCounter;
    private PythonFunctionAnalyzer functionAnalyzer;
    private CFunctionAnalyzer cFunctionAnalyzer;  // 新增C函数分析器
    private JTextArea resultArea;

    public CodeAnalyzerGUI() {
        lineCounter = new CodeLineCounter();
        functionAnalyzer = new PythonFunctionAnalyzer();
        cFunctionAnalyzer = new CFunctionAnalyzer();  // 初始化C函数分析器
        initializeUI();
    }

    private void initializeUI() {
        setTitle("代码统计分析工具");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("代码统计分析工具", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // 功能选择面板 - 修改为3行1列以容纳新按钮
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new GridLayout(3, 1, 10, 10));
        functionPanel.setBorder(BorderFactory.createTitledBorder("选择分析功能"));

        JButton lineCountButton = new JButton("统计不同编程语言的行数");
        lineCountButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JButton functionAnalyzeButton = new JButton("统计Python函数的长度");
        functionAnalyzeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 新增C函数分析按钮
        JButton cFunctionAnalyzeButton = new JButton("统计C函数的长度");
        cFunctionAnalyzeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        functionPanel.add(lineCountButton);
        functionPanel.add(functionAnalyzeButton);
        functionPanel.add(cFunctionAnalyzeButton);  // 添加到面板

        // 结果显示区域
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("分析结果"));

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton clearButton = new JButton("清空结果");
        JButton backButton = new JButton("返回主菜单");

        bottomPanel.add(clearButton);
        bottomPanel.add(backButton);

        // 添加组件到主面板
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(functionPanel, BorderLayout.CENTER);
        mainPanel.add(resultScrollPane, BorderLayout.SOUTH);
        add(mainPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // 事件监听器
        lineCountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeCodeLines();
            }
        });

        functionAnalyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzePythonFunctions();
            }
        });

        // 新增C函数分析按钮监听器
        cFunctionAnalyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeCFunctions();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭当前窗口
            }
        });
    }

    private void analyzeCodeLines() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("选择要分析的目录");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            resultArea.append("开始分析目录: " + selectedDirectory.getAbsolutePath() + "\n");

            // 在新线程中执行分析，避免界面冻结
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, Integer> languageLines = lineCounter.analyzeDirectory(selectedDirectory.getAbsolutePath());

                        // 在事件分发线程中更新UI
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                displayLineCountResults(languageLines);
                                // 显示饼状图
                                displayPieChart(languageLines);
                            }
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                                        "分析过程中出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void analyzePythonFunctions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("选择Python文件");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Python文件", "py"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.append("开始分析Python文件: " + selectedFile.getName() + "\n");

            // 在新线程中执行分析
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        functionAnalyzer.analyzePythonFile(selectedFile.getAbsolutePath(), CodeAnalyzerGUI.this);
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                                        "分析过程中出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    // 新增C函数分析方法
    private void analyzeCFunctions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("选择C文件");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("C文件", "c"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.append("开始分析C文件: " + selectedFile.getName() + "\n");

            // 在新线程中执行分析
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cFunctionAnalyzer.analyzeCFile(selectedFile.getAbsolutePath(), CodeAnalyzerGUI.this);
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                                        "分析过程中出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void displayLineCountResults(Map<String, Integer> languageLines) {
        StringBuilder resultText = new StringBuilder();
        resultText.append("\n=== 代码行数统计结果 ===\n");

        for (Map.Entry<String, Integer> entry : languageLines.entrySet()) {
            resultText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" 行\n");
        }

        int totalLines = languageLines.values().stream().mapToInt(Integer::intValue).sum();
        resultText.append("总代码行数: ").append(totalLines).append(" 行\n\n");

        resultArea.append(resultText.toString());
    }

    private void displayPieChart(Map<String, Integer> languageLines) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PieChartDisplay pieChart = new PieChartDisplay("代码行数统计", languageLines);
                pieChart.setVisible(true);
            }
        });
    }
}