package ru.netology;

import ru.netology.Logger;
import ru.netology.FileLogger;
import ru.netology.ClientThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    public static final String SETTINGS_FILE = "settings.txt";
    public static final CopyOnWriteArrayList<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        Logger logger = FileLogger.getInstance();

        int port = readPortFromSettings(logger);
        if (port == -1) {
            System.err.println("Не удалось запустить сервер.");
            return;
        }

        logger.log("Сервер запущен на порту " + port);
        //System.out.println("Сервер запущен на порту " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.log("Новое подключение: " + clientSocket.getRemoteSocketAddress());

                ClientThread handler = new ClientThread(clientSocket, logger);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            logger.log("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int readPortFromSettings(Logger logger) {
        try {
            String content = Files.readString(Paths.get(SETTINGS_FILE));
            return Integer.parseInt(content);
        } catch (IOException e) {
            logger.log("Ошибка чтения файла настроек: " + e.getMessage());
            return -1;
        } catch (NumberFormatException e) {
            logger.log("Неверный формат порта в " + SETTINGS_FILE);
            return -1;
        }
    }
}
