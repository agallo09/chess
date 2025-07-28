package dataaccess;

import model.AuthData;

public interface AuthTokenDaoInterface {
    void createAuth(AuthData authdata);
    void deleteAuthInfo(String token);
    Object getAuth(AuthData token) throws DataAccessException;
    void clear();
    String getUsername(String token) throws DataAccessException;

}
