import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String input;
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
        printOptions();
        System.out.print("[LOGGED OUT] ->> ");
        while (!exit) {
            input = scanner.nextLine();
            switch (input) {
                case "help":
                    printOptions();
                    System.out.print("[LOGGED OUT] ->> ");
                    break;
                case "quit":
//                    System.out.println("bye");
                    exit = true;
                    break;
                case "login":
                    //Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
                    System.out.println("login here please");
                    break;
                case "register":
                    //Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
                    System.out.println("register here please");
                    break;
                default:
                    System.out.println("option not valid. please try again");
                    System.out.print("[LOGGED OUT] ->> ");
            }

        }

    }
    static void printOptions() {
        System.out.print("help - display this help menu\n" +
                "login <username> <password> - to log in\n" +
                "register <username> <password> <email>\n" +
                "quit - exit chess\n");
    }
}