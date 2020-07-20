package dao;

import model.Message;
import model.User;

import java.sql.*;

public class MessageDaoImpl implements MessageDao {
    private final UserDao dao = new UserDaoImpl();

    @Override
    public void writeMessage(Message message) {
        try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate("insert into message (text, idUser) values ('" + message.getText() + "','" + message.getUser().getName() + "');");
            System.out.println("User add into database ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readMessage(String text) {
        try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select text, idUser from message")
        ) {
            while (resultSet.next()) {
                if (text.equalsIgnoreCase(resultSet.getString("text"))) {
                    return new Message(text, dao.findByName(resultSet.getString("idUser")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StringBuffer readMessage(User user) {
        StringBuffer messages = new StringBuffer();
        try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select text, idUser from message")
        ) {
            while (resultSet.next()) {
                if (user.getName().equals(resultSet.getString("idUser"))) {
                    messages.append(resultSet.getString("text"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
