package com.guet.ExperimentalPlatform.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadForceContains {

    public static String deleteEnter(String text){
        int len = text.length();
        int beginIndex = 0;
        int endIndex = len;

        for (int i = 0; i < len; i++) {
            if (text.charAt(i) != '\n'){
                beginIndex = i;
                break;
            }
        }

        for (int i = len - 1; i > -1; i--) {
            if (text.charAt(i) != '\n'){
                endIndex = i;
                break;
            }
        }

        return text.substring(beginIndex, endIndex + 1);

    }

    public static String[] load(String filePath){

        ArrayList<String> containsList = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;

            StringBuilder part = new StringBuilder();

            while ((line = br.readLine()) != null) {

                if (line.contains("import"))
                    continue;

                if (line.equals("")) {
                    part.append(line).append("\n");
                }else {
                    part.append("    ").append(line).append("\n");
                }

                if (line.contains("# --------------------- START ---------------------")){
                    containsList.add(deleteEnter(part.toString()));
                    part = new StringBuilder();
                }

            }
            containsList.add(part.toString().strip());

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] foreContains = new String[containsList.size()];

        for(int i = 0; i < containsList.size(); i++){
            foreContains[i] = containsList.get(i);
        }

        return foreContains;

    }
}
