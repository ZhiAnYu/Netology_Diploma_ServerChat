package ru.netology;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Записывает сообщения в файл.
//Работает корректно при нескольких вызовах.
//Является Singleton'ом (опционально, но полезно).


class FileLoggerTest {
    private Path tempLogFile;
    private FileLogger logger;


    @BeforeEach
    void setUp() throws IOException {
        // Создаём временный файл для лога — уникальный для каждого теста
        tempLogFile = Files.createTempFile("test-log-", ".log");
        logger = new FileLogger(tempLogFile.toString()); // используем тестовый конструктор
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаляем временный файл после теста
        Files.deleteIfExists(tempLogFile);
    }

    @Test
    @DisplayName("Запись появляется в файле")
    void logTest_whenLog_thenWriteToLogFile() throws IOException {
        // arrange
        String message = "Тестовое сообщение";
        // act
        logger.log(message);

        List<String> lines = Files.readAllLines(tempLogFile);
        // assert
        assertEquals(1, lines.size());
        String logLine = lines.get(0);
        assertTrue(logLine.contains(message));
    }

    @Test
    @DisplayName("Проверяем режим append")
    void logTest_whenLog_thenNewMessage() throws IOException {
        // arrange
        logger.log("Первое");
        logger.log("Второе");
        // act
        List<String> lines = Files.readAllLines(tempLogFile);
        // assert
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("Первое"));
        assertTrue(lines.get(1).contains("Второе"));
    }

 //   @Test
 //   @DisplayName("Проверяем Singletone")
 //   void singletonPatternWorks() {
 //       // arrange
 //       FileLogger instance1 = FileLogger.getInstance();
 //       FileLogger instance2 = FileLogger.getInstance();
 //
 //      // assert
 //      assertSame(instance1, instance2);
 //  }

}