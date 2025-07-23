package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import dataaccess.DataAccessException;

public class DAOauthToken {
    private final Map<String, String> auths = new HashMap<>();

    public DAOauthToken(){}
    public void createAuth(AuthData authdata) {
        auths.put(authdata.authToken(), authdata.username());
    }

    public void deleteAuthInfo(String token) {
        if (auths.containsKey(token)) {
            auths.remove(token);
            return ;
        }
    }
    public Object getAuth(AuthData token) throws DataAccessException {
        String tokenToken = token.authToken();
        if (auths.containsKey(tokenToken)) {
            return token;
        }
        return null;
    }

    public void clear() {
        auths.clear();
    }

    public String getUsername(String s) {
        return auths.get(s);
    }
}
