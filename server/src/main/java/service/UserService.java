import model.AuthData;
import model.UserData;

public class UserService {

    public AuthData login(UserData user) {
        String username = user.username();
        String password = user.password();

        userDAO userDAO = new userDAO();
        UserData userdata = userDAO.findUser(username);

        if (userdata == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Check password match
        if (!userdata.password().equals(password)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Generate token (dummy example, you'd use something more secure in production)
        String token = java.util.UUID.randomUUID().toString();

        return new AuthData(token, username);
    }
}