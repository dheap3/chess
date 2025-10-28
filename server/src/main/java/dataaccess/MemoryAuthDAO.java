package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> auths = new HashMap<String, AuthData>();

    @Override
    public void addAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void removeAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clearDB() {
        auths.clear();
    }

    @Override
    public boolean dbContains(String authToken) {
        return auths.containsKey(authToken);
    }

    @Override
    public ArrayList<AuthData> getAuths() {
        return new ArrayList<>(auths.values());
    }
}
