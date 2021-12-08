package com.guet.ExperimentalPlatform.Utils;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class RunPython {

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

        if (!limitImport(libs, changedPythonFile)) {
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

    private static String checkCodes(String changedFile,
                                     String originalFile,
                                     double codeSimilarity,
                                     String[] forceContains,
                                     String[] libs) {

        if (forceContains != null) {
            for (String s : forceContains) {
                if (!changedFile.contains(s)) {
                    return "不可修改原有代码";
                }
            }
        }

        changedFile = limitImportAndFormat(changedFile, libs);

        if (changedFile.equals("不可import其他库")) {
            return "不可import其他库";
        }

        if (changedFile.length() > 3000) {
            return "代码过多";
        }

        if (codeSimilarity > 0 &&
                CodeSimilarity.calculate(originalFile, changedFile) < codeSimilarity) {
            return "代码修改过多";
        }

        return "success";
    }

    private static String checkCodes(String pythonFile,
                                     String[] forceContains,
                                     String[] libs) {

        if (!limitImport(libs, pythonFile)) {
            return "不可import其他库";
        }

        if (forceContains != null) {
            for (String s : forceContains) {
                if (!pythonFile.contains(s)) {
                    return "不可修改原有代码";
                }
            }
        }

        if (pythonFile.length() > 4000) {
            return "代码过多";
        }

        return "success";
    }

    public static String addTracebackCode(String codes) {

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

        return processedCode.toString();
    }

    private static String getResult(String filePath) throws IOException {

        String result = RunCMD.execute("python3 " + filePath, 1);

        if (result.equals("")) {
            return "无输出";
        }

        return result;
    }

    public static String run(String codes, String filePath, String originalFile,
                             double codeSimilarity, String[] forceContains, String[] libs) throws IOException {
        String checkStatus;
        String result;

        checkStatus = checkCodes(codes, originalFile, codeSimilarity, forceContains, libs);
        codes = addTracebackCode(codes);
        FileOperation.writeFile(filePath, codes);

        if (checkStatus.equals("success")) {
            result = getResult(filePath);
        } else {
            result = checkStatus;
        }

        return result;

    }

    public static String run(String codes, String filePath,
                             String[] forceContains, String[] libs) throws IOException {
        String checkStatus;
        String result;

        checkStatus = checkCodes(codes, forceContains, libs);
        codes = addTracebackCode(codes);
        FileOperation.writeFile(filePath, codes);

        if (checkStatus.equals("success")) {
            result = getResult(filePath);
        } else {
            result = checkStatus;
        }

        return result;
    }

    public static String run(String filePath, String[] libs) throws IOException {
        String checkStatus;
        String result;

        checkStatus = checkCodes(FileOperation.readFile(filePath), null, libs);

        if (checkStatus.equals("success")) {
            result = getResult(filePath);
        } else {
            result = checkStatus;
        }

        return result;
    }

}
