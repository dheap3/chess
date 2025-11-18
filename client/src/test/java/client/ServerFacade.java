package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import datamodel.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = "";

    public ServerFacade(int port) {
        String url = "http://localhost:" + port;
        serverUrl = url;
    }

    public void clear(){
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }
    public AuthData register(String username, String password, String email) {
        UserData user = new UserData(username, password, email);
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        //if it returned an auth token we need to update our authToken
        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
        if (obj.get("authToken") != null) {
            authToken = obj.get("authToken").getAsString();
        }
        return handleResponse(response, AuthData.class);
    }
    public AuthData login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }
    public void logout() {}
    public GameData createGame(GameData game) {
        return game;
    }
    public GameData joinGame(GameData game) {
        return game;
    }
    public GameData listGames(GameData game) {
        return game;
    }

    public void deletePet(int id){
        var path = String.format("/pet/%s", id);
        var request = buildRequest("DELETE", path, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

//    public PetList listPets(){
//        var request = buildRequest("GET", "/pet", null);
//        var response = sendRequest(request);
//        return handleResponse(response, PetList.class);
//    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            return null;
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass){
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                ErrorResponse error = new ErrorResponse(status, body);
                throw new RuntimeException(error.toString());
            } else {
                ErrorResponse error = new ErrorResponse(status, "unknown error");
                throw new RuntimeException(error.toString());
            }
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}