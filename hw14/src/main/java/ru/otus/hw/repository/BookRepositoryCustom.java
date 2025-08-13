package ru.otus.hw.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.mongo.MongoBook;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public Optional<MongoBook> findByTitleAuthorAndAllGenres(String title, String authorId, List<String> genreIds) {
        Criteria byTitle  = Criteria.where("title").is(title);
        Criteria byAuthor = Criteria.where("author._id").is(authorId); // или new ObjectId(authorId)

        Criteria[] mustHave = genreIds.stream()
                .map(gid -> Criteria.where("genres").elemMatch(Criteria.where("_id").is(gid)))
                .toArray(Criteria[]::new);

        Query q = new Query(new Criteria().andOperator(byTitle, byAuthor, new Criteria().andOperator(mustHave)));
        return Optional.ofNullable(mongoTemplate.findOne(q, MongoBook.class));
    }
}
