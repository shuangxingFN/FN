package codeanalyzer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CFunctionAnalyzer {
    public void analyzeCFile(String filePath, JFrame parentFrame) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || !filePath.endsWith(".c")) {
            JOptionPane.showMessageDialog(parentFrame, "文件不存在或不是有效的C文件!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> lines = FileUtils.readLines(file);
        List<Integer> functionLengths = extractFunctionLengths(lines);

        if (functionLengths.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "未找到C函数定义!", "信息", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AnalysisResult result = StatisticsCalculator.calculateStatistics(functionLengths);
        displayResults(result, functionLengths.size(), parentFrame);
    }

    private List<Integer> extractFunctionLengths(List<String> lines) {
        List<Integer> functionLengths = new ArrayList<>();
        int currentFunctionStart = -1;
        int braceLevel = 0;
        boolean inFunction = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String originalLine = lines.get(i);

            // 跳过注释行
            if (line.startsWith("//") || line.startsWith("/*") || line.endsWith("*/")) {
                continue;
            }

            // 检测函数定义 (简化版，匹配典型C函数定义模式)
            if (!inFunction && line.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s+[a-zA-Z_][a-zA-Z0-9_]*\\(.*\\)\\s*\\{")) {
                currentFunctionStart = i;
                inFunction = true;
                braceLevel = 1;
            }
            // 在函数内部处理花括号
            else if (inFunction) {
                // 统计花括号数量
                for (char c : originalLine.toCharArray()) {
                    if (c == '{') {
                        braceLevel++;
                    } else if (c == '}') {
                        braceLevel--;

                        // 函数结束（回到0级）
                        if (braceLevel == 0) {
                            int functionLength = i - currentFunctionStart + 1; // 包含结束括号行
                            functionLengths.add(functionLength);
                            inFunction = false;
                            currentFunctionStart = -1;
                            break;
                        }
                    }
                }
            }
        }

        // 处理文件结束时仍未闭合的函数
        if (inFunction && currentFunctionStart != -1) {
            int functionLength = lines.size() - currentFunctionStart;
            functionLengths.add(functionLength);
        }

        return functionLengths;
    }

    private void displayResults(AnalysisResult result, int functionCount, JFrame parentFrame) {
        StringBuilder message = new StringBuilder();
        message.append("C函数分析结果\n\n");
        message.append("分析的函数数量: ").append(functionCount).append("\n");
        message.append("函数长度统计:\n");
        message.append("  最小值: ").append(result.getMin()).append(" 行\n");
        message.append("  最大值: ").append(result.getMax()).append(" 行\n");
        message.append("  平均值: ").append(String.format("%.2f", result.getMean())).append(" 行\n");
        message.append("  中位数: ").append(result.getMedian()).append(" 行\n");

        JOptionPane.showMessageDialog(parentFrame, message.toString(), "C函数分析结果", JOptionPane.INFORMATION_MESSAGE);
    }
}