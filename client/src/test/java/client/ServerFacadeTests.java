package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        String url = "http://localhost:" + port;
        facade = new ServerFacade(url);
        System.out.println("Started test HTTP server on " + url);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }

    // ---- REGISTER ----

    @Test
    public void register_positive() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        assertNotNull(auth.authToken());
        assertEquals("player1", auth.username());
    }

    @Test
    public void register_duplicate_negative() throws Exception {
        facade.register("player1", "pass123", "p1@email.com");

        Exception ex = assertThrows(Exception.class, () ->
                facade.register("player1", "pass123", "p1@email.com"));

        System.out.println("Actual error message: " + ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
    }

    // ---- LOGIN ----

    @Test
    public void login_positive() throws Exception {
        facade.register("player1", "pass123", "p1@email.com");
        AuthData auth = facade.login("player1", "pass123");
        assertEquals("player1", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    public void login_wrong_password_negative() throws Exception {
        facade.register("player1", "pass123", "p1@email.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.login("player1", "wrongpass"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // ---- LOGOUT ----

    @Test
    public void logout_positive() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logout_invalid_token_negative() {
        Exception ex = assertThrows(Exception.class, () -> facade.logout("bad-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // ---- CREATE GAME ----

    @Test
    public void createGame_positive() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        int gameID = facade.createGame("My Game", auth.authToken());
        assertTrue(gameID > 0);
    }

    @Test
    public void createGame_invalid_token_negative() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.createGame("My Game", "bad-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // ---- LIST GAMES ----

    @Test
    public void listGames_positive() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        facade.createGame("First Game", auth.authToken());
        List<GameData> games = facade.listGames(auth.authToken());
        assertEquals(1, games.size());
        assertEquals("First Game", games.get(0).gameName());
    }

    @Test
    public void listGames_invalid_token_negative() {
        Exception ex = assertThrows(Exception.class, () -> facade.listGames("bad-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // ---- JOIN GAME ----

    @Test
    public void joinGame_white_positive() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        int gameID = facade.createGame("Epic Game", auth.authToken());
        assertDoesNotThrow(() -> facade.joinGame(gameID, "WHITE", auth.authToken()));
    }

    @Test
    public void joinGame_invalid_token_negative() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        int gameID = facade.createGame("Epic Game", auth.authToken());

        Exception ex = assertThrows(Exception.class, () ->
                facade.joinGame(gameID, "black", "bad-token"));

        System.out.println("Actual joinGame error: " + ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    public void joinGame_nonexistent_game_negative() throws Exception {
        AuthData auth = facade.register("player1", "pass123", "p1@email.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.joinGame(9999, "white", auth.authToken()));
        assertTrue(ex.getMessage().toLowerCase().contains("error") ||
                ex.getMessage().toLowerCase().contains("bad"));
    }
}
