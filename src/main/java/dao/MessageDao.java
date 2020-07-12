package dao;

import model.Message;
import model.User;

public interface MessageDao {

    void writeMessage(Message message);
    Message readMessage(String text);
    StringBuffer readMessage(User name);
}
