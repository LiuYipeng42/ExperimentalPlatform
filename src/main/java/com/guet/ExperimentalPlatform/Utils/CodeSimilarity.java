package com.guet.ExperimentalPlatform.Utils;

public class CodeSimilarity {
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

    public static double calculate(String originalFile, String changedFile) {

        double similarity = 0;

        int originalLength = originalFile.length();
        int changedLength = changedFile.length();
        int originalIndex = 0;
        int changedIndex = 0;
        int newIndex;
        int count;
        while (originalIndex < originalLength && changedIndex < changedLength) {
//            System.out.println(originalIndex + " " + changedIndex);
            if (originalFile.charAt(originalIndex) == changedFile.charAt(changedIndex)) {
                originalIndex++;
                changedIndex++;
                similarity += 1;
            } else {

                newIndex = getNewIndex(originalFile, originalIndex, changedFile, changedIndex);
                if (newIndex == 0) {
                    newIndex = getNewIndex(changedFile, changedIndex, originalFile, originalIndex);
                    if (newIndex == 0) {

                        for (int i = originalIndex; i < originalLength; i++) {
                            for (int j = changedIndex; j < changedLength; j++) {
                                if (originalFile.charAt(i) == changedFile.charAt(j)) {
                                    for (count = 0; count < 10; count++) {
                                        if (i + count < originalLength && j + count < changedLength) {
                                            if (originalFile.charAt(i + count) != changedFile.charAt(j + count)) {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                    if (count >= 10) {
                                        similarity -= (i - originalIndex) * 0.5;
                                        similarity -= (j - changedIndex) * 0.5;
                                        originalIndex = i;
                                        changedIndex = j;
                                        i = originalLength;
                                        j = changedLength;
                                    }
                                }
                            }
                            if (i == originalLength - 1) {
                                originalIndex = originalLength;
                                changedIndex = changedLength;
                            }
                        }
                    } else {
                        originalIndex = newIndex;
                    }
                } else {
                    similarity -= newIndex - changedIndex;
                    changedIndex = newIndex;
                }

            }
        }

        return similarity / originalFile.length();
    }
}
