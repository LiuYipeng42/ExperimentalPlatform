package com.guet.ExperimentalPlatform.Utils;

import com.guet.ExperimentalPlatform.entity.RunCodesRecord;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RunPython {

    private static final HashMap<String, String> originalFiles = new HashMap<>();

    static {
        originalFiles.put("1", loadOriginalFile("PaddingOracleFiles/OriginalFiles/manual_attack.py"));
        originalFiles.put("2", loadOriginalFile("PaddingOracleFiles/OriginalFiles/auto_attack.py"));
        originalFiles.put("4", loadOriginalFile("CodeTest/FilesForCopy/aes.py"));
        originalFiles.put("3", loadOriginalFile("CodeTest/FilesForCopy/rsa.py"));
    }

    private static String loadOriginalFile(String filePath) {
        return Arrays.stream(FileOperation.readFile(filePath)
                        .split("\n")).filter(x -> !x.contains("#"))
                .collect(Collectors.joining()).replace(" ", "");
    }

    public static boolean limitImport(String[] libs, String changedPythonFile) {

        HashSet<String> set = new HashSet<>(Arrays.asList(libs));
        int importIndex;
        int asIndex;
        String lib;
        for (String line : changedPythonFile.split("\n")) {

            if (!line.contains("import") && line.replace(" ", "").length() > 3)
                break;

            if (line.startsWith("from ")) {
                importIndex = line.indexOf("import");

                String part1 = line.substring(5, importIndex - 1);
                String[] part2s = Arrays.stream(line.substring(importIndex + 7).split(","))
                        .map(String::strip).toArray(String[]::new);

                for (String part2 : part2s) {
                    if (!set.contains(part1 + ":" + part2.split(" as ")[0])) {
                        System.out.println(set.contains(part1 + ":" + part2.split(" as ")[0]));
                        return false;
                    }
                }

            }

            if (line.startsWith("import ")) {
                asIndex = line.indexOf(" as ");

                if (asIndex != -1) {
                    lib = line.substring(7, asIndex);
                } else {
                    lib = line.substring(7);
                }

                for (String s : lib.split(",")) {
                    if (!set.contains(s.strip())) {
                        System.out.println(s.strip());
                        return false;
                    }
                }

            }

        }
        return true;
    }

    private static String limitImportAndFormat(String changedPythonFile, String[] libs) {

        if (!limitImport(libs, changedPythonFile)){
            return "不可import其他库";
        }

        StringBuilder reduceNotes = new StringBuilder();
        for (String line : changedPythonFile.split("\n")) {
            if (!line.contains("#")) {
                reduceNotes.append(line);
            }
        }

        return reduceNotes.toString().replace(" ", "").replace("\n", "");
    }

    private static String checkCodes(String changedPythonFile,
                                     String codeType,
                                     double codeSimilarity,
                                     String[] libs) {

        changedPythonFile = limitImportAndFormat(changedPythonFile, libs);

        if (changedPythonFile.equals("不可import其他库")) {
            return "不可import其他库";
        }

        if (changedPythonFile.length() > 3000) {
            return "代码过多";
        }

        System.out.println(originalFiles.get(codeType));

        if (codeSimilarity > 0 &&
                CodeSimilarity.calculate(originalFiles.get(codeType), changedPythonFile) < codeSimilarity) {
            return "代码修改过多";
        }

        return "success";
    }

    private static String checkCodes(String pythonFile,
                                     String[] forceContains,
                                     String[] libs) {

        if (forceContains != null) {
            for (String s : forceContains) {
                System.out.println(pythonFile.contains(s));
                if (!pythonFile.contains(s)) {
                    return "不可修改原有代码";
                }
            }
        }

        if (!limitImport(libs, pythonFile)) {
            return "不可import其他库";
        }

        if (pythonFile.length() > 4000) {
            return "代码过多";
        }

        return "success";
    }

    public static void addTracebackCode(String codes, String filePath) {

        String[] codeLines = codes.split("\n");

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

        FileOperation.writeFile(
                filePath,
                processedCode.toString()
        );

    }

    private static String getResult(String filePath) throws IOException {
        System.out.println("python3 " + filePath);

        String result = RunCMD.execute("python3 " + filePath, 1);

        if (result.equals("")) {
            return "无输出";
        }

        return result;
    }

    public static RunCodesRecord runPostCodes(HttpServletRequest request, String filePath, String codeType,
                                              double codeSimilarity, String[] libs) throws IOException {
        String checkStatus;

        String codes = FileOperation.savePostText(request, filePath);

        String result;

        checkStatus = checkCodes(codes, codeType, codeSimilarity, libs);

        if (checkStatus.equals("success")) {
            result = getResult(filePath);
        } else {
            result = checkStatus;
        }

        return new RunCodesRecord()
                .setCodeType(codeType)
                .setCode(codes)
                .setResult(result)
                .setRunningDatetime(new Date());

    }

    public static RunCodesRecord runPostCodes(HttpServletRequest request, String filePath, String codeType,
                                              String[] libs) throws IOException {
        String checkStatus;

        String codes = FileOperation.savePostText(request, filePath);

        String result;

        checkStatus = checkCodes(codes, null, libs);

        if (checkStatus.equals("success")) {
            result = getResult(filePath);
        } else {
            result = checkStatus;
        }

        return new RunCodesRecord()
                .setCodeType(codeType)
                .setCode(codes)
                .setResult(result)
                .setRunningDatetime(new Date());
    }

    public static String runAndGetTraceback(String codes, String dstFilePath, String[] libs, String[] forceContains)
            throws IOException {

        String checkStatus;

        checkStatus = checkCodes(codes, forceContains, libs);

        addTracebackCode(codes, dstFilePath);

        if (checkStatus.equals("success")) {
            return getResult(dstFilePath);
        } else {
            return checkStatus;
        }

    }

}
