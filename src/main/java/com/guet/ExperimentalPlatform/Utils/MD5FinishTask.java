package com.guet.ExperimentalPlatform.Utils;

import com.guet.ExperimentalPlatform.pojo.MD5CommandResult;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MD5FinishTask {
    public static String judge(long userId, String userTask, List<MD5CommandResult> commandAndResult) {
        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userId;

        List<String> userCommands = commandAndResult.stream()
                .map(x -> x.getCommand().replace(" ", ""))
                .collect(Collectors.toList());

        List<String> userResults = commandAndResult.stream()
                .map(x -> x.getResult().replace(" ", ""))
                .collect(Collectors.toList());

        String[] files;

        switch (userTask) {
            case "task1":
                files = new String[]{"prefix", "out1.bin", "out2.bin"};
                for (String file : files) {
                    if (!new File(filePath + "/" + file).exists()) {
                        return "缺少 " + file + " 文件";
                    }
                }
                if (!userCommands.contains("md5collgen-pprefix-oout1.binout2.bin")
                        && !userCommands.contains("md5collgen-pprefix-oout2.binout1.bin")) {
                    return "没有运行 md5collgen";
                }
                if (!userCommands.contains("xxdout1.bin") ||
                        !userCommands.contains("xxdout2.bin")) {
                    return "没有进行 xxd 命令检验";
                }
                if (!userCommands.contains("sha256sumout1.binout2.bin") &&
                        !userCommands.contains("sha256sumout2.binout1.bin") &&
                        !(userCommands.contains("sha256sumout1.bin") && userCommands.contains("sha256sumout2.bin"))) {
                    return "没有进行 sha256sum 命令检验";
                }
                if (!userCommands.contains("md5sumout1.binout2.bin") &&
                        !userCommands.contains("md5sumout2.binout1.bin") &&
                        !(userCommands.contains("md5sumout1.bin") && userCommands.contains("md5sumout2.bin"))) {
                    return "没有进行 md5sum 命令检验";
                }
                break;

            case "task2":
                files = new String[]{"prefix", "suffix", "out1.bin", "out2.bin", "out1_long.bin", "out2_long.bin"};
                for (String file : files) {
                    if (!new File(filePath + "/" + file).exists()) {
                        return "缺少 " + file + " 文件";
                    }
                }

                if (!userCommands.contains("sha256sumout1_long.binout2_long.bin") &&
                        !userCommands.contains("sha256sumout2_long.binout1_long.bin") &&
                        !(userCommands.contains("sha256sumout1_long.bin") && userCommands.contains("sha256sumout2_long.bin"))) {
                    return "没有进行 sha256sum 命令检验";
                }
                if (!userCommands.contains("md5sumout1_long.binout2_long.bin") &&
                        !userCommands.contains("md5sumout2_long.binout1_long.bin") &&
                        !(userCommands.contains("md5sumout1_long.bin") && userCommands.contains("md5sumout2_long.bin"))) {
                    return "没有进行 md5sum 命令检验";
                }
                break;

            case "task3":
                files = new String[]{"task3", "a1.out", "a2.out"};
                for (String file : files) {
                    if (!new File(filePath + "/" + file).exists()) {
                        return "缺少 " + file + " 文件";
                    }
                }

                if (!userCommands.contains("hextask3")) {
                    return "没有以 16进制的形式查看 task3 文件";
                }

                if (!userCommands.contains("vitask3.sh")) {
                    return "没有利用 vi 命令编辑 task3.sh 中的代码";
                }

                if (!userCommands.contains("./task3.sh")) {
                    return "没有运行 task3.sh";
                }

                if (!userCommands.contains("./a1.out") &&
                        !userCommands.contains("./a2.out")) {
                    return "没有运行 a1.out 或 a2.out 命令";
                }

                if (Objects.equals(userResults.get(userCommands.indexOf("./a1.out")), "") ||
                        Objects.equals(userResults.get(userCommands.indexOf("./a2.out")), "")) {
                    return "a1.out 或 a1.out 运行失败";
                }

                if (!userCommands.contains("md5suma1.outa2.out") &&
                        !userCommands.contains("md5suma2.outa2.out") &&
                        !(userCommands.contains("md5suma1.out") && userCommands.contains("md5suma2.out"))) {
                    return "没有进行 md5sum 命令检验";
                }
                break;
            case "task4":
                files = new String[]{"task4", "a1.out", "a2.out"};
                for (String file : files) {
                    if (!new File(filePath + "/" + file).exists()) {
                        return "缺少 " + file + " 文件";
                    }
                }

                if (!userCommands.contains("hextask4")) {
                    return "没有以 16进制的形式查看 task4 文件";
                }

                if (!userCommands.contains("vitask4-1.sh")) {
                    return "没有利用 vi 命令编辑 task4-1.sh 中的代码";
                }

                if (!userCommands.contains("vitask4-2.sh")) {
                    return "没有利用 vi 命令编辑 task4-2.sh 中的代码";
                }

                if (!userCommands.contains("./task4-1.sh")) {
                    return "没有运行 task4-1.sh";
                }

                if (!userCommands.contains("./task4-2.sh")) {
                    return "没有运行 task-2.sh";
                }

                if (!userCommands.contains("./a1.out") &&
                        !userCommands.contains("./a2.out")) {
                    return "没有运行 a1.out 或 a2.out 命令";
                }

                if (!userCommands.contains("md5suma1.outa2.out") &&
                        !userCommands.contains("md5suma2.outa1.out") &&
                        !(userCommands.contains("md5suma1.out") && userCommands.contains("md5suma2.out"))) {
                    return "没有进行 md5sum 命令检验";
                }

                for (MD5CommandResult result : commandAndResult) {
                    if (result.getCommand().equals("./a1.out")) {
                        if (!result.getResult().contains("Executing benign code...")) {
                            return "a1.out 运行失败";
                        }
                    }

                    if (result.getCommand().equals("./a2.out")) {
                        if (!result.getResult().contains("Executing malicious code...")) {
                            return "a2.out 运行失败";
                        }
                    }
                }
                break;
            case "task5":

                files = new String[]{"out1.py", "out2.py"};
                for (String file : files) {
                    if (!new File(filePath + "/" + file).exists()) {
                        return "缺少 " + file + " 文件";
                    }
                }

                if (userCommands.contains("viout1.py") || userCommands.contains("viout2.py")){
                    new File(filePath + "/out1.py").delete();
                    new File(filePath + "/out2.py").delete();
                    return "不可编辑 out1.py 与 out2.py，请退出此实验重做";
                }

                if (!userCommands.contains("hexout1.py")) {
                    return "没有以 16 进制的形式查看 out1.py 文件";
                }

                if (!userCommands.contains("hexout2.py")) {
                    return "没有以 16 进制的形式查看 out2.py 文件";
                }

                if (!userCommands.contains("md5sumout1.pyout2.py") &&
                        !userCommands.contains("md5sumout2.pyout1.py") &&
                        !(userCommands.contains("md5sumout1.py") && userCommands.contains("md5sumout2.py"))) {
                    return "没有进行 md5sum 命令检验";
                }

                for (MD5CommandResult result : commandAndResult) {
                    if (result.getCommand().equals("python3out1.py")) {
                        if (!result.getResult().contains("HelloWorld\nHelloWorld\nHelloWorld")) {
                            return "out1.py 运行失败";
                        }
                    }

                    if (result.getCommand().equals("python3out2.py")) {
                        if (!result.getResult().contains("GoodBye\nGoodBye\nGoodBye")) {
                            return "out2.py 运行失败";
                        }
                    }
                }

                break;
        }

        return "成功完成任务";
    }
}
