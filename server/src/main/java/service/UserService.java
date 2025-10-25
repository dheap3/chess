package service;

import DataModel.AuthData;
import DataModel.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserService {
//    private Map<String, UserData> users = new HashMap<String, UserData>();
//    private Map<String, AuthData> auths = new HashMap<String, AuthData>();

    public Map<String, Map<UserData, AuthData>> register(Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String email = (String) req.get("email");

        Map<String, Map<UserData, AuthData>> registerData = new HashMap<>();
        Map<UserData, AuthData> datas = new HashMap<>();
        AuthData myAuthData = createAuth(username);
        datas.put(createUser(username, password, email), myAuthData);
        registerData.put(username, datas);
        return registerData;
    }
    public AuthData login(Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");

        AuthData loginData = createAuth(username);
        return loginData;
    }
    public void logout() {

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
    }
    public AuthData getAuth(String authToken, Map<String, AuthData> auths) {
        //get the auth from the db
        if (auths.containsKey(authToken)) {
            var auth = auths.get(authToken);
            return auth;
        } else {
            return null;
        }
    }
    public AuthData createAuth(String username) {
        //create authToken
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, authToken);
        //add to db
        return auth;

}
