package server;

import com.google.gson.Gson;
import dataModel.AuthData;
import dataModel.GameData;
import dataModel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.gameService;
import service.userService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin server;
    private userService userService = new userService();
    private gameService gameService = new gameService();

    //move these fake db too mysql later and the methods into the service classes
    private Map<String, UserData> users = new HashMap<String, UserData>();
    private Map<String, AuthData> auths = new HashMap<String, AuthData>();
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
        users.clear();
        auths.clear();
        games.clear();
        var res = new Gson().toJson(Map.of());
        ctx.status(200).result(res);
    }
    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        Map<String, Map<UserData, AuthData>> registerData = userService.register(req);



        //checks if the user sent valid data
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String email = (String) req.get("email");
        if (username == null || username.isBlank() ||
                password == null || password.isBlank() ||
                email == null || email.isBlank()) {
            var res = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400).result(res);
            return;
        }

        UserData user = userService.getUser((String) req.get("username"), users);

        //checks if the user has already registered
        if (user != null) {
            var res = new Gson().toJson(Map.of("message", "Error: already taken"));
            ctx.status(403).result(res);
            return;
        }

        //add userData and Authdata to db
        //should only hold one set in each map for register data
        users.put((String) req.get("username"), registerData.get(req.get("username")).keySet().iterator().next());
        auths.put((String) req.get("username"), registerData.get(req.get("username")).values().iterator().next());

        //reset our user to the appropriate value
        user = userService.getUser((String) req.get("username"), users);

        AuthData auth = userService.getAuth(user.getUsername(), auths);
//        if (auth == null) {
//            var res = new Gson().toJson(Map.of("username", "", "authToken", ""));
//            ctx.status(500).result(res);
//            return;
//        }


        var res = new Gson().toJson(Map.of("username", auth.getUsername(), "authToken", auth.getAuthToken()));
        ctx.status(200).result(res);
//        ctx.status(200).result({ "username":auth.getUsername(), "authToken":auth.getAuthToken() });
    }
    private void login(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);

        //checks if the user sent valid data
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            var res = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400).result(res);
            return;
        }

        //check if the user is authorized (correct password)
        UserData correctData = users.get(username);
        if (correctData == null ||//cannot find data for the associated username (incorrect username)
                !password.equals(correctData.getPassword())) {//the password given doesn't match the one stored
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }

        AuthData auth = userService.login(req);
        auths.put(username, auth);

        var res = new Gson().toJson(Map.of("username", auth.getUsername(), "authToken", auth.getAuthToken()));
        ctx.status(200).result(res);
//        ctx.status(200).result({ "username":auth.getUsername(), "authToken":auth.getAuthToken() });
    }
    private void logout(Context ctx) {
        if (auths.isEmpty()) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }
        auths.clear();

        var res = new Gson().toJson(Map.of());//empty JSON object
        ctx.status(200).result(res);
    }
    private void createGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("Authorization");
        String gameName = (String) req.get("gameName");

        //checks if the user sent valid data
        if (authToken == null || authToken.isBlank()) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }
        if (gameName == null || gameName.isBlank()) {
            var res = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400).result(res);
            return;
        }

        //check if the authToken exists in db
        boolean found = false;
        for (AuthData authData : auths.values()) {
            if (authToken.equals(authData.getAuthToken())) {
                found = true;
                break;
            }
        }
        if (!found) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
            return;
        }

        GameData myGamePackage = gameService.createGame(gameName);
        Integer gameID = myGamePackage.getGameID();
        games.put(gameID, myGamePackage);

        var res = new Gson().toJson(Map.of("gameID", gameID));
        ctx.status(200).result(res);
    }
    private void listGames(Context ctx) {
//        System.out.println("==== REQUEST CONTEXT DUMP ====");
//        System.out.println("Method: " + ctx.method());
//        System.out.println("Path: " + ctx.path());
//        System.out.println("Headers: " + ctx.headerMap());
//        System.out.println("Query params: " + ctx.queryParamMap());
//        System.out.println("Body: " + ctx.body());
//        System.out.println("==============================");

        String authToken = ctx.header("Authorization");

        //check if the authToken exists in db
        boolean found = false;
        for (AuthData authData : auths.values()) {
            if (authToken.equals(authData.getAuthToken())) {
                found = true;
                break;
            }
        }
        if (!found) {
            var res = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            ctx.status(401).result(res);
        }
        ArrayList<GameData> gamesList = gameService.listGames();
        var res = new Gson().toJson(Map.of("games", gamesList));
        ctx.status(200).result(res);
    }
    private void joinGame(Context ctx) {}

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
