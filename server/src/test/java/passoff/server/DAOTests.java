package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MySQLAuthDAO;
import datamodel.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class DAOTests {
    //test authDAO
    //addAuth
    @Test
    public void testAddAuthSuccess() throws Exception {
        String testUsername = "testUsername";
        String testAuthToken = "testAuthToken";
        AuthData auth = new AuthData(testUsername, testAuthToken);
        AuthDAO myAuthDAO = new MemoryAuthDAO();

        Assertions.assertTrue(myAuthDAO.addAuth(auth));
    }
    @Test
    public void testAddAuthFail() throws Exception {
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        Assertions.assertFalse(myAuthDAO.addAuth(null));
    }
    //getAuth
    @Test
    public void testGetAuthSuccess() throws Exception {
        String testUsername = "testUsername";
        String testAuthToken = "testAuthToken";
        AuthData auth = new AuthData(testUsername, testAuthToken);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth);

        Assertions.assertEquals(auth, myAuthDAO.getAuth(testAuthToken));
    }
    @Test
    public void testGetAuthFail() throws Exception {
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        Assertions.assertNull(myAuthDAO.getAuth(null));
    }
    //getAuths
    @Test
    public void testGetAuthsSuccess() throws Exception {
        String testUsername1 = "testUsername1";
        String testAuthToken1 = "testAuthToken1";
        AuthData auth1 = new AuthData(testUsername1, testAuthToken1);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth1);
        String testUsername2 = "testUsername2";
        String testAuthToken2 = "testAuthToken2";
        AuthData auth2 = new AuthData(testUsername2, testAuthToken2);
        myAuthDAO.addAuth(auth2);
        ArrayList<AuthData> optimalAuths = new ArrayList<>(List.of());
        optimalAuths.add(auth1);
        optimalAuths.add(auth2);

        ArrayList<AuthData> auths = (ArrayList<AuthData>) myAuthDAO.getAuths();
        optimalAuths.sort(Comparator.comparing(AuthData::authToken));
        auths.sort(Comparator.comparing(AuthData::authToken));

        Assertions.assertEquals(optimalAuths, auths);
    }
    @Test
    public void testGetAuthsFail() throws Exception {
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        Assertions.assertEquals(List.of(), myAuthDAO.getAuths());
    }
    //removeAuth
    @Test
    public void testRemoveAuthSuccess() throws Exception {
        String testUsername1 = "testUsername1";
        String testAuthToken1 = "testAuthToken1";
        AuthData auth1 = new AuthData(testUsername1, testAuthToken1);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth1);

        myAuthDAO.removeAuth(testAuthToken1);

        ArrayList<AuthData> auths = (ArrayList<AuthData>) myAuthDAO.getAuths();
        Assertions.assertEquals(List.of(), auths);
    }
    @Test
    public void testRemoveAuthFail() throws Exception {
        String testUsername1 = "testUsername1";
        String testAuthToken1 = "testAuthToken1";
        AuthData auth1 = new AuthData(testUsername1, testAuthToken1);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth1);
        ArrayList<AuthData> auths = new ArrayList<>(List.of());
        auths.add(auth1);
        myAuthDAO.removeAuth(testUsername1);
        Assertions.assertEquals(auths, myAuthDAO.getAuths());
    }
    //clearDB
    @Test
    public void clearDBSuccess() {
        String testUsername1 = "testUsername1";
        String testAuthToken1 = "testAuthToken1";
        AuthData auth1 = new AuthData(testUsername1, testAuthToken1);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth1);
        String testUsername2 = "testUsername2";
        String testAuthToken2 = "testAuthToken2";
        AuthData auth2 = new AuthData(testUsername2, testAuthToken2);
        myAuthDAO.addAuth(auth2);

        myAuthDAO.clearDB();
        Assertions.assertEquals(List.of(), myAuthDAO.getAuths());
    }

    //dbContains
    @Test
    public void dbContainsSuccess() {
        String testUsername1 = "testUsername1";
        String testAuthToken1 = "testAuthToken1";
        AuthData auth1 = new AuthData(testUsername1, testAuthToken1);
        AuthDAO myAuthDAO = new MemoryAuthDAO();
        myAuthDAO.addAuth(auth1);

        Assertions.assertTrue(myAuthDAO.dbContains(testAuthToken1));
    }
    @Test
    public void dbContainsFail() {
        String testAuthToken1 = "testAuthToken1";
        AuthDAO myAuthDAO = new MemoryAuthDAO();

        Assertions.assertFalse(myAuthDAO.dbContains(testAuthToken1));
    }

    //test userDAO
    //addUser
    //getUser
    //getUsers
    //clearDB

    //test gameDAO
    //addGame
    //getGame
    //getGames
    //updateGame
    //clearDB

}
