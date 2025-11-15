import chess.ChessGame;
import java.util.Scanner;
import static java.lang.System.exit;
import ui.BoardText;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
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
                    break;
                case "login":
                    if (args.length != 3) {
                        System.out.println("[LOGGED OUT] ->> Login failed. Please enter a valid USERNAME/PASSWORD");
                        break;
                    } else {
                        System.out.println(args[1] + " = username");
                        System.out.println(args[2] + " = password");
                        //Prompts the user to input login information. Calls the server login API to log in the user. When successfully logged in, the client should transition to the Postlogin UI.
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
                        //Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
                        postLoginUI();
                        break;
                    }
                default:
                    System.out.println("option not valid. please try again");
            }

        }
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
                    //Logs out the user. Calls the server logout API to log out the user. After logging out with the server, the client should transition to the Prelogin UI.
                    exit = true;
                    break;
                case "create":
                    if (args.length != 2) {
                        System.out.println("Create failed. Please enter a valid GAMENAME");
                        break;
                    } else {
                        gameName = args[1];
                        System.out.println(gameName + " = gamename");
                        //Allows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.
                        break;
                    }
                case "list":
                    //Lists all the games that currently exist on the server. Calls the server list API to get all the game data, and displays the games in a numbered list, including the game name and players (not observers) in the game. The numbering for the list should be independent of the game IDs and should start at 1.
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
                        //Allows the user to specify which game they want to join and what color they want to play. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to join the user to the game.
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
                        //Allows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Additional functionality will be added in Phase 6.
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
}