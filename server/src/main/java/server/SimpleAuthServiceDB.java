package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthServiceDB implements AuthService {
    private ServiceDB serviceDB;

    public SimpleAuthServiceDB(ServiceDB serviceDB) {
        this.serviceDB = serviceDB;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return serviceDB.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {

        return serviceDB.registration(login, password, nickname);
    }

    @Override
    public boolean changeNick(ClientHandler clientHandler, String login, String newNick) {
        return serviceDB.changeNick(clientHandler, login, newNick);
    }
}
