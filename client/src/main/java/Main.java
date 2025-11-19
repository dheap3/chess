import chess.ChessGame;
import java.util.Scanner;
import static java.lang.System.exit;

import datamodel.ErrorResponse;
import ui.BoardText;

public class Main {
    static public ServerFacade facade;

    public static void main(String[] args) {
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
        int port = 53414;
        String url = "http://localhost:" + port;
        facade = new ServerFacade(port);
        preloginUI();

    }

    static void preloginUI() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String line;
        String input;
        printPreOptions();
        while (!exit) {
            System.out.print("[LOGGED OUT] ->> ");
            line = scanner.nextLine();
            var args = line.split(" ");
            input = args[0].toLowerCase();
            switch (input) {
                case "help":
                    printPreOptions();
                    System.out.print("[LOGGED OUT] ->> ");
                    break;
                case "quit", "exit":
                    exit = true;
                    //stop server here?
                    break;
                case "login":
                    try {
                        facade.login(args[1], args[2]);
                    } catch (Exception e) {
                        System.out.println("Login failed. Please enter a valid USERNAME/PASSWORD");
//                            System.out.println(e.toString());
                        break;
                    }
//                        facadeResponseHandler(args, "login");
                    postLoginUI();
                    break;
                case "register":
                    try {
                        facade.register(args[1], args[2], args[3]);
                    } catch (Exception e) {
                        System.out.println("Register failed, please enter a valid USERNAME/PASSWORD/EMAIL");
//                        System.out.println(e.toString());
                        break;
                    }
                    postLoginUI();
                    break;
                case "admin":
                    System.out.println("You can now clear the database. Only use for testing purposes. There is no going back");
                    System.out.println("Would you like to clear the database? (Y/N)\n:");
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("y")) {
                        facade.clear();
                    } else {
                        System.out.println("returning to normal menu");
                    }
                    break;
                default:
                    System.out.println("option not valid. please try again");
            }

        }
//        server.stop();
    }

    static void postLoginUI() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String line;
        String input;
        ChessGame.TeamColor color = null;

        System.out.println("You are logged in!");
        printPostOptions();
        while (!exit) {
            System.out.print("[LOGGED IN] ->> ");
            line = scanner.nextLine();
            var args = line.split(" ");
            input = args[0].toLowerCase();
            switch (input) {
                case "help":
                    printPostOptions();
                    System.out.print("[LOGGED IN] ->> ");
                    break;
                case "logout":
                    exit = true;
                    break;
                case "create":
                    try {
                        String gameName = "";
                        for (int i = 1; i < args.length; i++) {
                            gameName += args[i];
                            if (i != args.length - 1) {
                                gameName += " ";
                            }
                        }
                        var game = facade.createGame(gameName);
                        System.out.println("Created game " + gameName);
                    } catch (Exception e) {
                        System.out.println("Create failed. Please enter a valid GAMENAME");
//                            System.out.println(e.toString());
                        break;
                    }
                    break;
                case "list":
                    try {
                        var list = facade.listGames().getGames();
                        System.out.println("# : Game ID : Game Name : Users In Game WHITE|BLACK");
                        for (int i = 0; i < list.size(); i++) {
                            var game = list.get(i);
                            System.out.print((i + 1) + ". : " + game.gameID() + " : " + game.gameName());
                            if (game.whiteUsername() != null) {
                                System.out.print(" : " + game.whiteUsername());
                            }
                            System.out.print(" | ");
                            if (game.blackUsername() != null) {
                                System.out.print(game.blackUsername());
                            }
                            System.out.print("\n");
                        }
//                        System.out.println(list.toString());
                    } catch (Exception e) {
                        System.out.println("List failed. Talk to the administrator :(");
                        System.out.println(e.toString());
                        break;
                    }
                    break;
                case "join":
                    try {
                        int gameID = Integer.parseInt(args[1]);
                        if (args[2].equalsIgnoreCase("WHITE")) {
                            color = ChessGame.TeamColor.WHITE;
                        } else if (args[2].equalsIgnoreCase("BLACK")){
                            color = ChessGame.TeamColor.BLACK;
                        } else {
                            System.out.println("Join failed. Please enter a valid game ID/COLOR");
                            break;
                        }
                        facade.joinGame(color, gameID);
                    } catch (Exception e) {
                        System.out.println("Join failed. Please enter a valid game ID/COLOR");
//                            System.out.println(e.toString());
                        break;
                    }
                    break;
                case "observe":
                    if (args.length != 2) {
                        System.out.println("Observe failed. Please enter a valid game ID");
                        break;
                    } else {
                        int gameID = Integer.parseInt(args[1]);
//                        System.out.println(gameID + " = game ID");
                        //default color to observe is white
                        color = ChessGame.TeamColor.WHITE;
                        gameUI(gameID, color);
                        break;
                    }
                default:
                    System.out.println("option not valid. please try again");

            }
        }

    }

    static void gameUI(int gameID, ChessGame.TeamColor color) {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean exit = false;
        System.out.println("welcome to the game!");
        if (color == ChessGame.TeamColor.BLACK) {
            printBlackBoard();
        } else if (color == ChessGame.TeamColor.WHITE) {
            printWhiteBoard();
        } else {
            System.out.println("color error");
            exit(0);
        }
        //loop here to continue the game

    }

    static void printBlackBoard() {
        BoardText blackBoard = new BoardText(true);
        blackBoard.printBoard();
    }

    static void printWhiteBoard() {
        BoardText whiteBoard = new BoardText(false);
        whiteBoard.printBoard();
    }

    static void printPreOptions() {
        System.out.print("help - display this help menu\n" +
                "login <username> <password> - to log in\n" +
                "register <username> <password> <email>\n" +
                "quit - exit chess\n");
    }
    static void printPostOptions() {
        System.out.print("help - display this help menu\n" +
                "logout - to log out\n" +
                "create <GAMENAME> - create a game with the name GAMENAME\n" +
                "list - list all games\n" +
                "join <ID> [WHITE|BLACK] - join the game with game id ID as color WHITE|BLACK\n" +
                "observe <ID> - observe the game with the game id ID\n");
    }

    static String facadeResponseHandler(String args[], String option) {
        try {
            switch (option) {
                case "register":
                    facade.register(args[1], args[2], args[3]);
                case "login":
                    facade.login(args[1], args[2]);
                case "logout":
                    facade.logout();
                case "create":
                    facade.createGame(args[1]);
                case "join":
                    facade.joinGame(ChessGame.TeamColor.valueOf(args[1]), Integer.parseInt(args[2]));
                case "list":
                    facade.listGames();
            }
//        } catch (ErrorResponse e) {
//            return e.toString();
        } catch (Exception e) {
            return e.toString();
        }
        return "";
    }
}