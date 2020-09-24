package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthServiceDB implements AuthService {
    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;
    List<UserData> users;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    private static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SimpleAuthServiceDB() {
        users = new ArrayList<>();

        try {
            connect();

            ResultSet rs = stmt.executeQuery("SELECT login, password, nick FROM users;");
            while (rs.next()) {
                users.add(new UserData(rs.getString("login"), rs.getString("password"), rs.getString("nick")));
            }
            rs.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        for (UserData user : users) {
            if (user.login.equals(login) || user.nickname.equals(nickname)) {
                return false;
            }
        }

        users.add(new UserData(login, password, nickname));

        try {
            connect();

            psInsert = connection.prepareStatement("INSERT INTO users (login, password, nick) VALUES (?, ?, ?);");
            psInsert.setString(1, login);
            psInsert.setString(2, password);
            psInsert.setString(3, nickname);

            psInsert.executeUpdate();

            psInsert.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        return true;
    }

    @Override
    public boolean changeNick(String login, String newNick) {

        for (UserData user : users) {
            if (user.nickname.equals(newNick)) {
                return false;
            }
        }

        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).login.equals(login)){
                users.get(i).setNickname(newNick);
                break;
            }
        }

        try {
            connect();

            psInsert = connection.prepareStatement("UPDATE users SET nick = ? WHERE login = ?;");
            psInsert.setString(1, newNick);
            psInsert.setString(2, login);


            psInsert.executeUpdate();

            psInsert.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        return true;
    }
}
