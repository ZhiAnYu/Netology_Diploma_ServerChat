package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ChatClient {
    private static final String SETTINGS_FILE = "settings.txt";
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Logger logger;

    public static void main(String[] args) {
        new ChatClient().start();
    }

    public void start() {
        logger = new FileLogger("file.log");

        // Читаем порт
        int port = readPort(logger);
        if (port == -1) {
            System.err.println("Ошибка запуска");
            return;
        }

        // Ввод имени
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ваше имя для чата: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "Anonymous";
        } ;
        logger.log("Выбрано имя: " + username);

        try {
            // 3. Подключаемся к localhost:port
            socket = new Socket("localhost", port);
            logger.log("Подключение к серверу localhost:" + port);

            // 4. Настраиваем потоки
            writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true
            );
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );

            // 5. Отправляем имя (первая строка!)
            writer.println(username);

            // 6. Запускаем поток приёма
            Thread readerThread = new Thread(this::receiveMessages);
            readerThread.setDaemon(true);
            readerThread.start();

            // 7. Основной цикл ввода
            System.out.println("Введите сообщение или '/exit' для выхода.");
            while (true) {
                String message = scanner.nextLine();
                if ("/exit".equalsIgnoreCase(message.trim())) {
                    logger.log("Пользователь ввёл команду выхода");
                    break;
                }
                writer.println(message);
                logger.log("Отправлено: " + username + ": " + message);
            }

        } catch (IOException e) {
            logger.log("Ошибка подключения: " + e.getMessage());
        } finally {
            closeConnection();
            scanner.close();
        }
    }

    // метод чтения порта
    private static int readPort(Logger logger) {
        try {
            String content = Files.readString(Paths.get(SETTINGS_FILE)).trim();
            return Integer.parseInt(content);
        } catch (IOException e) {
            logger.log("Не удалось прочитать " + SETTINGS_FILE + ": " + e.getMessage());
            return -1;
        } catch (NumberFormatException e) {
            logger.log("Неверный формат порта в " + SETTINGS_FILE);
            return -1;
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
                logger.log("Получено: " + message);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                logger.log("Соединение с сервером потеряно: " + e.getMessage());
            }
        }
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.log("Соединение закрыто");
            }
        } catch (IOException e) {
            logger.log("Ошибка при закрытии сокета: " + e.getMessage());
        }
    }
}
