package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        showAllQuestions(questionDao.findAll());
    }

    private void showAllQuestions(List<Question> questionList) {
        for (int questionCount = 0; questionCount < questionList.size(); questionCount++) {
            Question question = questionList.get(questionCount);
            ioService.printFormattedLine("%s. %s:", questionCount+1, question.text());
            List<Answer> answerList = question.answers();
            for (int answerCount = 0; answerCount < answerList.size(); answerCount++) {
                Answer answer = answerList.get(answerCount);
                ioService.printFormattedLine("  %s. %s", (char) (answerCount+'A'), answer.text());
            }
            ioService.printLine("");
        }
    }
}
