package dataaccess;

import chess.ChessGame;
import model.GameData;
import dataaccess.DataAccessException;
import model.JoinRequest;

import java.util.Collection;

public interface GameDaoInterface {
    GameData createGame(GameData game);
    String getGame(JoinRequest join);
    ChessGame.TeamColor checkColor(int id, ChessGame.TeamColor color);
    void setWhiteUsername(int gameID, String username) throws DataAccessException;
    void setBlackUsername(int gameID, String username) throws DataAccessException;
    GameData checkGame(JoinRequest join);
    Collection<GameData> list() throws DataAccessException;
    void clear();
}