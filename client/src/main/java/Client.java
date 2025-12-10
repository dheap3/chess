import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import ui.BoardText;
import ui.ServerFacade;
import ui.ServerObserver;
import ui.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class Client implements ServerObserver {
    static public ServerFacade serverFacade;
    static public WebSocketFacade wsFacade = null;
    static public String authToken = "";
    static ServerObserver observer;
    static public BoardText printer = null;
    static public ChessGame.TeamColor currentColor = null;
    static public String user = null;

    public static void main(String[] args) {
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
        int port = 50143;
        String url = "http://localhost:" + port;
        serverFacade = new ServerFacade(port);
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
                    try {
                        AuthData auth = serverFacade.login(args[1], args[2]);
                        authToken = auth.authToken();
                        user = args[1];
                    } catch (Exception e) {
                        printError(e, "login");
                        break;
                    }
                    postLoginUI();
                    break;
                case "register":
                    try {
                        AuthData auth = serverFacade.register(args[1], args[2], args[3]);
                        authToken = auth.authToken();
                        user = args[1];
                    } catch (Exception e) {
                        printError(e, "register");
                        break;
                    }
                    postLoginUI();
                    break;
                case "admin":
                    System.out.println("You can now clear the database. Only use for testing purposes. There is no going back");
                    System.out.println("Would you like to clear the database? (Y/N)\n:");
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("y")) {
                        serverFacade.clear();
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
        String line, input;
        ChessGame.TeamColor color = null;
        Map<Integer, Integer> numGameID = null;
        System.out.println("You are logged in!");
        printPostOptions();
        while (!exit) {
            System.out.print("[LOGGED IN] ->> ");
            line = scanner.nextLine();
            var args = line.split(" ");
            input = args[0].toLowerCase();

            //update the numGameID list consistently
            numGameID = new HashMap<Integer, Integer>();
            var list = serverFacade.listGames().getGames();
            for (int i = 0; i < list.size(); i++) {
                var game = list.get(i);
                int num = i + 1;
                numGameID.put(num, game.gameID());
            }

            switch (input) {
                case "help":
                    printPostOptions();
                    break;
                case "logout":
                    exit = true;
                    user = null;
                    break;
                case "create":
                    try {
                        String gameName = "";
                        for (int i = 1; i < args.length; i++) {
                            gameName += args[i];
                            String valAdded = ((i != (args.length - 1)) ? (" ") : (""));
                            gameName += valAdded;
                        }
                        var game = serverFacade.createGame(gameName);
                        var num =  numGameID.size() + 1;
                        numGameID.put(num, game.gameID());
                        System.out.println("Created game " + num + ". " + gameName);
                        //automatically list the games here?//could add a feature here later
                    } catch (Exception e) {
                        printError(e, "create");
                        break;
                    }
                    break;
                case "list":
                    //print out the list of games
                    try {
                        list = serverFacade.listGames().getGames();
                        System.out.println("# : Game Name : Users In Game WHITE|BLACK");
                        for (int i = 0; i < list.size(); i++) {
                            var game = list.get(i);
                            int num = i + 1;
                            System.out.print(num + ". : " + game.gameName() + " : ");
                            String whiteUser = (game.whiteUsername() != null) ? game.whiteUsername() : "(No User)";
                            String blackUser = (game.blackUsername() != null) ? game.blackUsername() : "(No User)";
                            System.out.print(whiteUser + " | " + blackUser + "\n");
                        }
                    } catch (Exception e) {
                        printError(e, "list");
                        break;
                    }
                    break;
                case "join":
                    try {
                        int num = Integer.parseInt(args[1]);
                        int gameID = numGameID.get(num);
                        if (args[2].equalsIgnoreCase("WHITE")) {
                            color = ChessGame.TeamColor.WHITE;
                        } else if (args[2].equalsIgnoreCase("BLACK")) {
                            color = ChessGame.TeamColor.BLACK;
                        } else {
                            System.out.println("Join failed. Please enter a valid game ID/COLOR");
                            break;
                        }
                        serverFacade.joinGame(color, gameID);
                        String url = ServerFacade.serverUrl;
//                        System.out.println(url);
                        //Open a WebSocket connection with the server
                        wsFacade = new WebSocketFacade(url, observer);
                        //Send a CONNECT WebSocket message to the server.
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, user, currentColor.toString());
                        wsFacade.send(command);
                        gameUI(gameID, color);
                    } catch (Exception e) {
                        printError(e, "join");
                        break;
                    }
                    break;
                case "observe":
                    try {
                        int num = Integer.parseInt(args[1]);
                        int gameID = numGameID.get(num);
                        color = ChessGame.TeamColor.WHITE;//default color to observe is white

                        String url = ServerFacade.serverUrl;
                        //Open a WebSocket connection with the server
                        wsFacade = new WebSocketFacade(url, observer);
                        //Send a CONNECT WebSocket message to the server.
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, user, "observer");
                        wsFacade.send(command);
                        gameUI(gameID, color);
                        break;
                    } catch (Exception e) {
                        printError(e, "observe");
                    }
                default:
                    System.out.println("option not valid. please try again");

            }
        }
    }

    static void gameUI(int gameID, ChessGame.TeamColor color) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean exit = false;
        String gameName = "JOINED";
        GameData data = serverFacade.listGames().getGames().get(gameID);
        ChessGame game = data.game();
        currentColor = color;
        printer = new BoardText(game, color);

        System.out.println("Joined Successfully!");
        printGameOptions();
        while (!exit) {
            sleep(1000);
            System.out.print("[GAME " + gameName + "] ->> ");
            input = scanner.nextLine();
            var args = input.split(" ");
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    printGameOptions();
                }
                case "leave" -> {
                    //need to leave the game
                    //if i leave i can rejoin as a different player
                    UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, user, currentColor.toString());
                    wsFacade.send(command);
                    currentColor = null;
                    exit = true;
                }
                case "redraw" -> {
                    if (color == ChessGame.TeamColor.BLACK) {
                        printBlackBoard();
                    } else if (color == ChessGame.TeamColor.WHITE) {
                        printWhiteBoard();
                    } else {
                        System.out.println("color error");
                        exit(0);
                    }
                }
                case "move" -> {
                    UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, user, currentColor.toString());
                    wsFacade.send(command);
//                    wsFacade.onText()
                    //need more code here?
                    printGameOptions();
                }
                case "resign" -> {
                    System.out.println("Are you sure? y/n");
                    String answer = scanner.nextLine();
                    if (answer.toLowerCase().equals("y")) {
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, user, currentColor.toString());
                        wsFacade.send(command);
                        System.out.println("You forfeit. Game Over!");
                        //end game
                    } else {
                        //do nothing and return to normal input
                    }
                }
                case "moves" -> {
                    //highlight the legal moves
                    printGameOptions();
                }
                default -> {
                    System.out.println("option not valid. please try again");
                }

            }
        }
    }

    static void printBlackBoard() {
        BoardText blackBoard = new BoardText(new ChessGame(), ChessGame.TeamColor.BLACK);
        blackBoard.printBoard();
    }

    static void printWhiteBoard() {
        BoardText whiteBoard = new BoardText(new ChessGame(), ChessGame.TeamColor.WHITE);
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
                "join <GAMENUM> [WHITE|BLACK] - join the game with game number GAMENUM as color WHITE|BLACK\n" +
                "observe <GAMENUM> - observe the game with the game number GAMENUM\n");
    }

    static void printGameOptions() {
        System.out.print("help - display this help menu\n" +
                "leave - leave the current game\n" +
                "redraw - redraw the chess board\n" +
                "move <MOVE> - make a move with notation <asdf>\n" +
                "resign - forfeit the current game\n" +//doesn't leave the game
                "moves <PIECE> - highlight the legal moves for PIECE\n");
    }

    static void printError(Exception e, String method) {
        switch (method) {
            case "create":
                if (e.getMessage().startsWith("400")) {
                    System.out.println("Create failed. Invalid GAMENAME");
                } else if (e.getMessage().startsWith("401")) {
                    System.out.println("Create failed. You are not authorized for that");
                } else {
                    System.out.println("Create failed. Error: " + e.getMessage());
                }
                break;
            case "list":
//                System.out.println("List failed. Talk to the administrator :(");
                if (e.getMessage().startsWith("401")) {
                    System.out.println("List failed. You are not authorized for that");
                } else {
                    System.out.println("List failed. Error: " + e.getMessage());
                }
                break;
            case "join":
//                System.out.println("Join failed. Please enter a valid GAMENUM/COLOR");
                if (e instanceof java.lang.IndexOutOfBoundsException ) {
                    if (e.getMessage().endsWith("1")) {//length of the args should be 3
                        System.out.println("Join failed. Please enter a valid GAMENUM/COLOR");
                    } else if (e.getMessage().endsWith("2")) {
                        System.out.println("Join failed. Please enter a valid COLOR");
                    }
                } else if (e.getMessage().startsWith("400")) {
                    System.out.println("Join failed. Invalid GAMENUM");
                } else if (e.getMessage().startsWith("401")) {
                    System.out.println("Join failed. You are not authorized for that");
                } else if (e.getMessage().startsWith("403")) {
                    System.out.println("Join failed. COLOR already taken");
                } else if (e instanceof java.lang.NullPointerException ) {
                    System.out.println("Join failed. Game does not exist");
                } else if (e instanceof java.lang.NumberFormatException ) {
                    System.out.println("Join failed. Invalid GAMENUM");
                } else {
                    System.out.println("Join failed. Error: " + e.getMessage());
                }
                break;
            case "observe":
                if (e instanceof java.lang.IndexOutOfBoundsException ) {
                    if (e.getMessage().endsWith("1")) {//length of the args should be 2
                        System.out.println("Observe failed. Please enter a valid GAMENUM");
                    }
                } else if (e instanceof java.lang.NullPointerException ) {
                    System.out.println("Observe failed. Game does not exist");
                } else {
                    System.out.println("Observe failed. Error: " + e.getMessage());
                }
                break;
            case "login":
                if (e instanceof java.lang.IndexOutOfBoundsException ) {
                    if (e.getMessage().endsWith("1")) {//length of the args should be 3
                        System.out.println("Login failed. Please enter a valid USERNAME/PASSWORD");
                    } else if (e.getMessage().endsWith("2")) {
                        System.out.println("Login failed. Please enter a valid PASSWORD");
                    }
                } else if (e instanceof java.lang.RuntimeException ) {
                    if (e.getMessage().startsWith("401")) {
                        System.out.println("Login failed. Invalid credentials");
                    }
                } else {
                    System.out.println("Login failed. Error: " + e.getMessage());
                }
                break;
            case "register":
                if (e instanceof java.lang.IndexOutOfBoundsException ) {
                    if (e.getMessage().endsWith("1")) {//length of the args should be 4
                        System.out.println("Register failed, please enter a valid USERNAME/PASSWORD/EMAIL");
                    } else if (e.getMessage().endsWith("2")) {
                        System.out.println("Register failed, please enter a valid PASSWORD/EMAIL");
                    } else if (e.getMessage().endsWith("3")) {
                        System.out.println("Register failed, please enter a valid EMAIL");
                    }
                } else if (e instanceof java.lang.RuntimeException ) {
                    if (e.getMessage().startsWith("403")) {
                        System.out.println("Register failed. Username already taken");
                    }
                } else {
                    System.out.println("Register failed. Error: " + e.getMessage());
                }
                break;
            default:
                System.out.println("option not valid. please try again");
                break;
        }
//        System.out.println("error: " + e.getMessage());
    }

    @Override
    public void notify(ServerMessage data) {
        System.out.println("Received WS message: " + data);
        String dataString = data.toString();
        ServerMessage msg = new Gson().fromJson(dataString, ServerMessage.class);
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                ChessGame game = data.getGame();
                printer = new BoardText(game, currentColor);
                printer.printBoard();

            }
            case ERROR -> {

            }
            case NOTIFICATION -> {

            }
        }
    }
}