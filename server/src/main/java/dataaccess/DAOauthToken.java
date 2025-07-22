package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import dataaccess.DataAccessException;

public class DAOauthToken {
    private final Map<String, String> auth = new HashMap<>();

    public DAOauthToken(){}
    public void createAuth(AuthData authdata) {
        auth.put(authdata.authToken(), authdata.username());
    }

    public void deleteAuthInfo(String token) {
        if (auth.containsKey(token)) {
            auth.remove(token);
            return ;
        }
    }
    public Object getAuth(AuthData token) throws DataAccessException {
        String tokenToken = token.authToken();
        if (auth.containsKey(tokenToken)) {
            return token;
        }
        return null;
    }

}
