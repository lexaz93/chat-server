package services;

import dao.UserDao;
import dao.UserDaoImpl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import model.User;

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

        String messageFromUser = "";
        while (!clientSocket.isClosed() && (messageFromUser = serverMessageReceiver.readMessage()) != null) {
            if (messageFromUser.contains("Registration")) {
                registration(messageFromUser);
            } else if (messageFromUser.contains("Authorization")) {
                authorization(messageFromUser);
            } else {
                break;
            }
        }

        if (!clientSocket.isClosed()) {
            do {
                System.out.println(messageFromUser);
                server.notifyObservers(messageFromUser);
            } while ((messageFromUser = serverMessageReceiver.readMessage()) != null);
        }

    }

    @SneakyThrows
    private void authorization(String messageFromUser) {
        String loginFromClient = messageFromUser.split(" ")[1];
        String passwordFromClient = messageFromUser.split(" ")[2];

        User userFromDao;
        if ((userFromDao = dao.findByName(loginFromClient)) != null) {
            if (userFromDao.getPassword().equals(passwordFromClient)) {
                client = userFromDao;
                notifyObserver("Authorization successfully");
                System.out.println("Authorization for " + client.getName() + " failed");
            } else {
                System.out.println("Authorization for " + loginFromClient + " failed");
                notifyObserver("Authorization failed: wrong password");
                server.deleteObserver(this);
                clientSocket.close();
            }
        } else {
            System.out.println("Authorization for " + loginFromClient + " success");
            notifyObserver("Authorization failed: wrong name");
            server.deleteObserver(this);
            clientSocket.close();
        }
    }

    @SneakyThrows
    private void registration(String messageFromUser) {
        if (dao.findByName(messageFromUser.split(" ")[1]) != null) {
            System.out.println("Registration for " + messageFromUser.split(" ")[1] + " failed");
            notifyObserver("Registration failed: wrong name");
            server.deleteObserver(this);
            clientSocket.close();
        } else {
            client = new User(messageFromUser.split(" ")[1], messageFromUser.split(" ")[2]);
            System.out.println("Registration for " + client.getName() + " success");
            notifyObserver("Registration successful");
            dao.createUser(client);
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
