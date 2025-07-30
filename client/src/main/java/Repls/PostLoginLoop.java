package Repls;
import server.ServerFacade;

public class PostLoginLoop {
    private ServerFacade server;
    public PostLoginLoop(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }
}
