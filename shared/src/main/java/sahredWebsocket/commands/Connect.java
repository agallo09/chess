package sahredWebsocket.commands;

public class Connect extends  UserGameCommand{
    public Connect(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);
    }
}
