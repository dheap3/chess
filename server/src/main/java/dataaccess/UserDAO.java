package dataaccess;

import datamodel.UserData;

public interface UserDAO {
    boolean addUser(UserData user);
    UserData getUser(String username);
    void clearDB();
}
