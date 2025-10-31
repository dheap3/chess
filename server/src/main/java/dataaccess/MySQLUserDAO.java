package dataaccess;

import datamodel.UserData;

import java.util.Collection;
import java.util.List;

public class MySQLUserDAO implements UserDAO {
    @Override
    public boolean addUser(UserData user) {
        return false;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public Collection<UserData> getUsers() {
        return List.of();
    }

    @Override
    public void clearDB() {

    }
}
