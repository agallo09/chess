package chess;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor  ;
        this.type = type  ;
    }
    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case BISHOP:
                return bishopMoves(myPosition.getRow(),myPosition.getColumn());
            default:
                return new ArrayList<ChessMove>();

        }
    }
    private ArrayList<ChessMove> bishopMoves(int row, int col){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        // up right diagonal
        int upRow = row + 1;
        int rightCol = col +1 ;
        while (upRow<=8  && rightCol<= 8){
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, rightCol),null));
            upRow ++;
            rightCol++;
        }
        //up left diagonal
        upRow = row + 1;
        // we use upRow
        int leftCol = col -1;
        while (upRow<=8  && leftCol>=1){
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, leftCol),null));
            upRow ++;
            leftCol--;
        }
        //left down diagonal
        int downRow = row-1;
        leftCol = col-1;
        while (downRow>=1  && leftCol>=1){
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, leftCol),null));
            downRow --;
            leftCol--;
        }
        //right down diagonal
        downRow = row -1;
        // we use right Col
        rightCol = col +1 ;
        while (downRow>=1  && rightCol<=8){
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, rightCol),null));
            downRow --;
            rightCol++;
        }

        return moves;
    }

    }

