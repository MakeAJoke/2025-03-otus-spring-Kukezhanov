package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            showQuestion(question);
            var isAnswerValid = checkUserAnswer(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void showQuestion(Question question) {
        ioService.printFormattedLine("%s:", question.text());
        List<Answer> answerList = question.answers();
        for (int answerCount = 0; answerCount < answerList.size(); answerCount++) {
            Answer answer = answerList.get(answerCount);
            ioService.printFormattedLine("  %s. %s", answerCount + 1, answer.text());
        }
        ioService.printLine("");
    }

    private boolean checkUserAnswer(Question question) {
        List<Answer> answers = question.answers();
        String inputAnswerNumberMessage = ioService.getMessage("TestService.input.answer.number", answers.size());
        String errorAnswerNumberInputMessage = ioService.getMessage("TestService.invalid.answer.number.message");
        int answerNumber = ioService.readIntForRangeWithPrompt(
                1,
                answers.size(),
                inputAnswerNumberMessage,
                errorAnswerNumberInputMessage);
        ioService.printLine("");
        Answer answer = answers.get(answerNumber - 1);
        return answer.isCorrect();
    }
}
