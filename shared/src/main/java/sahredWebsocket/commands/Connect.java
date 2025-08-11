package sahredWebsocket.commands;

public class Connect extends UserGameCommand {
    private final String color;

    public Connect(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}