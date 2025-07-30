package Repls;
import server.ServerFacade;
public class Repl {
    //initializing objects from loops
    private final ServerFacade facade;
    //contructor
    public Repl(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
    }
    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to the pet store. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

}
