import chess.ChessGame;
import dataaccess.*;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class DAOTests {
    /// clean up these tests so they are consistent


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
    public void clearAuthDBSuccess() {
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
    @Test
    public void testAddUserSuccess() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail";
        UserData user = new UserData(testUsername, testPassword, testEmail);
        UserDAO myUserDAO = new MemoryUserDAO();

        Assertions.assertTrue(myUserDAO.addUser(user));
    }
    @Test
    public void testAddUserFail() throws Exception {
        UserDAO myUserDAO = new MemoryUserDAO();
        Assertions.assertFalse(myUserDAO.addUser(null));
    }
    //getUser
    @Test
    public void testGetUserSuccess() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail";
        UserData user = new UserData(testUsername, testPassword, testEmail);
        UserDAO myUserDAO = new MemoryUserDAO();
        myUserDAO.addUser(user);

        Assertions.assertEquals(user, myUserDAO.getUser(testUsername));
    }
    @Test
    public void testGetUserFail() throws Exception {
        UserDAO myUserDAO = new MemoryUserDAO();
        Assertions.assertNull(myUserDAO.getUser(null));
    }
    //getUsers
    @Test
    public void testGetUsersSuccess() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail";
        UserData user = new UserData(testUsername, testPassword, testEmail);
        UserDAO myUserDAO = new MemoryUserDAO();
        myUserDAO.addUser(user);
        String testUsername1 = "testUsername1";
        String testPassword1 = "testPassword1";
        String testEmail1 = "testEmail1";
        UserData user1 = new UserData(testUsername1, testPassword1, testEmail1);
        myUserDAO.addUser(user1);

        ArrayList<UserData> optimalUsers = new ArrayList<>(List.of());
        optimalUsers.add(user);
        optimalUsers.add(user1);

        ArrayList<UserData> users = (ArrayList<UserData>) myUserDAO.getUsers();
        optimalUsers.sort(Comparator.comparing(UserData::username));
        users.sort(Comparator.comparing(UserData::username));

        Assertions.assertEquals(optimalUsers, users);
    }
    @Test
    public void testGetUsersFail() throws Exception {
        UserDAO myUserDAO = new MemoryUserDAO();
        Assertions.assertEquals(List.of(), myUserDAO.getUsers());
    }
    //clearDB
    @Test
    public void clearUserDBSuccess() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail";
        UserData user = new UserData(testUsername, testPassword, testEmail);
        UserDAO myUserDAO = new MemoryUserDAO();
        myUserDAO.addUser(user);
        String testUsername1 = "testUsername1";
        String testPassword1 = "testPassword1";
        String testEmail1 = "testEmail1";
        UserData user1 = new UserData(testUsername1, testPassword1, testEmail1);
        myUserDAO.addUser(user1);

        myUserDAO.clearDB();
        Assertions.assertEquals(List.of(), myUserDAO.getUsers());
    }

    //test gameDAO
    //addGame
    @Test
    public void testAddGameSuccess() throws Exception {
        int gameID = 1234;
        String whiteUsername = "phil";
        String blackUsername = "sherry";
        String gameName = "WAR";
        ChessGame chess = new ChessGame();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chess);
        GameDAO myGameDAO = new MemoryGameDAO();
        Assertions.assertTrue(myGameDAO.addGame(game));
    }
    @Test
    public void testAddGameFail() throws Exception {
        GameDAO myGameDAO = new MemoryGameDAO();
        Assertions.assertFalse(myGameDAO.addGame(null));
    }
    //getGame
    @Test
    public void testGetGameSuccess() throws Exception {
        int gameID = 1234;
        String whiteUsername = "phil";
        String blackUsername = "sherry";
        String gameName = "WAR";
        ChessGame chess = new ChessGame();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chess);
        GameDAO myGameDAO = new MemoryGameDAO();
        myGameDAO.addGame(game);

        Assertions.assertEquals(game, myGameDAO.getGame(gameID));
    }
    @Test
    public void testGetGameFail() throws Exception {
        GameDAO myGameDAO = new MemoryGameDAO();
        Assertions.assertNull(myGameDAO.getGame(null));
    }
    //getGames
    @Test
    public void testGetGamesSuccess() throws Exception {
        GameDAO myGameDAO = new MemoryGameDAO();
        int gameID = 1234;
        String whiteUsername = "phil";
        String blackUsername = "sherry";
        String gameName = "WAR";
        ChessGame chess = new ChessGame();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chess);
        myGameDAO.addGame(game);
        int gameID2 = 12342;
        String whiteUsername2 = "phil2";
        String blackUsername2 = "sherry2";
        String gameName2 = "WAR2";
        ChessGame chess2 = new ChessGame();
        GameData game2 = new GameData(gameID2, whiteUsername2, blackUsername2, gameName2, chess2);
        myGameDAO.addGame(game2);

        ArrayList<GameData> optimalGames = new ArrayList<>(List.of());
        optimalGames.add(game);
        optimalGames.add(game2);

        ArrayList<GameData> games = (ArrayList<GameData>) myGameDAO.getGames();
        optimalGames.sort(Comparator.comparing(GameData::gameID));
        games.sort(Comparator.comparing(GameData::gameID));

        Assertions.assertEquals(optimalGames, games);
    }
    @Test
    public void testGetGamesFail() throws Exception {
        GameDAO myGameDAO = new MemoryGameDAO();
        Assertions.assertEquals(List.of(), myGameDAO.getGames());
    }
    //clearDB
    @Test
    public void clearGameDBSuccess() {
        GameDAO myGameDAO = new MemoryGameDAO();
        int gameID = 12345;
        String whiteUsername = "phillis";
        String blackUsername = "sherril";
        String gameName = "PEACE";
        ChessGame chess = new ChessGame();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chess);
        myGameDAO.addGame(game);
        int gameID2 = 123452;
        String whiteUsername2 = "phillis2";
        String blackUsername2 = "sherril2";
        String gameName2 = "PEACE2";
        ChessGame chess2 = new ChessGame();
        GameData game2 = new GameData(gameID2, whiteUsername2, blackUsername2, gameName2, chess2);
        myGameDAO.addGame(game2);

        myGameDAO.clearDB();
        Assertions.assertEquals(List.of(), myGameDAO.getGames());
    }
    //updateGame

}
