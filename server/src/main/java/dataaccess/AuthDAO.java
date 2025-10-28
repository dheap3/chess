package dataaccess;

import datamodel.AuthData;

import java.util.Collection;

public interface AuthDAO {
    void addAuth(AuthData auth);
    AuthData getAuth(String username);
    Collection<AuthData> getAuths();
    void removeAuth(String authToken);
    void clearDB();
    boolean dbContains(String authToken);
}
