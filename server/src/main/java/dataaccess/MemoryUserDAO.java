package dataaccess;

import datamodel.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private Map<String, UserData> users = new HashMap<String, UserData>();

    @Override
    public boolean addUser(UserData user) {
        users.put(user.username(), user);
        return true;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clearDB() {
        users.clear();
    }
}
