package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDao implements GameDaoInterface {

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (name, game) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.gameName());
            Gson gson = new Gson();
            String gameJson = gson.toJson(new ChessGame());
            stmt.setString(2, gameJson);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int gameId = rs.getInt(1);

            return new GameData(gameId, null, null,game.gameName(), new ChessGame());

        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
    }

    @Override
    public String getGame(JoinRequest join) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, join.gameID());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "exists";
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
    }

    @Override
    public ChessGame.TeamColor checkColor(int id, ChessGame.TeamColor color) throws DataAccessException {
        String sql = "SELECT whiteUsername, blackUsername FROM Games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String whiteUser = rs.getString("whiteUsername");
                String blackUser = rs.getString("blackUsername");

                if (color == ChessGame.TeamColor.WHITE) {
                    return (whiteUser == null) ? null : ChessGame.TeamColor.WHITE;
                } else if (color == ChessGame.TeamColor.BLACK) {
                    return (blackUser == null) ? null : ChessGame.TeamColor.BLACK;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to check color", e);
        }
    }

    @Override
    public void setWhiteUsername(int gameID, String username) throws DataAccessException {
        String sql = "UPDATE Games SET whiteUsername = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to set white username", e);
        }
    }

    @Override
    public void setBlackUsername(int gameID, String username) throws DataAccessException {
        String sql = "UPDATE Games SET blackUsername = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to set black username", e);
        }
    }

    @Override
    public GameData checkGame(JoinRequest join) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, join.gameID());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String whiteUser = rs.getString("whiteUsername");
                String blackUser = rs.getString("blackUsername");
                String gameJson = rs.getString("game");

                Gson gson = new Gson();
                ChessGame gameObj = gson.fromJson(gameJson, ChessGame.class);

                return new GameData(id, whiteUser, blackUser, name, gameObj);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to check game", e);
        }
    }

    @Override
    public Collection<GameData> list() throws DataAccessException {
        String sql = "SELECT * FROM Games";
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Gson gson = new Gson();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String whiteUser = rs.getString("whiteUsername");
                String blackUser = rs.getString("blackUsername");
                String gameJson = rs.getString("game");

                ChessGame gameObj = gson.fromJson(gameJson, ChessGame.class);

                games.add(new GameData(id, whiteUser, blackUser, name, gameObj));
            }

            return games;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games", e);
        }
    }
}

