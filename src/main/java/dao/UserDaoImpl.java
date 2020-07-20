package dao;

import lombok.SneakyThrows;
import model.User;
import utils.Props;

import java.sql.*;

public class UserDaoImpl implements UserDao {
    private final static String DB_URL = Props.getValueFromProperties("db.url");
    private final static String DB_USER = Props.getValueFromProperties("db.user");
    private final static String DB_PASSWORD = Props.getValueFromProperties("db.password");

    @SneakyThrows
    @Override
    public User findByName(String name) {
        ResultSet resultSet = getStatement().executeQuery("select login, password from user");
        while (resultSet.next()) {
            if (name.equalsIgnoreCase(resultSet.getString("login"))) {
                return new User(name, resultSet.getString("password"));
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void setName(User user, String newName) {
        if (user.getPassword().equals(findByName(user.getName()).getPassword())) {
            getStatement().executeUpdate("update user set login = '" + newName + "' where login = '" + user.getName() + "';");
            System.out.println("Login changed");
        }
    }

    @SneakyThrows
    @Override
    public void setPassword(User user, String newPassword) {
        if (user.getPassword().equals(findByName(user.getName()).getPassword())) {
            getStatement().executeUpdate("update user set password = '" + newPassword + "' where password = '" + user.getPassword() + "';");
            System.out.println("Password changed");
        }
    }

    @SneakyThrows
    @Override
    public void createUser(User user) {
        if (findByName(user.getName()) == null) {
            getStatement().executeUpdate("insert into user (login, password) values ('" + user.getName() + "','" + user.getPassword() + "');");
            System.out.println("User add into database ");

        }
    }

    @SneakyThrows
    @Override
    public void deleteUser(User user) {
        if (user.getPassword().equals(findByName(user.getName()).getPassword())) {
            getStatement().executeUpdate("delete from user where login = '" + user.getName() + "';");
            System.out.println("User delete from database");

        }
    }

    private Statement getStatement() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return connection.createStatement();
    }
}
