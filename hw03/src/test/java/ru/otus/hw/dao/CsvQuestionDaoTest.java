package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.datasource.FileDataSource;
import ru.otus.hw.datasource.ResourceFileDataSource;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.LocalizedIOServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {

    private QuestionDao questionDao;
    private TestFileNameProvider testFileNameProvider;
    private FileDataSource fileDataSource;
    private LocalizedIOService ioService;

    @BeforeEach
    void setUp() {
        testFileNameProvider = mock(AppProperties.class);
        ioService = mock(LocalizedIOServiceImpl.class);
        fileDataSource = new ResourceFileDataSource(ioService);
        questionDao = new CsvQuestionDao(testFileNameProvider, fileDataSource, ioService);
    }

    @Test
    void shouldReturnQuestionListFromCsvFile_whenCorrectFilePathIsPassed() {
        List<Question> expectedQuestions = List.of(
                new Question("Question_1", List.of(
                        new Answer("Answer_1", true),
                        new Answer("Answer_2", false),
                        new Answer("Answer_3", false)
                )),
                new Question("Question_2", List.of(
                        new Answer("Answer_4", false),
                        new Answer("Answer_5", true),
                        new Answer("Answer_6", false)
                )),
                new Question("Question_3", List.of(
                        new Answer("Answer_7", false),
                        new Answer("Answer_8", false),
                        new Answer("Answer_9", true)
                ))
        );
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");

        List<Question> actualQuestions = questionDao.findAll();

        assertEquals(expectedQuestions, actualQuestions);
    }

    @Test
    void shouldThrowQuestionReadException_whenParsingWrongFile() {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions_with_errors.csv");
        assertThrows(QuestionReadException.class, () -> questionDao.findAll());

    }
}
