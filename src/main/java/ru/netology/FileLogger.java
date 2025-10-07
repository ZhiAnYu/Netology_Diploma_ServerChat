package ru.netology;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements Logger{
    private final String logFilename;

    public FileLogger(String logFilename) {
        this.logFilename = logFilename;
    }


    @Override
    public synchronized void log(String message) {
        try (FileWriter fw = new FileWriter(logFilename, true)) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fw.write("[" + timestamp + "] " + message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Ошибка записи в лог: " + e.getMessage());
        }
    }



//    private static final String DEFAULT_LOG_FILE = "file.log";
//    private final String logFilename;
//    private static volatile FileLogger instance;
//
//    private FileLogger() {
//        this(DEFAULT_LOG_FILE);
//    }
//    // Для тестов
//    FileLogger(String logFilename) {
//        this.logFilename = logFilename;
//    }
//
//    //код по занятию Многопоточные паттерны
//    public static FileLogger getInstance() {
//        if (instance == null) {
//            synchronized (FileLogger.class) {
//                if (instance == null) {
//                    instance = new FileLogger();
//                }
//            }
//        }
//        return instance;
//    }
//
//    @Override
//    //Гарантирует, что только один поток может выполнять этот метод в один момент времени.
//    public synchronized void log(String message) {
//        //Автоматически закрывает FileWriter после выхода из блока, даже если произошла ошибка.
//        //logFilename — имя файла (например, "file.log").
//        //Параметр true означает: режим дозаписи (append).
//        try (FileWriter fw = new FileWriter(logFilename, true)) {
//            //формат даты в сообщениях логгера
//            //Получает текущие дату и время на компьютере сервера.Преобразует объект времени в читаемую строку.
//            //Возвращает правильный символ новой строки для текущей ОС
//            String time = LocalDateTime.now()
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            fw.write("[" + time + "] " + message + System.lineSeparator());
//        } catch (IOException e) {
//            System.err.println("Ошибка записи в лог: " + e.getMessage());
//        }
//    }
}
