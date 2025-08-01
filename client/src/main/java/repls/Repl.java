package repls;
import java.util.Scanner;

import static repls.State.PRELOGIN;

public class Repl {
    //initializing objects from loops
    private final ClientLoop client;
    private State status = PRELOGIN;

    //contructor
    public Repl(String serverUrl) {
        this.client = new ClientLoop(serverUrl);


    }
    public void run(){
        Scanner scanner = new Scanner(System.in);
        //initial options
        System.out.println("Options:\n" +
                "Login as an existing user: \"I\", \"login\" ‹USERNAME> <PASSWORD>\n" +
                "Register a new user: \"r\", \"register\" ‹USERNAME> < PASSWORD> <EMAIL>\n" +
                "Exit the program: \"a\", \"quit\"\n" +
                "Print this message: \"h\", \"help\"");
        var result = "";
        // evaluating first input
        while (!result.equals("quit")) {
            printPrompt(status);
            String line = scanner.nextLine();
            try {
                //System.out.print(line);
                //the evaluation of the input is going to give back a string,
                // which is going to be the response from the server
                result = client.eval(line);
                this.status = client.getstate();
                System.out.print(result);
                System.out.print('\n');

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    private void printPrompt(State status) {
        System.out.print("\n" + status + ">>> ");    }

}
