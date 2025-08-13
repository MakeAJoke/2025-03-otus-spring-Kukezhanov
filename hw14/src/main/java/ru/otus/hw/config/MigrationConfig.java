package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;

@Configuration
public class MigrationConfig {

    public static final String MIGRATE_JOB_NAME = "migrateJob";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    @StepScope
    public JpaPagingItemReader<Author> authorReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM author a")
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Genre> genreReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT g FROM genre g")
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Book> bookReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT DISTINCT b FROM book b
                        LEFT JOIN FETCH b.author
                        LEFT JOIN FETCH b.genres
                        """
                )
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Comment> commentReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("commentReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM comment c")
                .pageSize(100)
                .build();
    }

    @Bean
    public Step authorMigrationStep(ItemReader<Author> authorReader,
                                    ItemWriter<MongoAuthor> authorWriter,
                                    ItemProcessor<Author, MongoAuthor> authorProcessor) {
        return new StepBuilder("authorMigration", jobRepository)
                .<Author, MongoAuthor>chunk(10, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    @Bean
    public Step genreMigrationStep(ItemReader<Genre> genreReader,
                                   ItemWriter<MongoGenre> genreWriter,
                                   ItemProcessor<Genre, MongoGenre> genreProcessor) {
        return new StepBuilder("genreMigration", jobRepository)
                .<Genre, MongoGenre>chunk(10, platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Step bookMigrationStep(ItemReader<Book> bookReader,
                                  ItemWriter<MongoBook> bookWriter,
                                  ItemProcessor<Book, MongoBook> bookProcessor) {
        return new StepBuilder("bookMigration", jobRepository)
                .<Book, MongoBook>chunk(10, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public Step commentMigrationStep(ItemReader<Comment> commentReader,
                                  ItemWriter<MongoComment> commentWriter,
                                  ItemProcessor<Comment, MongoComment> commentProcessor) {
        return new StepBuilder("commentMigration", jobRepository)
                .<Comment, MongoComment>chunk(10, platformTransactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
                .build();
    }

    @Bean
    public Flow authorFlow(Step authorMigrationStep) {
        return new FlowBuilder<Flow>("authorMigrationFlow")
                .start(authorMigrationStep)
                .build();
    }

    @Bean
    public Flow genreFlow(Step genreMigrationStep) {
        return new FlowBuilder<Flow>("genreMigrationFlow")
                .start(genreMigrationStep)
                .build();
    }

    @Bean
    public Flow parallelAuthorsGenresFlow(TaskExecutor parallelTaskExecutor,
                                          Flow authorFlow,
                                          Flow genreFlow) {
        // Запустить author и genre ПАРАЛЛЕЛЬНО и дождаться обоих
        return new FlowBuilder<Flow>("authorsGenresParallel")
                .split(parallelTaskExecutor)
                .add(authorFlow, genreFlow)
                .build();
    }

    @Bean
    public Job migrateJob(Flow parallelAuthorsGenresFlow, Step bookMigrationStep, Step commentMigrationStep) {
        return new JobBuilder(MIGRATE_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(parallelAuthorsGenresFlow)
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .end()
                .build();
    }

}