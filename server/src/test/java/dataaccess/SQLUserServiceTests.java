package dataaccess;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserServiceTests {

    private UserService service;
    private UserDaoInterface userDAO;
    private AuthTokenDaoInterface authDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new SqlUserDao();
        authDAO = new SqlAuthTokenDao();
        service = new UserService(userDAO, authDAO);
        userDAO.clear(); // Clear Users table
        authDAO.clear(); // Clear AuthTokens table

    }

    private UserData createTestUser() {
        return new UserData("john_doe", "password123", "john@example.com");
    }

    @Test
    public void registerPositive() throws DataAccessException {
        UserData newUser = createTestUser();
        AuthData result = service.register(newUser);
        assertNotNull(result);
        assertEquals("john_doe", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeDuplicateUser() throws DataAccessException {
        UserData newUser = new UserData("john_doe", "password123", "john@example.com");
        service.register(newUser); // First time: should succeed
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.register(newUser);
        });
        assertEquals("Error: already taken", ex.getMessage());
    }

    //Test3s for login
    private UserData createUser(String username, String password, String email) {
        return new UserData(username, password, email);
    }

    private UserData createLoginUser(String username, String password) {
        // For login, email can be null if not required
        return new UserData(username, password, null);
    }

    @Test
    public void loginPositive() throws DataAccessException {
        UserData user = createUser("jane_doe", "securePass", "jane@example.com");
        service.register(user);
        AuthData result = service.login(createLoginUser("jane_doe", "securePass"));
        assertNotNull(result);
        assertEquals("jane_doe", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeWrongPassword() throws DataAccessException {
        UserData user = createUser("john_smith", "correctPass", "john@example.com");
        service.register(user);
        UserData wrongPasswordUser = createLoginUser("john_smith", "wrongPass");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.login(wrongPasswordUser);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}