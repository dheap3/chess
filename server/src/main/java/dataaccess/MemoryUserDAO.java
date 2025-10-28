package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.*;

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
    public ArrayList<UserData> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clearDB() {
        users.clear();
    }
}
