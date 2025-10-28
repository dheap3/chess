package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.UserDAO;
import datamodel.AuthData;
import datamodel.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StandardServiceTests {
    private final MemoryAuthDAO testAuthDAO = new MemoryAuthDAO(); //created so the UserService and GameService can access the same authDAO

    private final UserService testUserService = new UserService(testAuthDAO);
    private final GameService testGameService = new GameService(testAuthDAO);

    @BeforeEach
    void setup() {
        testUserService.clearDB();
        testGameService.clearDB();
    }

    //--test UserService--
    //test register
    @Test
    public void testRegisterSuccess() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        var response = testUserService.register(testUsername, testPassword, testEmail);
        var testUsers = testUserService.userDAO.getUsers();
        assertEquals(1, testUsers.size());
        UserData testUser = new UserData(testUsername, testPassword, testEmail);
        assertTrue(testUsers.contains(testUser));

        var testAuths = testAuthDAO.getAuths();
        assertEquals(1, testAuths.size());
        AuthData testAuth = testAuths.get(0);
        assertTrue(testAuths.contains(testAuth));

        var statusString = Map.of("username", testAuth.username(), "authToken", testAuth.authToken());
        var statusCode = 200;
        assertEquals(response, Map.of(statusCode, statusString));
    }
    @Test
    public void testRegisterBadRequest() {
        String testUsername = "";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        var response = testUserService.register(testUsername, testPassword, testEmail);

        var statusString = Map.of("message", "Error: bad request");
        var statusCode = 400;
        assertEquals(response, Map.of(statusCode, statusString));
        var testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        var testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = "";
        testEmail = "testEmail@test.com";
        response = testUserService.register(testUsername, testPassword, testEmail);
        assertEquals(response, Map.of(statusCode, statusString));
        testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = " ";
        testEmail = "testEmail@test.com";
        response = testUserService.register(testUsername, testPassword, testEmail);
        assertEquals(response, Map.of(statusCode, statusString));
        testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = " ";
        testPassword = "testPassword";
        testEmail = "testEmail@test.com";
        response = testUserService.register(testUsername, testPassword, testEmail);
        assertEquals(response, Map.of(statusCode, statusString));
        testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = "testPassword";
        testEmail = "";
        response = testUserService.register(testUsername, testPassword, testEmail);
        assertEquals(response, Map.of(statusCode, statusString));
        testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = "testPassword";
        testEmail = " ";
        response = testUserService.register(testUsername, testPassword, testEmail);
        assertEquals(response, Map.of(statusCode, statusString));
        testUsers = testUserService.userDAO.getUsers();
        assertEquals(0, testUsers.size());
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());
    }
    @Test
    public void testRegisterAlreadyTaken() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        testUserService.register(testUsername, testPassword, testEmail);
        var response = testUserService.register(testUsername, testPassword, testEmail);

        var statusString = Map.of("message", "Error: already taken");
        var statusCode = 403;
        assertEquals(response, Map.of(statusCode, statusString));

        var testUsers = testUserService.userDAO.getUsers();
        assertEquals(1, testUsers.size());
        UserData testUser = new UserData(testUsername, testPassword, testEmail);
        assertTrue(testUsers.contains(testUser));

        var testAuths = testAuthDAO.getAuths();
        assertEquals(1, testAuths.size());
        AuthData testAuth = testAuths.get(0);
        assertTrue(testAuths.contains(testAuth));
    }
    //test login
    @Test
    public void testLoginSuccess() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        UserData testUser = new UserData(testUsername, testPassword, testEmail);
        testUserService.userDAO.addUser(testUser);
        var response = testUserService.login(testUsername, testPassword);

        var testAuths = testAuthDAO.getAuths();
        AuthData testAuth = testAuths.get(0);

        var statusString = Map.of("username", testAuth.username(), "authToken", testAuth.authToken());
        var statusCode = 200;
        assertEquals(response, Map.of(statusCode, statusString));
    }
    @Test
    public void testLoginBadRequest() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        UserData testUser = new UserData(testUsername, testPassword, testEmail);
        testUserService.userDAO.addUser(testUser);
        testUsername = "";
        testPassword = "testPassword";
        var response = testUserService.login(testUsername, testPassword);

        var statusString = Map.of("message", "Error: bad request");
        var statusCode = 400;
        assertEquals(response, Map.of(statusCode, statusString));
        var testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = "";
        response = testUserService.login(testUsername, testPassword);
        assertEquals(response, Map.of(statusCode, statusString));
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = " ";
        response = testUserService.login(testUsername, testPassword);
        assertEquals(response, Map.of(statusCode, statusString));
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = " ";
        testPassword = "testPassword";
        response = testUserService.login(testUsername, testPassword);
        assertEquals(response, Map.of(statusCode, statusString));
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());


    }
    @Test
    public void testLoginUnauthorized() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testEmail = "testEmail@test.com";
        UserData testUser = new UserData(testUsername, testPassword, testEmail);
        testUserService.userDAO.addUser(testUser);
        testUsername = "asdf";
        testPassword = "testPassword";
        var response = testUserService.login(testUsername, testPassword);

        var statusString = Map.of("message", "Error: unauthorized");
        var statusCode = 401;
        assertEquals(response, Map.of(statusCode, statusString));
        var testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());

        testUsername = "testUsername";
        testPassword = "asdf";
        response = testUserService.login(testUsername, testPassword);
        assertEquals(response, Map.of(statusCode, statusString));
        testAuths = testAuthDAO.getAuths();
        assertEquals(0, testAuths.size());
    }
    //test logout
    @Test
    public void testLogoutSuccess() {
        String testUsername = "testUsername";
        String testAuthToken = "testAuthToken";
        AuthData auth = new AuthData(testUsername, testAuthToken);
        testAuthDAO.addAuth(auth);
        var response = testUserService.logout(testAuthToken);

        var statusString = Map.of();
        var statusCode = 200;
        assertEquals(response, Map.of(statusCode, statusString));
    }
    @Test
    public void testLogoutUnauthorized() {
        String testUsername = "testUsername";
        String testAuthToken = "testAuthToken";
        AuthData auth = new AuthData(testUsername, testAuthToken);
        testAuthDAO.addAuth(auth);

        testAuthToken = "asdf";
        var response = testUserService.logout(testAuthToken);

        var statusString = Map.of("message", "Error: unauthorized");
        var statusCode = 401;
        assertEquals(Map.of(statusCode, statusString), response);
        assertFalse(testAuthDAO.dbContains(testAuthToken));
    }
    //testGetUser
    @Test
    public void testGetUser() {
        String testUsername = "testUsername";
        testUserService.userDAO.addUser(new UserData(testUsername, "testPassword", "testEmail@test.com"));
        var testUser = testUserService.getUser(testUsername);
        assertEquals(testUsername, testUser.username());
    }
    @Test
    public void testGetUserFail() {
        String testUsername = "testUsername";
        var testUser = testUserService.getUser(testUsername);
        assertNull(testUser);
    }
    //test createAuth
    @Test
    public void testCreateUniqueAuth() {
        String testUsername = "testUsername";
        testUserService.createAuth(testUsername);

        testUserService.createAuth(testUsername);

        assertEquals(2, testAuthDAO.getAuths().size());
        assertNotEquals(testAuthDAO.getAuths().get(0), testAuthDAO.getAuths().get(1));
    }
    @Test
    public void testCreateUniqueAuthFail() {
        String testUsername = "testUsername";
        var testAuth = testUserService.createAuth(testUsername);
        testAuthDAO.addAuth(testAuth);
        //add two of the same auth token
        assertEquals(1, testAuthDAO.getAuths().size());
        assertEquals(testAuth, testAuthDAO.getAuths().get(0));
    }
    //test removeAuth
    @Test
    public void testRemoveAuth() {
        String testUsername = "testUsername";
        var testAuth = testUserService.createAuth(testUsername);
        testUserService.removeAuth(testAuth.authToken());
        assertFalse(testAuthDAO.dbContains(testAuth.authToken()));
    }
    @Test
    public void testRemoveAuthFail() {
        String testUsername = "testUsername";
        var testAuth = testUserService.createAuth(testUsername);
        testUserService.removeAuth("testAuth.authToken()");
        assertTrue(testAuthDAO.dbContains(testAuth.authToken()));
    }
    //test clearDB
    @Test
    public void testClearDBSuccess() {
        testUserService.clearDB();

        assertEquals(0, testUserService.userDAO.getUsers().size());
        assertEquals(0, testUserService.authDAO.getAuths().size());
    }


    //--test GameService--
}
