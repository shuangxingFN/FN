package codeanalyzer;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class PythonFunctionAnalyzer {
    public void analyzePythonFile(String filePath, JFrame parentFrame) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || !filePath.endsWith(".py")) {
            JOptionPane.showMessageDialog(parentFrame, "文件不存在或不是有效的Python文件!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> lines = FileUtils.readLines(file);
        List<Integer> functionLengths = extractFunctionLengths(lines);

        if (functionLengths.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "未找到Python函数定义!", "信息", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AnalysisResult result = StatisticsCalculator.calculateStatistics(functionLengths);
        displayResults(result, functionLengths.size(), parentFrame);
    }

    private List<Integer> extractFunctionLengths(List<String> lines) {
        // 原有的函数提取逻辑保持不变
        List<Integer> functionLengths = new ArrayList<>();
        int currentFunctionStart = -1;
        int indentLevel = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            // 检测函数定义
            if (line.startsWith("def ") && line.endsWith(":")) {
                // 如果已经有函数在统计中，先保存上一个函数的长度
                if (currentFunctionStart != -1) {
                    int functionLength = i - currentFunctionStart;
                    functionLengths.add(functionLength);
                }

                currentFunctionStart = i;
                // 计算当前行的缩进级别
                String originalLine = lines.get(i);
                indentLevel = countLeadingSpaces(originalLine);
            }
            // 检测类定义（作为函数边界）
            else if (line.startsWith("class ") && line.endsWith(":")) {
                if (currentFunctionStart != -1) {
                    int functionLength = i - currentFunctionStart;
                    functionLengths.add(functionLength);
                    currentFunctionStart = -1;
                }
            }
            // 检测空行和注释，但不是函数边界
            else if (currentFunctionStart != -1 && !line.isEmpty() && !line.startsWith("#")) {
                // 检查是否回到了相同或更低的缩进级别（表示函数结束）
                String originalLine = lines.get(i);
                int currentIndent = countLeadingSpaces(originalLine);
                if (currentIndent <= indentLevel && !line.startsWith("def ")) {
                    int functionLength = i - currentFunctionStart;
                    functionLengths.add(functionLength);
                    currentFunctionStart = -1;
                }
            }
        }

        // 处理最后一个函数
        if (currentFunctionStart != -1) {
            int functionLength = lines.size() - currentFunctionStart;
            functionLengths.add(functionLength);
        }

        return functionLengths;
    }

    private int countLeadingSpaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else if (c == '\t') count += 4; // 假设tab等于4个空格
            else break;
        }
        return count;
    }

    private void displayResults(AnalysisResult result, int functionCount, JFrame parentFrame) {
        StringBuilder message = new StringBuilder();
        message.append("Python函数分析结果\n\n");
        message.append("分析的函数数量: ").append(functionCount).append("\n");
        message.append("函数长度统计:\n");
        message.append("  最小值: ").append(result.getMin()).append(" 行\n");
        message.append("  最大值: ").append(result.getMax()).append(" 行\n");
        message.append("  平均值: ").append(String.format("%.2f", result.getMean())).append(" 行\n");
        message.append("  中位数: ").append(result.getMedian()).append(" 行\n");

        JOptionPane.showMessageDialog(parentFrame, message.toString(), "Python函数分析结果", JOptionPane.INFORMATION_MESSAGE);
    }
}