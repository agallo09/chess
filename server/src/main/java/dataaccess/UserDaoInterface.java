package dataaccess;

import model.UserData;
import dataaccess.DataAccessException;

public interface UserDaoInterface {
    void createUser(UserData user) throws DataAccessException;
    Object getUser(UserData user) throws DataAccessException;
    void clear() ;
    boolean validate(UserData user);
}