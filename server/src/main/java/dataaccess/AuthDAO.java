package dataaccess;

import datamodel.AuthData;

public interface AuthDAO {
    boolean addAuth(AuthData auth);
    AuthData getAuth(String username);
    boolean removeAuth(String authToken);
    void clearDB();
    boolean dbContains(String authToken);
}
