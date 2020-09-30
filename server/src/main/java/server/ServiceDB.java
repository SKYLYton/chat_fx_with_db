package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDB {

    private Connection connection;
    private Statement stmt;
    private PreparedStatement psInsert;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    public void disconnect() {
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

    public void saveMsg(String date, String message, String recipient, String sender){
        try {

            psInsert = connection.prepareStatement("INSERT INTO messages (date, message, recipient, sender) VALUES (?, ?, (SELECT id FROM users WHERE nick=?), (SELECT id FROM users WHERE nick=?));");
            psInsert.setString(1, date);
            psInsert.setString(2, message);
            psInsert.setString(3, recipient);
            psInsert.setString(4, sender);


            psInsert.executeUpdate();

            psInsert.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMessages(ClientHandler clientHandler){
        StringBuilder stringBuilder = new StringBuilder();
        try {

            ResultSet rs = stmt.executeQuery("SELECT date, message, (SELECT nick FROM users WHERE id = recipient), (SELECT nick FROM users WHERE id = sender) FROM messages;");


            while (rs.next()) {
                String date = rs.getString(1);
                String message = rs.getString(2);
                String recipient = rs.getString(3);
                String sender = rs.getString(4);

                if(recipient == null || recipient.equals("null")){
                    stringBuilder.append(String.format("%s %s : %s", date, sender, message)).append("\n");
                } else {
                    stringBuilder.append(String.format("[%s] private [%s] : %s", sender, recipient, message)).append("\n");
                }
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        stringBuilder.append("---История успешно загружена---\n");

        return stringBuilder.toString();
    }

    public boolean registration(String login, String password, String nickname) {

        try {

            psInsert = connection.prepareStatement("INSERT INTO users (login, password, nick) VALUES (?, ?, ?);");
            psInsert.setString(1, login);
            psInsert.setString(2, password);
            psInsert.setString(3, nickname);

            psInsert.executeUpdate();

            psInsert.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean changeNick(ClientHandler clientHandler, String login, String newNick) {

        if(clientHandler.getNickname().equals(newNick)){
            return false;
        }

        try {

            psInsert = connection.prepareStatement("UPDATE users SET nick = ? WHERE login = ?;");
            psInsert.setString(1, newNick);
            psInsert.setString(2, login);


            psInsert.executeUpdate();

            psInsert.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            psInsert = connection.prepareStatement("SELECT nick FROM users WHERE password = ? AND login = ?;");
            psInsert.setString(1, password);
            psInsert.setString(2, login);
            ResultSet rs = psInsert.executeQuery();

            if (rs.next()) {
                return rs.getString("nick");
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
