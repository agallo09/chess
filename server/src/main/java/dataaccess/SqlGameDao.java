package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinRequest;

import java.util.Collection;

public class SqlGameDao implements GameDaoInterface {
    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    @Override
    public String getGame(JoinRequest join) {
        return null;
    }

    @Override
    public ChessGame.TeamColor checkColor(int id, ChessGame.TeamColor color) {
        return null;
    }

    @Override
    public void setWhiteUsername(int gameID, String username) throws DataAccessException {

    }

    @Override
    public void setBlackUsername(int gameID, String username) throws DataAccessException {

    }

    @Override
    public GameData checkGame(JoinRequest join) {
        return null;
    }

    @Override
    public Collection<GameData> list() throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
