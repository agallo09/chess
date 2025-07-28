package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import dataaccess.DataAccessException;

public class DAOauthToken implements AuthTokenDaoInterface{
    private final Map<String, String> auths = new HashMap<>();

    public DAOauthToken(){
        //nothing
    }
    public void createAuth(AuthData authdata) {
        auths.put(authdata.authToken(), authdata.username());
    }
    @Override
    public void deleteAuthInfo(String token) {
        if (auths.containsKey(token)) {
            auths.remove(token);
            return ;
        }
    }
    @Override
    public Object getAuth(AuthData token) throws DataAccessException {
        String tokenToken = token.authToken();
        if (auths.containsKey(tokenToken)) {
            return token;
        }
        return null;
    }
    @Override
    public void clear() {
        auths.clear();
    }
    @Override
    public String getUsername(String token) throws DataAccessException {
        String username = auths.get(token);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return username;
    }
}
