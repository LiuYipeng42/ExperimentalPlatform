package com.guet.ExperimentalPlatform.Utils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;


public class FileOperation {
    public static void copyAndReplace(String src, String dst, String regex, String replacement) {
        writeFile(dst,
                readFile(src)
                        .replaceFirst(regex, replacement));
    }

    public static void copyFile(String src, String dst) {
        writeFile(dst, readFile(src));
    }

    public static String readFile(String filePath) {
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        // 不关闭文件会导致资源的泄露，读写文件都同理
        StringBuilder text = new StringBuilder();

        // Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；
        // 详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(filePath);
             BufferedReader br = new BufferedReader(reader)  // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    public static void writeFile(String filePath, String text) {
        try {
            File writeName = new File(filePath); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(text); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPostData(HttpServletRequest request) {
        ServletInputStream inputStream = null;
        int len;

        try {
            inputStream = request.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();

            byte[] buf = new byte[10240];

            while ((len = inputStream.read(buf)) != -1) {
                stringBuilder.append(new String(buf, 0, len));
            }

            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static void savePostText(HttpServletRequest request, String filePath) {

        writeFile(
                filePath,
                getPostData(request)
        );

    }

    public static void savePostPythonFile(HttpServletRequest request, String filePath) {

        String[] codeLines = getPostData(request).split("\n");

        StringBuilder processedCode = new StringBuilder();
        int index;
        String line;

        for (index = 0; index < codeLines.length; index++) {
            line = codeLines[index];
            if (!line.contains("import") && line.replace(" ", "").length() > 3)
                break;
            processedCode.append(line).append("\n");
        }

        processedCode.append("import traceback, sys\n\n");
        processedCode.append("try:\n");

        for (; index < codeLines.length; index++) {
            line = codeLines[index];
            if (line.equals("")) {
                processedCode.append(line).append("\n");
            } else {
                processedCode.append("    ").append(line).append("\n");
            }

        }

        processedCode.append("\nexcept:\n" +
                "    exception = \"\"\n" +
                "    value, tb = sys.exc_info()[1:]\n" +
                "    for line in traceback.TracebackException(type(value), value, tb, limit=None).format(chain=True):\n" +
                "        exception += line\n" +
                "    print(exception)");

        writeFile(
                filePath,
                processedCode.toString()
        );

    }

}
