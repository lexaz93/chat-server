package services;

import lombok.SneakyThrows;

import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer implements Observable {
    public static final int PORT = 8080;
    private volatile static List<Observer> clients = new ArrayList<>();
    public static final Logger log = Logger.getLogger(MyServer.class);


    @SneakyThrows
    public void start() {
        System.out.println("--- START SERVER ---");
        ServerSocket serverSocket = new ServerSocket(PORT);
        log.debug("Server started!");


        while (true) {
            Socket socket = serverSocket.accept();

            if (socket != null) {
                new Thread(new ClientRunnable(socket, this)).start();
                log.debug("Socket created!");
            }
        }
    }

    @Override
    public void addObserver(Observer o) {
        clients.add(o);
    }

    @Override
    public void deleteObserver(Observer o) {
        clients.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer client: clients) {
            client.notifyObserver(message);
        }
    }
}
