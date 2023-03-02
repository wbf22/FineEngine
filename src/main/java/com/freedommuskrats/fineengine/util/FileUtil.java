package com.freedommuskrats.fineengine.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class FileUtil {


    public static void openFileInTextEdit(String filePath) {
        try {
            Runtime.getRuntime().exec("open -a TextEdit " + filePath);
        } catch (IOException e) {
            System.out.println("Couldn't open file in Mac TextEdit. " +
                    "Maybe file doesn't exist or this isn't a mac.");
        }
    }

    public static void openFileInVim(String filePath) {
        try {
            Runtime.getRuntime().exec("vim " + filePath);
        } catch (IOException e) {
            System.out.println("Couldn't open file in Vim. " +
                    "Maybe file doesn't exist or you don't have Vim installed.");
        }
    }

    public static void makeFile(String filePath) {
        try {
            Runtime.getRuntime().exec("touch " + filePath);
        } catch (IOException e) {
            System.out.println("Couldn't create file. Check your filepath for the last command");
        }
    }


    public static void listDirectory(String filePath) {
        System.out.print(ConsoleColors.RED);
        Stream.of(Objects.requireNonNull(new File(filePath).listFiles())).forEach(file -> {
            System.out.println(file.toPath());
        });
        System.out.print(ConsoleColors.RESET);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void printFile(String fileLocation, int lineLength) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            int count = 0;
            char[] chars = Files
                    .readString(Paths.get(fileLocation), StandardCharsets.UTF_8)
                    .toCharArray();

            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];

                stringBuffer.append(c);
                if (c == '\n') {
                    count = 0;
                }
                else {
                    count++;
                }

                if (count >= lineLength) {
                    while (!Character.isWhitespace(c)) {
                        i++;
                        c = chars[i];
                        stringBuffer.append(c);
                    }

                    stringBuffer.append('\n');
                    count = 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't find that file");
        }

        System.out.print(ConsoleColors.BLUE);
        System.out.println(stringBuffer);
        System.out.print(ConsoleColors.RESET);
    }
}
