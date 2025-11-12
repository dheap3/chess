import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to CS 240 Chess! ♕\nEnter one of the following options:");
        preloginUI();

    }

    static void preloginUI() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String input;
        printPreOptions();
        System.out.print("[LOGGED OUT] ->> ");
        while (!exit) {
            input = scanner.nextLine();
            input = input.toLowerCase();
            switch (input) {
                case "help":
                    printPreOptions();
                    System.out.print("[LOGGED OUT] ->> ");
                    break;
                case "quit":
//                    System.out.println("bye");
                    exit = true;
                    break;
                case "login":
                    //Prompts the user to input login information. Calls the server login API to log in the user. When successfully logged in, the client should transition to the Postlogin UI.
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
        System.out.println("you are logged in!");
        printPostOptions();
        System.out.print("[LOGGED IN] ->> ");
        while (!exit) {
            input = scanner.nextLine();
            input = input.toLowerCase();
            switch (input) {
                case "help":
                    printPostOptions();
                    System.out.print("[LOGGED IN] ->> ");
                    break;
                case "logout":
                    //Logs out the user. Calls the server logout API to log out the user. After logging out with the server, the client should transition to the Prelogin UI.
                    exit = true;
//                    preloginUI();
                    break;
                case "create":
                    //Allows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.
                    System.out.println("create a game that pleases you");
                    break;
                case "list":
                    //Lists all the games that currently exist on the server. Calls the server list API to get all the game data, and displays the games in a numbered list, including the game name and players (not observers) in the game. The numbering for the list should be independent of the game IDs and should start at 1.
                    System.out.println("heres all the games you need:");
                    break;
                case "play":
                    //Allows the user to specify which game they want to join and what color they want to play. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to join the user to the game.
                    System.out.println("be a kid, play a game!");
                    break;
                case "observe":
                    //Allows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Additional functionality will be added in Phase 6.
                    System.out.println("be an adult, watch the kids play");
                    break;
                default:
                    System.out.println("option not valid. please try again");
                    System.out.print("[LOGGED IN] ->> ");

            }
        }

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