package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RunPython {

    private static final HashMap<String, String> originalFiles = new HashMap<>();

    static {
        originalFiles.put("manualAttack", loadOriginalFile("PaddingOracleFiles/OriginalFiles/manual_attack.py"));
        originalFiles.put("autoAttack", loadOriginalFile("PaddingOracleFiles/OriginalFiles/auto_attack.py"));
        originalFiles.put("aes", loadOriginalFile("CodeTest/FilesForCopy/aes.py"));
        originalFiles.put("rsa", loadOriginalFile("CodeTest/FilesForCopy/rsa.py"));
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

                for (String part2: part2s) {
                    if(!set.contains(part1 + ":" + part2.split(" as ")[0])){
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

        HashSet<String> set = new HashSet<>(Arrays.asList(libs));

        int i;
        StringBuilder reduceNotes = new StringBuilder();
        for (String line : changedPythonFile.split("\n")) {
            i = line.indexOf("import");
            if (i != -1) {
                for (String s : line.substring(i + 6).split(",")) {
                    if (!set.contains(s.strip())) {
                        return "不可import其他库";
                    }
                }
            }
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

        if (codeSimilarity > 0 &&
                CodeSimilarity.calculate(originalFiles.get(codeType), changedPythonFile) < codeSimilarity) {
            return "代码修改过多";
        }

        return "success";
    }

    private static String checkCodes(String pythonFile,
                                     String[] libs,
                                     String[] forceContains) {

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

    private static String getResult(String filePath) throws IOException {
        System.out.println("python3 " + filePath);

        String result = RunCMD.execute("python3 " + filePath, 1);

        if (result.equals("")) {
            return "编译期编译出错";
        }

        return result;
    }

    public static String run(String filePath, String codeType, double codeSimilarity, String[] libs) throws IOException {
        String checkStatus;

        checkStatus = checkCodes(FileOperation.readFile(filePath), codeType, codeSimilarity, libs);

        if (checkStatus.equals("success")) {
            return getResult(filePath);
        } else {
            return checkStatus;
        }
    }

    public static String run(String filePath, String[] libs) throws IOException {
        String checkStatus;

        checkStatus = checkCodes(FileOperation.readFile(filePath), libs, null);

        if (checkStatus.equals("success")) {
            return getResult(filePath);
        } else {
            return checkStatus;
        }

    }

    public static String run(String filePath, String[] libs, String[] forceContains) throws IOException {
        String checkStatus;

        checkStatus = checkCodes(FileOperation.readFile(filePath), libs, forceContains);

        if (checkStatus.equals("success")) {
            return getResult(filePath);
        } else {
            return checkStatus;
        }

    }

}
