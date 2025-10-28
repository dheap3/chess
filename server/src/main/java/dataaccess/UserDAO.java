package dataaccess;

import datamodel.UserData;

import java.util.Collection;

public interface UserDAO {
    boolean addUser(UserData user);
    UserData getUser(String username);
    Collection<UserData> getUsers();
    void clearDB();
}
