package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import service.userService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private userService userService = new userService();

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        server.delete("db", ctx ->ctx.result("{}"));
        server.post("user", this::register); //same as server.post("user", ctx -> register(ctx));

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        userService userService = new userService();
        var req = serializer.fromJson(ctx.body(), Map.class);
        req.put("authToken", "cow");
        userService.register(req);
//        ctx.status(200).result(AuthData);

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
