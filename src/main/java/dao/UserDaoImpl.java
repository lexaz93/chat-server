package dao;

import model.User;

import java.sql.*;

public class UserDaoImpl implements UserDao {
    @Override
    public User findByName(String name) {
        try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select login, password from user")
        ) {
            while (resultSet.next()) {
//                System.out.println(resultSet.getString("login") + " ");
                if (name.equalsIgnoreCase(resultSet.getString("login"))) {
                    return new User(name, resultSet.getString("password"));
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
