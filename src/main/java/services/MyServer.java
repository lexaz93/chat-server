package services;

import lombok.SneakyThrows;

import org.apache.log4j.Logger;
import utils.MyResourceBundle;
import utils.Props;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyServer implements Observable {
    public static final int PORT = 8080;
    private volatile static List<Observer> clients = new ArrayList<>();
    public static final Logger LOGGER = Logger.getLogger(MyServer.class);
    public static  final MyResourceBundle RESOURCE_BUNDLE = new MyResourceBundle(new Locale(Props.getValueFromProperties("language"), Props.getValueFromProperties("country")));


    @SneakyThrows
    public void start() {
        System.out.println("--- " + RESOURCE_BUNDLE.getValue("start") + " ---");
//        System.out.println("--- START SERVER ---");
        ServerSocket serverSocket = new ServerSocket(PORT);
        LOGGER.debug("Server started!");


        while (true) {
            Socket socket = serverSocket.accept();

            if (socket != null) {
                new Thread(new ClientRunnable(socket, this)).start();
                LOGGER.debug("Socket created!");
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
        for (Observer client : clients) {
            client.notifyObserver(message);
        }
    }
}
