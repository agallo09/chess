package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

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
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
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
                return bishopMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition);
            case ROOK:
                return rookMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition);
            case QUEEN:
                //Alessandro   QS
                ArrayList<ChessMove> newMoves = new ArrayList<ChessMove>();
                newMoves.addAll(rookMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition));
                newMoves.addAll(bishopMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition));
                return newMoves;
            case KNIGHT:
                return knightMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition);
            case KING:
                return kingMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition);
            case PAWN:
                return pawnMoves(myPosition.getRow(),myPosition.getColumn(), board, myPosition);
            default:
                return new ArrayList<ChessMove>();

        }
    }
    private ArrayList<ChessMove> bishopMoves(int row, int col, ChessBoard board,ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        // up right diagonal
        int upRow = row + 1;
        int rightCol = col +1 ;
        while (upRow<=8  && rightCol<= 8){
            ChessPiece piece = board.getPiece(new ChessPosition(upRow,rightCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece==null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, rightCol),null));
            } else if(piece.getTeamColor() != mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, rightCol),null));
                break;
            }else{
                break;
            }

            upRow ++;
            rightCol++;
        }
        //up left diagonal
        upRow = row + 1;
        // we use upRow
        int leftCol = col -1;
        while (upRow<=8  && leftCol>=1){
            ChessPiece piece = board.getPiece(new ChessPosition(upRow,leftCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece==null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, leftCol),null));
            } else if(piece.getTeamColor() !=mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(upRow, leftCol),null));
                break;
            }else{
                break;
            }
            upRow ++;
            leftCol--;
        }
        //left down diagonal
        int downRow = row-1;
        leftCol = col-1;
        while (downRow>=1  && leftCol>=1){
            ChessPiece piece = board.getPiece(new ChessPosition(downRow,leftCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, leftCol),null));
            } else if(piece.getTeamColor()!=mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, leftCol),null));
                break;
            }else{
                break;
            }
            downRow --;
            leftCol--;
        }
        //right down diagonal
        downRow = row -1;
        // we use right Col
        rightCol = col +1 ;
        while (downRow>=1  && rightCol<=8){
            ChessPiece piece = board.getPiece(new ChessPosition(downRow,rightCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, rightCol),null));
            } else if(piece.getTeamColor()!= mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(downRow, rightCol),null));
                break;
            }else{
                break;
            }
            downRow --;
            rightCol++;
        }

        return moves;
    }


    private ArrayList<ChessMove> rookMoves(int row, int col, ChessBoard board,ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        // up
        int rightRow = row + 1;
        while (rightRow<=8){
            ChessPiece piece = board.getPiece(new ChessPosition(rightRow, col));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece==null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(rightRow, col),null));
            } else if(piece.getTeamColor() != mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(rightRow, col),null));
                break;
            }else{
                break;
            }
            rightRow ++;
        }
        //down
        int leftRow = row -1;
        while (leftRow>=1){
            ChessPiece piece = board.getPiece(new ChessPosition(leftRow,col));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece==null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(leftRow, col),null));
            } else if(piece.getTeamColor() !=mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(leftRow, col),null));
                break;
            }else{
                break;
            }
            leftRow --;
        }
        //left
        int leftCol = col-1;
        while (leftCol >=1){
            ChessPiece piece = board.getPiece(new ChessPosition(row,leftCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row, leftCol),null));
            } else if(piece.getTeamColor()!=mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row, leftCol),null));
                break;
            }else{
                break;
            }
            leftCol --;
        }
        //right
        int rightCol = col +1 ;
        while (rightCol<=8){
            ChessPiece piece = board.getPiece(new ChessPosition(row,rightCol));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row, rightCol),null));
            } else if(piece.getTeamColor()!= mypiece.getTeamColor() ){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row, rightCol),null));
                break;
            }else{
                break;
            }
            rightCol++;
        }

        return moves;
    }


    private ArrayList<ChessMove> knightMoves(int row, int col, ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        //Upright
        if (row+2 <= 8 && col+1 <= 8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 2, col + 1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 2, col + 1), null));
            }
        }

        // right up
        if (row+1 <= 8 && col+2<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col +2));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col +2), null));
            }
        }
        //Up left
        if (row+2 <= 8 && col-1>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 2, col -1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 2, col -1), null));
            }
        }
        // left up
        if (row+1 <= 8 && col-2>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col -2));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col -2), null));
            }
        }
        // down right
        if (row-2 >=1 && col+1<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -2, col +1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -2, col +1), null));
            }
        }
        //down left
        if (row-2 >=1 && col-1>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -2, col -1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -2, col -1), null));
            }
        }

        //right down
        if (row-1 >=1 && col+2<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -1, col +2));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -1, col +2), null));
            }
        }
        //left dow
        if (row-1 >=1 && col-2>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -1, col -2));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -1, col -2), null));
            }
        }
        return moves;
    }

    private ArrayList<ChessMove> kingMoves(int row, int col, ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        //Up left
        if (row+1 <= 8 && col-1 >=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col - 1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col -1), null));
            }
        }

        // Up
        if (row+1 <= 8 && col >=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col ));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col ), null));
            }
        }
        //Up right
        if (row+1 <= 8 && col+1<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col +1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col +1), null));
            }
        }
        // left
        if (row <= 8 && col-1>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col -1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row , col -1), null));
            }
        }
        //  right
        if (row <=8 && col+1<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col +1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row, col +1), null));
            }
        }
        //down left
        if (row-1 >=1 && col-1>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -1, col -1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -1, col -1), null));
            }
        }

        // down
        if (row-1 >=1 && col>=1) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -1, col));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -1, col), null));
            }
        }
        // down right
        if (row-1 >=1 && col+1<=8) {
            ChessPiece piece = board.getPiece(new ChessPosition(row -1, col +1));
            ChessPiece mypiece = board.getPiece(myPosition);
            if (piece == null || piece.getTeamColor() != mypiece.getTeamColor()) {
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row -1, col +1), null));
            }
        }
        return moves;

    }


    private ArrayList<ChessMove> pawnMoves(int row, int col, ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece thisPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = thisPiece.getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            addWhitePawnMoves(row, col, board, myPosition, moves);
        } else {
            addBlackPawnMoves(row, col, board, myPosition, moves);
        }

        return moves;
    }
    private void addWhitePawnMoves(int row, int col, ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        // Promotion
        if (row == 7) {
            addPromotions(row + 1, col, row, col, board, moves);
        }

        // Two-step move from start
        if (row == 2 && board.getPiece(new ChessPosition(row + 2, col)) == null &&
                board.getPiece(new ChessPosition(row + 1, col)) == null) {
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 2, col), null));
        }

        // One-step forward
        if (row + 1 <= 8 && board.getPiece(new ChessPosition(row + 1, col)) == null) {
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col), null));
        }

        // Captures
        addCaptures(row, col, 1, board, myPosition, moves, ChessGame.TeamColor.WHITE);
    }

    private void addBlackPawnMoves(int row, int col, ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        // Promotion
        if (row == 2) {
            addPromotions(row - 1, col, row, col, board, moves);
        }

        // Two-step move from start
        if (row == 7 && board.getPiece(new ChessPosition(row - 2, col)) == null &&
                board.getPiece(new ChessPosition(row - 1, col)) == null) {
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 2, col), null));
        }

        // One-step forward
        if (row - 1 >= 1 && board.getPiece(new ChessPosition(row - 1, col)) == null) {
            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 1, col), null));
        }

        // Captures
        addCaptures(row, col, -1, board, myPosition, moves, ChessGame.TeamColor.BLACK);
    }

    private void addPromotions(int targetRow, int targetCol, int fromRow, int fromCol, ChessBoard board, ArrayList<ChessMove> moves) {
        if (targetRow >= 1 && targetRow <= 8) {
            ChessPiece piece = board.getPiece(new ChessPosition(targetRow, targetCol));
            if (piece == null) {
                for (PieceType promo : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                    moves.add(new ChessMove(new ChessPosition(fromRow, fromCol), new ChessPosition(targetRow, targetCol), promo));
                }
            }
        }
    }

    private void addCaptures(int row, int col, int direction, ChessBoard board, ChessPosition myPosition,
                             ArrayList<ChessMove> moves, ChessGame.TeamColor color) {
        for (int dCol : new int[]{-1, 1}) {
            int targetRow = row + direction;
            int targetCol = col + dCol;
            if (targetRow >= 1 && targetRow <= 8 && targetCol >= 1 && targetCol <= 8) {
                ChessPiece targetPiece = board.getPiece(new ChessPosition(targetRow, targetCol));
                if (targetPiece != null && targetPiece.getTeamColor() != color) {
                    if ((color == ChessGame.TeamColor.WHITE && row == 7) ||
                            (color == ChessGame.TeamColor.BLACK && row == 2)) {
                        for (PieceType promo : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                            moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(targetRow, targetCol), promo));
                        }
                    } else {
                        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(targetRow, targetCol), null));
                    }
                }
            }
        }
    }



    }

