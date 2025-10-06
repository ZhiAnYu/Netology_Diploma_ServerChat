package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientThread implements Runnable {
    private final Socket socket;
    private final Logger logger;
    private PrintWriter writer;
    private String username;

    public ClientThread(Socket socket, Logger logger) {
        this.socket = socket;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                     StandardCharsets.UTF_8),
                     true)) {
            this.writer = writer;

            // 1. Получаем имя пользователя (первая строка)
            username = reader.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }

            // 2. Добавляем в список рассылки
            ChatServer.clientWriters.add(writer);
            logger.log("Пользователь подключился: " + username);

            // 3. Основной цикл: читаем сообщения
            String message;
            while ((message = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                logger.log("Сообщение: " + username + ": " + message);

                // Рассылаем всем подключённым клиентам
                for (PrintWriter w : ChatServer.clientWriters) {
                    w.println(username + ": " + message);
                }
            }

        } catch (IOException e) {
            logger.log("Ошибка: " + e.getMessage());
        } finally {
            // Удаляем из списка и закрываем ресурсы
            if (writer != null) {
                ChatServer.clientWriters.remove(writer);
            }
            try {
                socket.close();
            } catch (IOException e) {
                logger.log("Ошибка при закрытии сокета: " + e.getMessage());
            }
            logger.log("Пользователь отключился: " + username);
        }
    }
}
