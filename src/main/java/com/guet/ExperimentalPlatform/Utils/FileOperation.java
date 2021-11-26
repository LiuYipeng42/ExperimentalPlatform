package com.guet.ExperimentalPlatform.Utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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
            return "";
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

    public static String getPostData(HttpServletRequest request) {
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

    public static String savePostText(HttpServletRequest request, String filePath) {

        String codes = getPostData(request);

        writeFile(
                filePath,
                codes
        );

        return codes;

    }

    public static ResponseEntity<ByteArrayResource> sentToUser(String filePath, String fileName) throws IOException {

        File file = new File(filePath + "/" + fileName);

        HttpHeaders header = new HttpHeaders();

        // Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
        // 当 浏览器 接收到头时，它会激活文件下载对话框，它的文件名框自动填充了头中指定的文件名。
        // （请注意，这是设计导致的；无法使用此功能将文档保存到用户的计算机上，而不向用户询问保存位置。）
        // 服务端向客户端游览器发送文件时，如果是浏览器支持的文件类型，一般会默认使用浏览器打开，比如txt、jpg等，
        // 如果需要提示用户保存，就要利用 Content-Disposition 进行一下处理，关键在于一定要加上attachment：
        header.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        );

        // 清除缓存
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

}
