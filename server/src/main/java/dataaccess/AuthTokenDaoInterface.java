package dataaccess;

import model.AuthData;

public interface AuthTokenDaoInterface {
    void createAuth(AuthData authdata) throws DataAccessException;
    void deleteAuthInfo(String token) throws DataAccessException;
    Object getAuth(AuthData token) throws DataAccessException;
    void clear();
    String getUsername(String token) throws DataAccessException;

}
