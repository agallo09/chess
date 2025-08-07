package repls;

public class ResponseException extends Exception {
    public ResponseException(int i, String message) {
        super(message);
    }

    public ResponseException(String message, Throwable ex) {
        super(message, ex);
    }
}
