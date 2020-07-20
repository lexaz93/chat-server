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
                if (name.equalsIgnoreCase(resultSet.getString("login"))) {
                    return new User(name, resultSet.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setName(String oldName, String newName, String password) {
        if (password.equals(findByName(oldName).getPassword())) {
            try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
                 Statement statement = connection.createStatement()
            ) {
                statement.executeUpdate("update user set login = '" + newName + "' where login = '" + oldName + "';");
                System.out.println("Login changed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPassword(String name, String oldPassword, String newPassword) {
        if (oldPassword.equals(findByName(name).getPassword())) {
            try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
                 Statement statement = connection.createStatement()
            ) {
                statement.executeUpdate("update user set password = '" + newPassword + "' where password = '" + oldPassword + "';");
                System.out.println("Password changed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createUser(User user) {
        if (findByName(user.getName()) == null) {
            try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
                 Statement statement = connection.createStatement()
            ) {
                statement.executeUpdate("insert into user (login, password) values ('" + user.getName() + "','" + user.getPassword() + "');");
                System.out.println("User add into database ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteUser(User user) {
        if (user.getPassword().equals(findByName(user.getName()).getPassword())) {
            try (Connection connection = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC", "root", "root");
                 Statement statement = connection.createStatement()
            ) {
                statement.executeUpdate("delete from user where login = '" + user.getName() + "';");
                System.out.println("User delete from database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
