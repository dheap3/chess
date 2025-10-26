package server;

import com.google.gson.Gson;
import datamodel.GameData;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin server;
    private UserService userService = new UserService();
    private GameService gameService = new GameService();

    //move these fake db too mysql later and the methods into the service classes

    private Map<Integer, GameData> games = new HashMap<Integer, GameData>();


    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
//        server.delete("db", ctx ->ctx.result("{}"));
        server.delete("db", this::clear);
        server.post("user", this::register); //same as server.post("user", ctx -> register(ctx));
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

    }
    //helper functions for http endpoints
    private void clear(Context ctx) {
        userService.clearDB();
//        gameservice.clearDB();
        games.clear();
        var res = new Gson().toJson(Map.of());
        ctx.status(200).result(res);
    }
    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String email = (String) req.get("email");

        Map<Integer, Map<String, String>> registerData = userService.register(username, password, email);

        var res = new Gson().toJson(registerData.entrySet().iterator().next().getValue());
        ctx.status(registerData.entrySet().iterator().next().getKey()).result(res);
    }
    private void login(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String username = (String) req.get("username");
        String password = (String) req.get("password");

        Map<Integer, Map<String, String>> registerData = userService.login(username, password);

        var res = new Gson().toJson(registerData.entrySet().iterator().next().getValue());
        ctx.status(registerData.entrySet().iterator().next().getKey()).result(res);
    }
    private void logout(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("Authorization");

        Map<Integer, Map<String, String>> registerData = userService.logout(authToken);

        var res = new Gson().toJson(registerData.entrySet().iterator().next().getValue());
        ctx.status(registerData.entrySet().iterator().next().getKey()).result(res);

    }
    private void createGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("Authorization");
        String gameName = (String) req.get("gameName");

        //checks if the user sent valid data
        if (gameName == null || gameName.isBlank()) {
            var res = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400).result(res);
            return;
        }

        //check if the authToken exists in db
        if (!userService.getAuth(authToken).authToken().equals(authToken)) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }

        GameData myGamePackage = gameService.createGame(gameName);
        Integer gameID = myGamePackage.gameID();
        games.put(gameID, myGamePackage);

        var res = new Gson().toJson(Map.of("gameID", gameID));
        ctx.status(200).result(res);
    }
    private void joinGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("Authorization");
        String playerColor = (String) req.get("playerColor");
        Double gameIDDouble = (Double) req.get("gameID");
        String username = "";

        //checks if the user sent valid data
        if (playerColor == null || playerColor.isBlank() ||
                (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) ||
                gameIDDouble == null) {
            var res = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400).result(res);
            return;
        }
        //nasty double conversion
        double dub = gameIDDouble;
        int gameID = (int) dub;

        //check if the authToken exists in db
        if (!userService.getAuth(authToken).authToken().equals(authToken)) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }
//        username = auths.get(authToken).username();

        GameData gamePackage = games.get(gameID);
        //verify that the game is accepting a user of the color given ie: not full
        if ((playerColor.equals("WHITE") && gamePackage.whiteUsername() != null) ||
                (playerColor.equals("BLACK") && gamePackage.blackUsername() != null)) {
            var res = new Gson().toJson(Map.of("message", "Error: already taken"));
            ctx.status(403).result(res);
            return;
        }
        games.put(gameID, gameService.joinGame(username, playerColor, gamePackage));


        var res = new Gson().toJson(Map.of());
        ctx.status(200).result(res);
    }
    private void listGames(Context ctx) {
        String authToken = ctx.header("Authorization");

        //check if the authToken exists in db
        if (!userService.getAuth(authToken).authToken().equals(authToken)) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }
//        ArrayList<GameData> gamesList = gameService.listGames();
        ArrayList<GameData> gamesList = new ArrayList<>(games.values());
        var res = new Gson().toJson(Map.of("games", gamesList));
        ctx.status(200).result(res);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
