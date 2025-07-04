package chess;
import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor Team;
    public ChessGame() {
    }
    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return Team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
       this.Team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
















    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPiece thisPiece = board.getPiece(startPosition);
        if (thisPiece == null) {
            return null;
        }

        Collection<ChessMove> pieceMoves = thisPiece.pieceMoves(board, startPosition);
        for (ChessMove move : pieceMoves) {

            // Step 1: Create a deep copy of the board
            ChessBoard boardTry = copyBoard(this.board);

            // Step 2: Simulate the move on the copied board
            boardTry.addPiece(move.getEndPosition(), boardTry.getPiece(move.getStartPosition()));
            boardTry.addPiece(move.getStartPosition(), null); // Clear old position

            // Step 3: Temporarily swap to that board and check for check
            ChessBoard originalBoard = this.board;
            this.board = boardTry;

            boolean inCheck = isInCheck(thisPiece.getTeamColor());

            // Step 4: Restore the original board
            this.board = originalBoard;

            // Step 5: Only keep move if king is not in check
            if (!inCheck) {
                moves.add(move);
            }
        }

        return moves;
    }

    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = original.getPiece(pos);
                if (piece != null) {
                    copy.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return copy;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> moves = validMoves(startPosition);


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        // 1. Find the king's position for the given team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = pos;
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }

        // If king is not found, you might want to handle this as an error or return false
        if (kingPosition == null) {
            return false;
        }

        // 2. Check all opponent pieces for moves that attack the king's position
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> opponentMoves = piece.pieceMoves(board, pos);
                    for (ChessMove move : opponentMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true; // King is attacked
                        }
                    }
                }
            }
        }

        return false; // No opponent moves threaten the king
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }













    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
