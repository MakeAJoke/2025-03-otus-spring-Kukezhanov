package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.datasource.FileDataSource;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {

    private final TestFileNameProvider fileNameProvider;

    private final FileDataSource fileDataSource;

    private final LocalizedIOService ioService;

    @Override
    public List<Question> findAll() {
        List<QuestionDto> questionDtoList;
        try (InputStream fileInputStream = fileDataSource.getInputStream(fileNameProvider.getTestFileName());
             InputStreamReader reader = new InputStreamReader(fileInputStream)) {
            questionDtoList = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class).withSeparator(';').withSkipLines(1).build().parse();
        } catch (Exception e) {
            String msg = ioService.getMessage("QuestionReadException.message", e.getMessage());
            throw new QuestionReadException(msg, e);
        }

        return questionDtoList.stream()
                .map(QuestionDto::toDomainObject)
                .toList();
    }
}
