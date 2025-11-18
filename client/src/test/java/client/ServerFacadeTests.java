package client;

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
            var authData = facade.register("player1", "password", "p1@email.com");
            assertTrue(authData.authToken().length() > 10);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void registerBad() {
        try {
            var authData = facade.register("player2", "password", "p2@email.com");
            assertNotEquals("player1", authData.username());
        }  catch (Exception e) {
            fail();
        }
    }

}
