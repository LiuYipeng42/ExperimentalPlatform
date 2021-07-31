package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RunPython {
    private static final String manualAttack =
            Arrays.stream(FileOperation.readFile("PaddingOracleFiles/OriginalFiles/manual_attack.py")
                            .split("\n")).filter(x -> !x.contains("#"))
                    .collect(Collectors.joining()).replace(" ", "");

    private static final String autoAttack =
            Arrays.stream(FileOperation.readFile("PaddingOracleFiles/OriginalFiles/auto_attack.py")
                            .split("\n")).filter(x -> !x.contains("#"))
                    .collect(Collectors.joining()).replace(" ", "");


    private static int getNewIndex(String firstFile, int i, String secondFile, int j) {
        int count = 0;
        while (j < secondFile.length() && i < firstFile.length()) {
            if (firstFile.charAt(i) == secondFile.charAt(j)) {
                i++;
                j++;
                count++;
            } else {
                i = i - count;
                j++;
                count = 0;
            }
            if (count >= 10) {
                return j - count;
            }
        }
        if (count == 0) {
            return 0;
        }
        return j - count;
    }

    private static double codesSimilarity(boolean autoManual, String changedPythonFile) {

        String originalPythonFile;

        if(autoManual){
            originalPythonFile = autoAttack;
        }else {
            originalPythonFile = manualAttack;
        }

        double similarity = 0;

        int originalLength = originalPythonFile.length();
        int changedLength =changedPythonFile.length();
        int originalIndex = 0;
        int changedIndex = 0;
        int newIndex;
        int count;
        while (originalIndex < originalLength && changedIndex < changedLength) {
//            System.out.println(originalIndex + " " + changedIndex);
            if (originalPythonFile.charAt(originalIndex) == changedPythonFile.charAt(changedIndex)) {
                originalIndex++;
                changedIndex++;
                similarity += 1;
            } else {

                newIndex = getNewIndex(originalPythonFile, originalIndex, changedPythonFile, changedIndex);
                if (newIndex == 0) {
                    newIndex = getNewIndex(changedPythonFile, changedIndex, originalPythonFile, originalIndex);
                    if(newIndex == 0){

                        for (int i = originalIndex; i < originalLength; i++) {
                            for (int j = changedIndex; j < changedLength; j++) {
                                if (originalPythonFile.charAt(i) == changedPythonFile.charAt(j)){
                                    for (count = 0; count < 10; count++) {
                                        if (i + count < originalLength && j + count < changedLength) {
                                            if (originalPythonFile.charAt(i + count) != changedPythonFile.charAt(j + count)) {
                                                break;
                                            }
                                        }else {
                                            break;
                                        }
                                    }
                                    if (count >= 10){
                                        similarity -= (i - originalIndex) * 0.5;
                                        similarity -= (j - changedIndex) * 0.5;
                                        originalIndex = i;
                                        changedIndex = j;
                                        i = originalLength;
                                        j = changedLength;
                                    }
                                }
                            }
                            if (i == originalLength - 1){
                                originalIndex = originalLength;
                                changedIndex = changedLength;
                            }
                        }
                    }

                    else {
                        originalIndex = newIndex;
                    }
                }else {
                    similarity -= newIndex - changedIndex;
                    changedIndex = newIndex;
                }

            }
        }

        return similarity / originalPythonFile.length();
    }

    private static String limitImportAndFormat(String changedPythonFile){
        HashSet<String> set = new HashSet<>();
        set.add("socket");
        set.add("hexlify");
        set.add("unhexlify");
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

    private static String checkCodes(boolean autoManual, String changedPythonFile) {

        changedPythonFile = limitImportAndFormat(changedPythonFile);

        if (changedPythonFile.equals("不可import其他库")){
            return "不可import其他库";
        }

        if (changedPythonFile.length() > 3000 || codesSimilarity(autoManual, changedPythonFile) < 0.84){
            return "代码修改过多";
        }

        return "success";
    }

    public static String runPython(String filePath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        boolean autoManual = filePath.contains("auto_attack");
        String checkStatus;
        String result;

        checkStatus = checkCodes(autoManual, FileOperation.readFile(filePath));

        if(checkStatus.equals("success")){
            Process process = runtime.exec("python3 " + filePath);

            result = CMDResult.getResult(process);

            if (result.equals("")){
                return "运行出错";
            }

            return result;
        }else {
            return checkStatus;
        }

    }

}
