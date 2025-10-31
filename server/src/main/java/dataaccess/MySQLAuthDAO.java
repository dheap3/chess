package dataaccess;

import datamodel.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    CREATE TABLE IF NOT EXISTS authData (
                        authToken VARCHAR(255) PRIMARY KEY,
                        username VARCHAR(255)
                    );
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                //this statement returns the number of rows affected, we currently don't use that rn
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public void addAuth(AuthData auth) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    INSERT INTO authData (authToken, username) VALUES (?, ?);
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM authData WHERE authToken = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        //authToken already there
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ArrayList<AuthData> getAuths() {
        var auths = new ArrayList<AuthData>();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM authData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String token = rs.getString("authToken");
                        var currAuthData = new AuthData(token, username);
                        auths.add(currAuthData);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return auths;
    }

    @Override
    public void removeAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    DELETE FROM authData WHERE authToken = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearDB() {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    TRUNCATE TABLE authData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean dbContains(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM authData WHERE authToken = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
