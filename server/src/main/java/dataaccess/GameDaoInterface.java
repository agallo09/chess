package dataaccess;

import chess.ChessGame;
import model.GameData;
import dataaccess.DataAccessException;
import model.JoinRequest;

import java.util.Collection;

public interface GameDaoInterface {
    GameData createGame(GameData game) throws DataAccessException;
    String getGame(JoinRequest join) throws DataAccessException;
    ChessGame.TeamColor checkColor(int id, ChessGame.TeamColor color) throws DataAccessException;
    void setWhiteUsername(int gameID, String username) throws DataAccessException;
    void setBlackUsername(int gameID, String username) throws DataAccessException;
    GameData checkGame(JoinRequest join) throws DataAccessException;
    Collection<GameData> list() throws DataAccessException;
    void clear() throws DataAccessException;
}