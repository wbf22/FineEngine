package com.freedommuskrats.fineengine.ui;

import com.freedommuskrats.fineengine.util.ConsoleColors;
import com.freedommuskrats.fineengine.util.FileUtil;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CliUiImpl {


    @Scheduled(initialDelay = 1, fixedDelay=Long.MAX_VALUE)
    public void run() {
        boolean run = true;
        help();
        System.out.print(">");
        while (run) {
            try{
                Scanner in = new Scanner(System.in);
                if (in.hasNextLine()) {
                    StringTokenizer tokens = new StringTokenizer(in.nextLine());
                    List<String> args = new ArrayList<>();
                    tokens.asIterator().forEachRemaining(t -> args.add(t.toString()));
                    handleCommand(args);
                    System.out.print(">");
                }
            } catch (Exception e) {
                System.out.print(ConsoleColors.RED);
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println("Something went wrong with your last command. We probably " +
                        "forgot to handle something in the code :(");
                System.out.println("Check your last command for mistakes. Sorry!");
                System.out.print(ConsoleColors.RESET);
                System.out.print(">");
            }
        }
    }

    private void help() {
        System.out.print(ConsoleColors.PURPLE);
        System.out.println("**********************");
        System.out.println("Fine Engine Help");
        System.out.println("**********************");
        System.out.println();
        System.out.println("create <-i | -p> <name>");
        System.out.println("    -i creates a new investment entry with provided name");
        System.out.println("    -p creates a new property entry with provided name");
        System.out.println("cls");
        System.out.println("quit");
        System.out.println(ConsoleColors.RESET);
    }

    private void handleCommand(List<String> args) {
        switch (args.get(0)){
            case "help":
                help();
                break;
            case "create":
                create(args);
                break;
            case "cls":
                clear();
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                System.out.print(ConsoleColors.PURPLE);
                System.out.println(args.get(0) + " isn't a command. Do 'help' if you need it");
                System.out.print(ConsoleColors.RESET);
                break;
        }
    }

    private void create(List<String> args) {

    }


    private void open(String path) {
        FileUtil.openFileInTextEdit(path);
    }


    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
