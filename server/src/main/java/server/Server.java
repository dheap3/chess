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
        Map<String, Map<UserData, AuthData>> registerData = userService.register(req);
        //should only hold one set in each map for register data
        users.put((String) req.get("username"), registerData.get(req.get("username")).keySet().iterator().next());
        auths.put((String) req.get("username"), registerData.get(req.get("username")).values().iterator().next());

        UserData user = userService.getUser((String) req.get("username"), users);
        if (user == null) {
            var res = new Gson().toJson(Map.of("username", "", "authToken", ""));
            ctx.status(500).result(res);
            return;
        }
        users.put(user.getUsername(), user);
        AuthData auth = userService.getAuth(user.getUsername(), auths);
        if (auth == null) {
            var res = new Gson().toJson(Map.of("username", "", "authToken", ""));
            ctx.status(500).result(res);
            return;
        }
        auths.put(auth.getUsername(), auth);

        var res = new Gson().toJson(Map.of("username", auth.getUsername(), "authToken", auth.getAuthToken()));
        ctx.status(200).result(res);
//        ctx.status(200).result({ "username":auth.getUsername(), "authToken":auth.getAuthToken() });
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
