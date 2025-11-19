import chess.ChessGame;
import java.util.Scanner;
import static java.lang.System.exit;
import ui.BoardText;

public class Main {
    static public ServerFacade facade;

    public static void main(String[] args) {
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
        int port = 59802;
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
                    if (args.length != 3) {
                        System.out.println("Login failed. Please enter a valid USERNAME/PASSWORD");
                        break;
                    } else {
//                        System.out.println(args[1] + " = username");
//                        System.out.println(args[2] + " = password");

                        facade.login(args[1], args[2]);
                        postLoginUI();
                        break;
                    }
                case "register":
                    if (args.length != 4) {
                        System.out.println("Register failed, please enter a valid USERNAME/PASSWORD/EMAIL");
                        break;
                    }  else {
                        System.out.println(args[1] + " = username");
                        System.out.println(args[2] + " = password");
                        System.out.println(args[3] + " = email");
                        postLoginUI();
                        break;
                    }
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
        int gameID = 123;
        String gameName = "";
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
                    if (args.length != 2) {
                        System.out.println("Create failed. Please enter a valid GAMENAME");
                        break;
                    } else {
                        gameName = args[1];
                        System.out.println(gameName + " = gamename");
                        break;
                    }
                case "list":
                    System.out.println("heres all the games you need:");
                    break;
                case "join":
                    if (args.length != 3) {
                        System.out.println("Join failed. Please enter a valid game ID/COLOR");
                        break;
                    } else {
                        gameID = Integer.parseInt(args[1]);
                        if (args[2].equals("WHITE")) {
                            color = ChessGame.TeamColor.WHITE;
                        } else if (args[2].equals("BLACK")){
                            color = ChessGame.TeamColor.BLACK;
                        } else {
                            System.out.println("Join failed. Please enter a valid game ID/COLOR (COLOR must be all caps)");
                            break;
                        }
                        System.out.println(gameID + " = gameID");
                        System.out.println(color + " = color");
                        gameUI(gameID, color);
                        break;
                    }
                case "observe":
                    if (args.length != 2) {
                        System.out.println("Create failed. Please enter a valid game ID");
                        break;
                    } else {
                        gameID = Integer.parseInt(args[1]);
                        System.out.println(gameID + " = game ID");
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

    String facadeResponseHandler(String args[], String option) {
        try {
            switch (option) {
                case "register":
                    ;
                case "login":
                    ;
                case "logout":
                    ;
                case "create":
                    ;
                case "join":
                    ;
                case "list":
                    ;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return "";
    }
}