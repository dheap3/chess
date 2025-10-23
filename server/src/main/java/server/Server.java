package server;

import com.google.gson.Gson;
import dataModel.AuthData;
import dataModel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.userService;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin server;
    private userService userService = new userService();
    private Map<String, UserData> users = new HashMap<String, UserData>();
    private Map<String, AuthData> auths = new HashMap<String, AuthData>();


    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        server.delete("db", ctx ->ctx.result("{}"));
        server.post("user", this::register); //same as server.post("user", ctx -> register(ctx));

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        userService.register(req);

        UserData user = userService.getUser((String) req.get("username"), users);
        users.put(user.getUsername(), user);
        AuthData auth = userService.CreateAuth(user.getUsername(), user.getPassword(), user.getEmail());
        auths.put(auth.getUsername(), auth);


    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
