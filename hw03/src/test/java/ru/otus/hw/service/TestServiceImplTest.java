package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestServiceImplTest {

    private TestServiceImpl testServiceImpl;
    private LocalizedIOService ioService;
    private QuestionDao questionDao;

    @BeforeEach
    void setUp() {
        ioService = mock(LocalizedIOServiceImpl.class);
        questionDao = mock(CsvQuestionDao.class);
        testServiceImpl = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldReturnQuestionResultWithCorrectAnswerNumber() {
        List<Question> questions = List.of(
                new Question("Question 1", List.of(
                        new Answer("Answer 1", true),
                        new Answer("Answer 2", false)
                )),
                new Question("Question 2", List.of(
                        new Answer("Answer 3", false),
                        new Answer("Answer 4", true)
                )),
                new Question("Question 3", List.of(
                        new Answer("Answer 5", false),
                        new Answer("Answer 6", false),
                        new Answer("Answer 7", true)
                ))
        );
        Student student = new Student("Test", "student");
        TestResult expectedResult = new TestResult(student);
        expectedResult.getAnsweredQuestions().addAll(questions);
        expectedResult.setRightAnswersCount(3);
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.getMessage("TestService.input.answer.number", 2)).thenReturn("Enter answer number from 1 to 2");
        when(ioService.getMessage("TestService.input.answer.number", 3)).thenReturn("Enter answer number from 1 to 3");
        when(ioService.getMessage("TestService.invalid.answer.number.message")).thenReturn("Invalid answer number. Please, enter again");
        when(ioService.readIntForRangeWithPrompt(
                eq(1),
                eq(2),
                eq("Enter answer number from 1 to 2"),
                eq("Invalid answer number. Please, enter again"))).thenReturn(1,2);
        when(ioService.readIntForRangeWithPrompt(
                eq(1),
                eq(3),
                eq("Enter answer number from 1 to 3"),
                eq("Invalid answer number. Please, enter again"))).thenReturn(3);

        TestResult actualResult = testServiceImpl.executeTestFor(student);

        assertEquals(expectedResult, actualResult);
    }
}