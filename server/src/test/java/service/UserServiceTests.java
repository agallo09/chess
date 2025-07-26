package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService service;
    private DAOuserData userDAO;
    private DAOauthToken authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new DAOuserData();
        authDAO = new DAOauthToken();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerPositive() throws DataAccessException {
        UserData newUser = new UserData("john_doe", "password123", "john@example.com");
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
    @Test
    public void loginPositive() throws DataAccessException {
        UserData user = new UserData("jane_doe", "securePass", "jane@example.com");
        service.register(user);
        AuthData result = service.login(new UserData("jane_doe", "securePass", null));
        assertNotNull(result);
        assertEquals("jane_doe", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeWrongPassword() throws DataAccessException {
        UserData user = new UserData("john_smith", "correctPass", "john@example.com");
        service.register(user);
        UserData wrongPasswordUser = new UserData("john_smith", "wrongPass", null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.login(wrongPasswordUser);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }

}