package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    CREATE TABLE IF NOT EXISTS userData (
                        username VARCHAR(255) PRIMARY KEY,
                        password VARCHAR(255),
                        email VARCHAR(255)
                    );
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                //this statement returns the number of rows affected, we currently don't use that rn
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public boolean addUser(UserData user) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    INSERT INTO userData (username, password, email) VALUES (?, ?, ?);
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM userData WHERE username = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        //username already here
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Collection<UserData> getUsers() {
        ArrayList<UserData> users = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM userData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        UserData currUserData = new UserData(username, password, email);
                        users.add(currUserData);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void clearDB() {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    TRUNCATE TABLE userData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
