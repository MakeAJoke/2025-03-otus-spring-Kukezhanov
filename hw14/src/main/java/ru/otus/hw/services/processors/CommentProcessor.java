package ru.otus.hw.services.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.services.caches.BookCacheService;

@RequiredArgsConstructor
@StepScope
@Service
public class CommentProcessor implements ItemProcessor<Comment, MongoComment> {

    private final BookCacheService bookCacheService;

    @Override
    public MongoComment process(Comment comment) throws Exception {
        String dbRefId = bookCacheService.get(comment.getBook().getId());
        MongoBook bookDBRef = new MongoBook(dbRefId);
        return new MongoComment(null, comment.getText(), bookDBRef, comment.getId());
    }
}
