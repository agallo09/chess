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
    public void register_positive() throws DataAccessException {
        UserData newUser = new UserData("john_doe", "password123", "john@example.com");

        AuthData result = service.register(newUser);

        // Required: Assert statement
        assertNotNull(result);
        assertEquals("john_doe", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void register_negative_duplicateUser() throws DataAccessException {
        UserData newUser = new UserData("john_doe", "password123", "john@example.com");
        service.register(newUser); // First time: should succeed

        // Second time: should throw exception
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.register(newUser);
        });

        // Required: Assert statement
        assertEquals("Error: already taken", ex.getMessage());
    }
}
