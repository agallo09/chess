package service;

import dataaccess.DAOauthToken;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {

    private DAOauthToken authDAO;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        authDAO = new DAOauthToken();
        authService = new AuthService(authDAO);
    }
    // logout tests
    @Test
    public void logoutPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        authDAO.createAuth(auth);
        AuthData result = authService.logout(auth);
        assertEquals(auth.authToken(), result.authToken());
        assertEquals(auth.username(), result.username());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authDAO.getUsername(auth.authToken());
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    public void logoutNegativeUnauthorized() {
        AuthData fakeAuth = new AuthData("fakeToken", "user");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authService.logout(fakeAuth);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }
    //clearr tests
    @Test
    public void clearAuthServicePositive() throws DataAccessException {
        AuthData auth = new AuthData("tokenABC", "userX");
        authDAO.createAuth(auth);
        assertDoesNotThrow(() -> {
            authDAO.getUsername(auth.authToken());
        });
        authService.clear();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authDAO.getUsername(auth.authToken());
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}
