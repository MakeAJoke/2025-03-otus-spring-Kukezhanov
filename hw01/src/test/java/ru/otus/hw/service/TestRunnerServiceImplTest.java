package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.datasource.FileDataSource;
import ru.otus.hw.datasource.ResourceFileDataSource;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class TestRunnerServiceImplTest {

    private TestRunnerService testRunnerService;
    private PrintStream printStream;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        printStream = new PrintStream(outContent);
        IOService ioService = new StreamsIOService(printStream);
        FileDataSource fileDataSource = new ResourceFileDataSource();
        TestFileNameProvider fileNameProvider = new AppProperties("testQuestions.csv");
        QuestionDao questionDao = new CsvQuestionDao(fileNameProvider, fileDataSource);
        TestService testService = new TestServiceImpl(ioService, questionDao);
        testRunnerService = new TestRunnerServiceImpl(testService);
    }

    @Test
    void shouldPrintQuestionsAndAnswers() {
        testRunnerService.run();
        List<String> outLines = outContent.toString().lines().toList();
        List<String> expectedLines = List.of(
                "",
                "Please answer the questions below",
                "",
                "1. First question:",
                "  A. Answer 1",
                "  B. Answer 2",
                "  C. Answer 3",
                "",
                "2. Second question:",
                "  A. Answer 4",
                "  B. Answer 5",
                "  C. Answer 6",
                ""
        );
        assertLinesMatch(outLines, expectedLines);
    }

}