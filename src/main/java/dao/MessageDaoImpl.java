package dao;

import lombok.SneakyThrows;
import model.Message;
import model.User;
import utils.Props;

import java.sql.*;

public class MessageDaoImpl implements MessageDao {
    private final UserDao dao = new UserDaoImpl();
    private final static String DB_URL = Props.getValueFromProperties("db.url");
    private final static String DB_USER = Props.getValueFromProperties("db.user");
    private final static String DB_PASSWORD = Props.getValueFromProperties("db.password");

    @SneakyThrows
    @Override
    public void writeMessage(Message message) {
        getStatement().executeUpdate("insert into message (text, idUser) values ('" + message.getText() + "','" + message.getUser().getName() + "');");
        System.out.println("Message add into database ");

    }

    @SneakyThrows
    @Override
    public Message readMessage(String text) {
        ResultSet resultSet = getStatement().executeQuery("select text, idUser from message");
        while (resultSet.next()) {
            if (text.equalsIgnoreCase(resultSet.getString("text"))) {
                return new Message(text, dao.findByName(resultSet.getString("idUser")));
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public StringBuilder readMessage(User user) {
        StringBuilder messages = new StringBuilder();
        ResultSet resultSet = getStatement().executeQuery("select text, idUser from message");
        while (resultSet.next()) {
            if (user.getName().equals(resultSet.getString("idUser"))) {
                messages.append("- " + resultSet.getString("text") + " -\n");
            }
        }
        return messages;
    }

    private Statement getStatement() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return connection.createStatement();
    }
}
