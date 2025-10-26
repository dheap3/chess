package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
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
    AuthDAO myAuthDAO = new MemoryAuthDAO();
    private UserService userService = new UserService(myAuthDAO);
    private GameService gameService = new GameService(myAuthDAO);

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
        gameService.clearDB();
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



        Map<Integer, Map<String, String>> createGameData = gameService.createGame(authToken, gameName);
        int createGameStatus = createGameData.entrySet().iterator().next().getKey();

        var res = new Gson().toJson(createGameData.entrySet().iterator().next().getValue());
        ctx.status(createGameStatus).result(res);
        if (createGameStatus == 200) {
            //if it's a success we need to send the gameID as an int
            String gameID = createGameData.entrySet().iterator().next().getValue().get("gameID");
            res = new Gson().toJson(Map.of("gameID", Integer.valueOf(gameID)));
            ctx.status(200).result(res);
        }

    }
    private void joinGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("Authorization");
        String playerColor = (String) req.get("playerColor");
        Double gameIDDouble = (Double) req.get("gameID");
        String username = "";

        Map<Integer, Map<String, String>> joinGameData = gameService.joinGame(authToken, playerColor, gameIDDouble);

        var res = new Gson().toJson(joinGameData.entrySet().iterator().next().getValue());
        ctx.status(joinGameData.entrySet().iterator().next().getKey()).result(res);
    }
    private void listGames(Context ctx) {
        String authToken = ctx.header("Authorization");
        Map<Integer, String> listGamesResponse = gameService.listGames(authToken);//returns json string because of the list

        var res = listGamesResponse.entrySet().iterator().next().getValue();
        ctx.status(listGamesResponse.entrySet().iterator().next().getKey()).result(res);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
