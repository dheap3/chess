import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String input;
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");

        //prelogin ui
        printOptions();
        System.out.print("[LOGGED OUT] ->> ");
        while (!exit) {
            input = scanner.nextLine();
            input = input.toLowerCase();
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
                    postLoginUI();
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

    static void postLoginUI() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String input;
        System.out.print("[LOGGED OUT] ->> ");
        while (!exit) {
            input = scanner.nextLine();
            input = input.toLowerCase();
            //should this be one word? how should i read the other input?
            switch (input) {
                case "help":
                    break;
                case "logout":
                    break;
                case "create game":
                    break;
                case "list games":
                    break;
                case "play game":
                    break;
                case "observe game":
                    break;

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