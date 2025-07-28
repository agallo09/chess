import chess.*;
import dataaccess.DataAccessException;
import server.Server;
import dataaccess.DatabaseManager;
public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        // database
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        // server
        Server server = new Server();
        server.run(8080);

    }
}