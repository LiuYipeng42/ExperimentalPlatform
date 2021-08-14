package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RunAttack {
    private static final String manualAttack =
            Arrays.stream(FileOperation.readFile("PaddingOracleFiles/OriginalFiles/manual_attack.py")
                            .split("\n")).filter(x -> !x.contains("#"))
                    .collect(Collectors.joining()).replace(" ", "");

    private static final String autoAttack =
            Arrays.stream(FileOperation.readFile("PaddingOracleFiles/OriginalFiles/auto_attack.py")
                            .split("\n")).filter(x -> !x.contains("#"))
                    .collect(Collectors.joining()).replace(" ", "");

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

    private static String checkCodes(String changedPythonFile) {

        changedPythonFile = limitImportAndFormat(changedPythonFile);

        if (changedPythonFile.equals("不可import其他库")){
            return "不可import其他库";
        }

        if (changedPythonFile.length() > 3000){
            return "代码修改过多";
        }

        if (CodeSimilarity.calculate(manualAttack, changedPythonFile) < 0.84){
            return "代码修改过多";
        }

        return "success";
    }

    public static String runAttack(String filePath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String checkStatus;
        String result;

        if (filePath.contains("manual_attack")) {
            checkStatus = checkCodes(FileOperation.readFile(filePath));
        } else {
            checkStatus = "success";
        }

        if(checkStatus.equals("success")){
            Process process = runtime.exec("python3 " + filePath);

            result = CMDResult.getResult(process, 1);

            if (result.equals("")){
                return "运行出错";
            }

            return result;
        }else {
            return checkStatus;
        }

    }

}
