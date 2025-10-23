package service;

import dataModel.AuthData;
import dataModel.UserData;

import java.util.Map;

public class userService {
    public void register(Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String email = (String) req.get("email");

        if (getUser(username) != null) {
            //fix exception here
//            Exception AlreadyTakenException = null;
//            throw AlreadyTakenException;
        }
        createUser(username, password, email);
        CreateAuth(username, password, email);

    }
    public UserData getUser(String username) {
        //get the user from the db

        //edit this
        var user = new UserData(username, "pass", "gmail");

        return user;
//        return null;
    }
    public boolean createUser(String username, String password, String email) {
        //create user
        var user = new UserData(username, password, email);
        //add to db
        return true;
    }
    public AuthData getAuth(String username) {
        //get the auth from the db
        AuthData auth = new AuthData(username, "asdf");
        return auth;
//        return null;
    }
    public boolean CreateAuth(String username, String password, String email) {
        var authToken = password + ":" + email;
        AuthData auth = new AuthData(username, authToken);
        //add to db
        return true;
    }

}
