package services;

import dao.MessageDao;
import dao.MessageDaoImpl;
import dao.UserDao;
import dao.UserDaoImpl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import model.Message;
import model.User;

import java.io.PrintWriter;
import java.net.Socket;

import static services.MyServer.LOGGER;
import static services.MyServer.RESOURCE_BUNDLE;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket clientSocket;
    private final MyServer server;
    private User client;
    private Message message;
    private final UserDao dao = new UserDaoImpl();
    private final MessageDao messageDao = new MessageDaoImpl();

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
                server.notifyObservers(client.getName() + " " + RESOURCE_BUNDLE.getValue("story") + ":\n" + messageDao.readMessage(client).toString());
//                server.notifyObservers(client.getName() + " story:\n" + messageDao.readMessage(client).toString());
            } else {
                break;
            }
        }

        if (!clientSocket.isClosed()) {
            do {
                System.out.println(messageFromUser);
                server.notifyObservers(client.getName() + ": " + messageFromUser);
                message = new Message(messageFromUser, client);
                messageDao.writeMessage(message);
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
                notifyObserver(RESOURCE_BUNDLE.getValue("authorizaition_for") + " " + client.getName() + " " + RESOURCE_BUNDLE.getValue("successful"));
//                notifyObserver("Authorization for " + client.getName() + " successful");
                System.out.println(RESOURCE_BUNDLE.getValue("authorizaition_for") + " " + client.getName() + " " + RESOURCE_BUNDLE.getValue("successful"));
                LOGGER.info("Authorization for " + client.getName() + " success");
//                System.out.println("Authorization for " + client.getName() + " successful");
            } else {
                System.out.println(RESOURCE_BUNDLE.getValue("authorizaition_for") + " " + loginFromClient + " " + RESOURCE_BUNDLE.getValue("failed"));
//                System.out.println("Authorization for " + loginFromClient + " failed");
                notifyObserver(RESOURCE_BUNDLE.getValue("auth_f_w_p"));
                LOGGER.warn("Authorization failed: wrong password");
//                notifyObserver("Authorization failed: wrong password");
                server.deleteObserver(this);
                clientSocket.close();
            }
        } else {
            System.out.println(RESOURCE_BUNDLE.getValue("authorizaition_for") + " " + loginFromClient + " " + RESOURCE_BUNDLE.getValue("failed"));
//            System.out.println("Authorization for " + loginFromClient + " failed");
            notifyObserver(RESOURCE_BUNDLE.getValue("auth_f_w_n"));
            LOGGER.warn("Authorization failed: wrong name");
//            notifyObserver("Authorization failed: wrong name");
            server.deleteObserver(this);
            clientSocket.close();
        }
    }

    @SneakyThrows
    private void registration(String messageFromUser) {
        if (dao.findByName(messageFromUser.split(" ")[1]) != null) {
            System.out.println(RESOURCE_BUNDLE.getValue("registration_for") + " " + messageFromUser.split(" ")[1] + " " + RESOURCE_BUNDLE.getValue("failed"));
//            System.out.println("Registration_for " + messageFromUser.split(" ")[1] + " failed");
            notifyObserver(RESOURCE_BUNDLE.getValue("reg_f_w_n"));
            LOGGER.warn("Registration for failed: wrong name");
//            notifyObserver("Registration failed: wrong name");
            server.deleteObserver(this);
            clientSocket.close();
        } else {
            client = new User(messageFromUser.split(" ")[1], messageFromUser.split(" ")[2]);
            System.out.println(RESOURCE_BUNDLE.getValue("registration_for") + " " + client.getName() + " " + RESOURCE_BUNDLE.getValue("successful"));
//            System.out.println("Registration for " + client.getName() + " success");
            notifyObserver(RESOURCE_BUNDLE.getValue("registration_for") + " " + client.getName() + " " + RESOURCE_BUNDLE.getValue("successful"));
            LOGGER.info("Registration for " + client.getName() + " success");
//            notifyObserver("Registration for " + client.getName() + " success");
            dao.createUser(client);
        }
    }


    @SneakyThrows
    @Override
    public void notifyObserver(String message) {
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
        printWriter.println(message);
        printWriter.flush();
    }
}
