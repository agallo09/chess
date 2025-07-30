package dataaccess;

import model.AuthData;import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthServiceTests {

    private AuthTokenDaoInterface authDao;
    private UserDaoInterface userDao;
    private AuthService authService;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDao = new SqlAuthTokenDao();
        userDao = new SqlUserDao();
        userDao.clear(); // Clear Users table
        authDao.clear(); // Clear AuthTokens table

    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("user1", "pass123", "test@example.com"));
        AuthData auth = new AuthData("token123", "user1");
        authDao.createAuth(auth);
        String username = authDao.getUsername("token123");
        assertEquals("user1", username);
    }

    @Test
    public void createAuthNegativeDuplicateToken() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("user2", "pass123", "test@example.com"));
        AuthData auth = new AuthData("dupToken", "user2");
        authDao.createAuth(auth);
        AuthData duplicate = new AuthData("dupToken", "user2");
        assertThrows(DataAccessException.class, () -> authDao.createAuth(duplicate));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("user2", "pass123", "test@example.com"));
        AuthData auth = new AuthData("tokenXYZ", "user2");
        authDao.createAuth(auth);
        AuthData result = (AuthData) authDao.getAuth(auth);
        assertEquals(auth.authToken(), result.authToken());
        assertEquals(auth.username(), result.username());
    }

    @Test
    public void getAuthNegativeNonexistentToken() throws DataAccessException {
        AuthData fake = new AuthData("missingToken", "ghost");
        Object result = authDao.getAuth(fake);
        assertNull(result);
    }

    @Test
    public void getUsernamePositive() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("testuser", "pass123", "test@example.com"));

        // Now safe to create auth
        AuthData auth = new AuthData("abc123", "testuser");
        authDao.createAuth(auth);

        // Test getUsername
        String result = authDao.getUsername("abc123");
        assertEquals("testuser", result);
    }

    @Test
    public void getUsernameNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> authDao.getUsername("invalidToken"));
    }

    @Test
    public void deleteAuthInfoPositive() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("userDel", "pass123", "test@example.com"));
        AuthData auth = new AuthData("delToken", "userDel");
        authDao.createAuth(auth);
        authDao.deleteAuthInfo("delToken");
        assertThrows(DataAccessException.class, () -> authDao.getUsername("delToken"));
    }

    @Test
    public void clearPositive() throws DataAccessException {
        // Create the user first because of FK constraint
        UserDaoInterface userDao = new SqlUserDao();
        userDao.createUser(new UserData("u1", "pass123", "test@example.com"));
        // Create the user first because of FK constraint
        UserDaoInterface userDao2 = new SqlUserDao();
        userDao.createUser(new UserData("u2", "pass123", "test@example.com"));
        AuthData a1 = new AuthData("t1", "u1");
        AuthData a2 = new AuthData("t2", "u2");
        authDao.createAuth(a1);
        authDao.createAuth(a2);
        authDao.clear();
        assertThrows(DataAccessException.class, () -> authDao.getUsername("t1"));
        assertThrows(DataAccessException.class, () -> authDao.getUsername("t2"));
    }
}
