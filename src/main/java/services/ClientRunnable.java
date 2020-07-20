package services;

import dao.UserDao;
import dao.UserDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket clientSocket;
    private final MyServer server;
    private User client;
    private final UserDao dao = new UserDaoImpl();

    @SneakyThrows
    @Override
    public void run() {
        server.addObserver(this);
        ServerMessageReceiver serverMessageReceiver = new ServerMessageReceiver(clientSocket.getInputStream());
        String messageFromUser;

        messageFromUser = serverMessageReceiver.readMessage();
        if (messageFromUser.contains("Registration")) {
            System.out.println("Registration");
            client = new User(messageFromUser.split(" ")[1], messageFromUser.split(" ")[2]);
            System.out.println("Registration for " + client.getName() + " success");
            notifyObserver("Registration successful");
        } else if (messageFromUser.contains("Authorization")) {
            String loginFromClient = messageFromUser.split(" ")[1];
            String passwordFromClient = messageFromUser.split(" ")[2];

            User userFromDao = dao.findByName(loginFromClient);

            if (userFromDao.getPassword().equals(passwordFromClient)) {
                client = userFromDao;
                notifyObserver("Authorization successfully");
                System.out.println("Authorization for " + client.getName() + " success");
            } else {
                notifyObserver("Authorization wrong password");
                System.out.println("Authorization wrong password");
            }
        }

        while ((messageFromUser = serverMessageReceiver.readMessage()) != null) {
                System.out.println(messageFromUser);
                server.notifyObservers(messageFromUser);
        }
    }


    @SneakyThrows
    @Override
    public void notifyObserver(String message) {
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
//        if (client != null) { //закомментили  чтобы приходило сообщение неверный пароль тк там мы не присваеваем к клиенту
        printWriter.println(message);
        printWriter.flush();
//        }
    }
}
