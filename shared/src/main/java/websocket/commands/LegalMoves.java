package websocket.commands;
import chess.*;

public class LegalMoves extends UserGameCommand{
    private ChessPosition piece;
    public LegalMoves(CommandType commandType, String authToken, Integer gameID, ChessPosition piece) {
        super(commandType, authToken, gameID);
        this.piece = piece;
    }
    public ChessPosition getPiece(){
        return piece;
    }
}
