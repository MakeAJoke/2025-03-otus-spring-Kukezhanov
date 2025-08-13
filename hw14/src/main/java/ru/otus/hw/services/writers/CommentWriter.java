package ru.otus.hw.services.writers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.repository.CommentRepository;
import ru.otus.hw.services.caches.CommentCacheService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@StepScope
public class CommentWriter implements ItemWriter<MongoComment> {

    private final CommentRepository commentRepository;

    private final CommentCacheService commentCacheService;

    @Override
    public void write(Chunk<? extends MongoComment> chunk) throws Exception {
        chunk.getItems().forEach(comment -> {
            if (commentCacheService.get(comment.getLegacyId()) == null) {
                Optional<MongoComment> existingComment =
                        commentRepository.findByTextAndMongoBookId(comment.getText(), comment.getMongoBook().getId());
                if (existingComment.isEmpty()) {
                    commentRepository.save(comment);
                    commentCacheService.put(comment.getLegacyId(), comment.getId());
                }
            }
        });
    }
}
