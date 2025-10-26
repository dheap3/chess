package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datamodel.AuthData;
import datamodel.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserService {
    UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();

    public UserService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public Map<Integer, Map<String, String>> register(String username, String password, String email) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;

        UserData user = userDAO.getUser(username);
        //checks if the user sent valid data
        if (username == null || username.isBlank() ||
                password == null || password.isBlank() ||
                email == null || email.isBlank()) {
            statusString = Map.of("message", "Error: bad request");
            statusCode = 400;
            return Map.of(statusCode, statusString);
        }

        //checks if the user has already registered
        if (user != null) {
            statusString = Map.of("message", "Error: already taken");
            statusCode = 403;
            return Map.of(statusCode, statusString);
        }

        user = new UserData(username, password, email);
        //put user in db
        userDAO.addUser(user);

        AuthData myAuthData = createAuth(user.username());
        //put auth in db
        authDAO.addAuth(myAuthData);

        //success
        statusString = Map.of("username", myAuthData.username(), "authToken", myAuthData.authToken());
        statusCode = 200;
        return Map.of(statusCode, statusString);
    }
    public Map<Integer, Map<String, String>> login(String username, String password) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;

        UserData user = userDAO.getUser(username);
        //checks if the user sent valid data
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            statusString = Map.of("message", "Error: bad request");
            statusCode = 400;
            return Map.of(statusCode, statusString);
        }

        //check if the user is authorized (correct password)
        UserData correctData = getUser(username);
        if (correctData == null ||//cannot find data for the associated username (incorrect username)
                !password.equals(correctData.password())) {//the password given doesn't match the one stored
            statusString = Map.of("message", "Error: unauthorized");
            statusCode = 401;
            return Map.of(statusCode, statusString);
        }


        AuthData myAuthData = createAuth(user.username());
        //put auth in db
        authDAO.addAuth(myAuthData);

        //success
        statusString = Map.of("username", myAuthData.username(), "authToken", myAuthData.authToken());
        statusCode = 200;
        return Map.of(statusCode, statusString);


    }
    public Map<Integer, Map<String, String>> logout(String authToken) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;

        if (!authDAO.dbContains(authToken)) {
            statusString = Map.of("message", "Error: unauthorized");
            statusCode = 401;
            return Map.of(statusCode, statusString);
        }
        removeAuth(authToken);

        //success
        statusString = Map.of();
        statusCode = 200;
        return Map.of(statusCode, statusString);
    }

    public UserData getUser(String username) {
        return userDAO.getUser(username);
    }
    public AuthData createAuth(String username) {
        //create authToken
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, authToken);
        //add to db
        authDAO.addAuth(auth);
        return auth;
    }
    public boolean removeAuth(String authToken) {
        authDAO.removeAuth(authToken);
        return true;
    }
    public void clearDB() {
        userDAO.clearDB();
        authDAO.clearDB();
    }
}
