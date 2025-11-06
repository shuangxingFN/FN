package codeanalyzer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeLineCounter {
    private static final Map<String, String> FILE_EXTENSIONS = new HashMap<>();

    static {
        FILE_EXTENSIONS.put("java", "Java");
        FILE_EXTENSIONS.put("py", "Python");
        FILE_EXTENSIONS.put("cpp", "C++");
        FILE_EXTENSIONS.put("c", "C");
        FILE_EXTENSIONS.put("js", "JavaScript");
        FILE_EXTENSIONS.put("html", "HTML");
        FILE_EXTENSIONS.put("css", "CSS");
        FILE_EXTENSIONS.put("xml", "XML");
        FILE_EXTENSIONS.put("json", "JSON");
    }

    public Map<String, Integer> analyzeDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        Map<String, Integer> languageLines = new HashMap<>();

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("目录不存在或不是有效目录!");
        }

        // 遍历目录统计代码行数
        countLinesRecursive(directory, languageLines);
        return languageLines;
    }

    private void countLinesRecursive(File file, Map<String, Integer> languageLines) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    countLinesRecursive(f, languageLines);
                }
            }
        } else {
            String fileName = file.getName();
            String extension = getFileExtension(fileName);

            if (FILE_EXTENSIONS.containsKey(extension)) {
                String language = FILE_EXTENSIONS.get(extension);
                int lines = FileUtils.countLines(file);
                languageLines.put(language, languageLines.getOrDefault(language, 0) + lines);
            }
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}