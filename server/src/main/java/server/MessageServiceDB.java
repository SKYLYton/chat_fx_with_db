package server;

import java.sql.*;

public class MessageServiceDB {
    private Connection connection;
    private Statement stmt;
    private PreparedStatement psInsert;
    private Server server;

    public MessageServiceDB(Server server) {
        this.server = server;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    private void disconnect() {
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

    public void saveMsg(String date, String message, String fromLogin){
        try {
            connect();

            psInsert = connection.prepareStatement("INSERT INTO messages (date, message, toLogin, fromLogin) VALUES (?, ?, 'all', ?);");
            psInsert.setString(1, date);
            psInsert.setString(2, message);
            psInsert.setString(3, fromLogin);

            psInsert.executeUpdate();

            psInsert.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void savePrivateMsg(String date, String message, String toLogin, String fromLogin){
        try {
            connect();

            psInsert = connection.prepareStatement("INSERT INTO messages (date, message, toLogin, fromLogin) VALUES (?, ?, ?, ?);");
            psInsert.setString(1, date);
            psInsert.setString(2, message);
            psInsert.setString(3, toLogin);
            psInsert.setString(4, fromLogin);


            psInsert.executeUpdate();

            psInsert.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public String getMessages(ClientHandler clientHandler){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connect();

            ResultSet rs = stmt.executeQuery("SELECT date, message, toLogin, fromLogin FROM messages;");



            while (rs.next()) {
                String date = rs.getString("date");
                String message = rs.getString("message");
                String toL = rs.getString("toLogin");
                String fromL = rs.getString("fromLogin");

                if(toL.equals("all")){
                    stringBuilder.append(String.format("%s %s : %s", date, server.getNickname(fromL), message)).append("\n");
                } else if(toL.equals(clientHandler.getLogin()) || fromL.equals(clientHandler.getLogin())){
                    stringBuilder.append(String.format("[%s] private [%s] : %s", server.getNickname(fromL), server.getNickname(toL), message)).append("\n");
                }
            }
            rs.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        stringBuilder.append("---История успешно загружена---\n");

        return stringBuilder.toString();
    }

}
