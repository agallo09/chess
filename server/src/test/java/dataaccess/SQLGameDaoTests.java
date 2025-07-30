package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDaoTests {

    private GameDaoInterface gameDao;
    private UserDaoInterface userDao;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDao = new SqlGameDao();
        userDao = new SqlUserDao();
        // Clear tables before each test
        gameDao.clear();
        userDao.clear();
        // Create test users for FK constraints
        userDao.createUser(new UserData("whiteUser", "pass", "white@example.com"));
        userDao.createUser(new UserData("blackUser", "pass", "black@example.com"));
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        GameData game = new GameData(0, null, null, "Test Game", new ChessGame());
        GameData created = gameDao.createGame(game);
        assertNotNull(created);
        assertTrue(created.gameID() > 0);
        assertEquals("Test Game", created.gameName());
    }

    @Test
    public void getGameExists() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "Game1", new ChessGame()));
        JoinRequest join = new JoinRequest(null, created.gameID());
        assertEquals("exists", gameDao.getGame(join));
    }

    @Test
    public void getGameNotExist() throws DataAccessException {
        JoinRequest join = new JoinRequest(null, 9999);
        assertNull(gameDao.getGame(join));
    }

    @Test
    public void checkColorWhiteAssigned() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "GameColor", new ChessGame()));
        gameDao.setWhiteUsername(created.gameID(), "whiteUser");
        assertEquals(ChessGame.TeamColor.WHITE, gameDao.checkColor(created.gameID(), ChessGame.TeamColor.WHITE));
    }

    @Test
    public void checkColorWhiteUnassigned() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "GameColor", new ChessGame()));
        assertNull(gameDao.checkColor(created.gameID(), ChessGame.TeamColor.WHITE));
    }

    @Test
    public void checkColorBlackAssigned() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "GameColor", new ChessGame()));
        gameDao.setBlackUsername(created.gameID(), "blackUser");
        assertEquals(ChessGame.TeamColor.BLACK, gameDao.checkColor(created.gameID(), ChessGame.TeamColor.BLACK));
    }

    @Test
    public void checkColorBlackUnassigned() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "GameColor", new ChessGame()));
        assertNull(gameDao.checkColor(created.gameID(), ChessGame.TeamColor.BLACK));
    }

    @Test
    public void setWhiteUsernamePositive() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "SetWhiteUser", new ChessGame()));
        gameDao.setWhiteUsername(created.gameID(), "whiteUser");

        GameData updated = gameDao.checkGame(new JoinRequest(null, created.gameID()));
        assertEquals("whiteUser", updated.whiteUsername());
    }

    @Test
    public void setBlackUsernamePositive() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "SetBlackUser", new ChessGame()));
        gameDao.setBlackUsername(created.gameID(), "blackUser");

        GameData updated = gameDao.checkGame(new JoinRequest(null, created.gameID()));
        assertEquals("blackUser", updated.blackUsername());
    }

    @Test
    public void checkGameExists() throws DataAccessException {
        GameData created = gameDao.createGame(new GameData(0, null, null, "CheckGame", new ChessGame()));
        JoinRequest join = new JoinRequest(null, created.gameID());

        GameData gameData = gameDao.checkGame(join);
        assertNotNull(gameData);
        assertEquals(created.gameID(), gameData.gameID());
    }

    @Test
    public void checkGameNotExists() throws DataAccessException {
        JoinRequest join = new JoinRequest(null, 99999);
        assertNull(gameDao.checkGame(join));
    }

    @Test
    public void listGamesReturnsAll() throws DataAccessException {
        gameDao.createGame(new GameData(0, null, null, "List1", new ChessGame()));
        gameDao.createGame(new GameData(0, null, null, "List2", new ChessGame()));
        Collection<GameData> games = gameDao.list();
        assertTrue(games.size() >= 2);
    }

    @Test
    public void clearGamesWorks() throws DataAccessException {
        gameDao.createGame(new GameData(0, null, null, "Clear1", new ChessGame()));
        gameDao.clear();
        Collection<GameData> games = gameDao.list();
        assertTrue(games.isEmpty());
    }

}
