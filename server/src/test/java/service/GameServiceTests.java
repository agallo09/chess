package service;

import chess.ChessGame;
import dataaccess.DAOauthToken;
import dataaccess.DAOgameData;
import dataaccess.DAOuserData;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private DAOauthToken authDAO;
    private DAOgameData gameDAO;
    private DAOuserData userDAO;
    private GameService service;

    @BeforeEach
    public void setup() {
        authDAO = new DAOauthToken();
        gameDAO = new DAOgameData();
        userDAO = new DAOuserData();
        service = new GameService(authDAO, gameDAO, userDAO);
    }

    // creating game tests

    @Test
    public void createPositive() throws DataAccessException {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        GameData gameInput = new GameData(0, null, null, "Chess Game 1", null);
        GameData result = service.create(auth, gameInput);
        assertNotNull(result);
        assertEquals("Chess Game 1", result.gameName());
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createNegativeUnauthorized() {
        AuthData invalidAuth = new AuthData("badtoken", "user");
        GameData gameInput = new GameData(0, null, null, "Chess Game 1", null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.create(invalidAuth, gameInput);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    public void createNegativeBadRequest() throws DataAccessException {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        GameData badGame = new GameData(0, null, null, "  ", null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.create(auth, badGame);
        });
        assertEquals("Error: bad request", ex.getMessage());
    }

    // list tests

    @Test
    public void listPositive() throws DataAccessException {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        gameDAO.createGame(new GameData(0, "white1", "black1", "Game 1", null));
        gameDAO.createGame(new GameData(0, "white2", "black2", "Game 2", null));
        ListData result = service.list(auth);
        assertNotNull(result);
        List<ListDataObject> games = result.games();
        assertEquals(2, games.size());
        ListDataObject game0 = games.get(0);
        assertNotNull(game0.gameID());
        assertEquals("Game 1", game0.gameName());
    }

    @Test
    public void listNegativeUnauthorized() {
        AuthData invalidAuth = new AuthData("badtoken", "user");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.list(invalidAuth);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    // joined tests
    @Test
    public void joinPositive() throws DataAccessException {
        // Prepare data
        String token = "token1";
        String username = "player1";
        authDAO.createAuth(new AuthData(token, username));
        GameData game = gameDAO.createGame(new GameData(0, null, null, "JoinGame", null));
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, game.gameID());
        String result = service.join(token, joinRequest);
        assertEquals("{}", result);
        GameData updatedGame = gameDAO.checkGame(joinRequest);
        assertEquals(username, updatedGame.whiteUsername());
    }

    @Test
    public void joinNegativeUnauthorized() {
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, 1);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.join(null, joinRequest);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    public void joinNegativeBadRequestInvalidColor() throws DataAccessException {
        String token = "token1";
        authDAO.createAuth(new AuthData(token, "player1"));
        JoinRequest badJoin = new JoinRequest(null,1 ); // null color

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.join(token, badJoin);
        });
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    public void joinNegativeBadRequestNoGame() throws DataAccessException {
        String token = "token1";
        authDAO.createAuth(new AuthData(token, "player1"));
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, 9999);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.join(token, joinRequest);
        });
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    public void joinNegativeColorAlreadyTaken() throws DataAccessException {
        String token1 = "token1";
        String username1 = "player1";
        authDAO.createAuth(new AuthData(token1, username1));
        GameData game = gameDAO.createGame(new GameData(0, null, null, "GameWithWhite", null));
        gameDAO.setWhiteUsername(game.gameID(), "someoneElse");
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, game.gameID());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.join(token1, joinRequest);
        });
        assertEquals("Error: already taken", ex.getMessage());
    }

    // clear testd

    @Test
    public void clearGameServicePositive() throws DataAccessException {
        GameData game = new GameData(0, null, null, "Test Game", null);
        AuthData auth = new AuthData("token", "user");
        authDAO.createAuth(auth);
        gameDAO.createGame(game);
        assertFalse(gameDAO.list().isEmpty());
        service.clear();
        assertTrue(gameDAO.list().isEmpty());
    }
}