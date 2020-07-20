package dao;

import model.User;

public interface UserDao {

    User findByName(String name);
    void setName(String oldName, String newName, String password);
    void setPassword(String name, String oldPassword, String newPassword);
    void createUser(User user);
    void deleteUser(User user);
}
