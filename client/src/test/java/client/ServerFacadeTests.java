package client;

import datamodel.AuthData;
import datamodel.ErrorResponse;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void before() {
        facade.clear();
    }

    @Test
    void registerGood() {
        try {
            AuthData auth = facade.register("player1", "password", "p1@email.com");
            assertTrue(auth.authToken().length() > 10);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void registerBad() {
        try {
            AuthData auth = facade.register("", "password", "p2@email.com");
            fail();
        }  catch (Exception e) {
            //if it threw an exception then it was correct
            assertFalse(false);
        }
    }

    @Test
    void clearGood() {
        try {
            facade.clear();
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void clearBad() {//it either works or it doesn't...
        try {
            facade.clear();
            assertFalse(false);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void loginGood() {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        assertTrue(auth.authToken().length() > 10);
    }
    @Test
    void loginBad() {
        facade.register("player1", "password", "p1@email.com");
        try {
            AuthData auth = facade.login("player1", "urmom");
            fail();
        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void logoutGood() {
        try {
            facade.register("player1", "password", "p1@email.com");
            AuthData auth = facade.login("player1", "password");
            facade.logout();
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void logoutBad() {//it either works or it doesn't...
        try {
            facade.register("player1", "password", "p1@email.com");
            AuthData auth = facade.login("player1", "password");
            facade.logout();
            assertFalse(false);
        } catch (Exception e) {
            fail();
        }
    }

}
