package service;

import dataModel.AuthData;
import dataModel.UserData;

import java.util.HashMap;
import java.util.Map;

public class userService {
//    private Map<String, UserData> users = new HashMap<String, UserData>();
//    private Map<String, AuthData> auths = new HashMap<String, AuthData>();

    public void register(Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String email = (String) req.get("email");

//        if (getUser(username) != null) {
//            //fix exception here
////            Exception AlreadyTakenException = null;
////            throw AlreadyTakenException;
//        }
        createUser(username, password, email);
        CreateAuth(username, password, email);
//        return getAuth(username);
    }
    public UserData getUser(String username, Map<String, UserData> users) {
        //get the user from the db
        if (users.containsKey(username)) {
            var user = users.get(username);
            return user;
        } else {
            return null;
        }
    }
    public UserData createUser(String username, String password, String email) {
        //create user
        var user = new UserData(username, password, email);
        //add to db
        return user;
//        users.put(username, user);
//        if (users.containsKey(username)) {
//            return true;
//        } else {
//            return false;
//        }
    }
    public AuthData getAuth(String username, Map<String, AuthData> auths) {
        //get the auth from the db
        if (auths.containsKey(username)) {
            var auth = auths.get(username);
            return auth;
        } else {
            return null;
        }
    }
    public AuthData CreateAuth(String username, String password, String email) {
        //create authToken
        var authToken = password + ":" + email;
        AuthData auth = new AuthData(username, authToken);
        //add to db
        return auth;
//        auths.put(username, auth);
//        if (auths.containsKey(username)) {
//            return true;
//        } else {
//            return false;
//        }
    }

}
