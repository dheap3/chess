package dataaccess;

import datamodel.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> auths = new HashMap<String, AuthData>();

    @Override
    public boolean addAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
        return true;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public boolean removeAuth(String authToken) {
        auths.remove(authToken);
        return true;
    }

    @Override
    public void clearDB() {
        auths.clear();
    }

    @Override
    public boolean dbContains(String authToken) {
        return auths.containsKey(authToken);
    }
}
