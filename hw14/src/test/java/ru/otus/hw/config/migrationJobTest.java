package ru.otus.hw.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.config.MigrationConfig.MIGRATE_JOB_NAME;

@SpringBootTest
@SpringBatchTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
public class migrationJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldMigrateAllSqlDatabaseToMongo() throws Exception {

        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(MIGRATE_JOB_NAME);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        List<MongoAuthor> allAuthors = mongoTemplate.findAll(MongoAuthor.class);
        List<MongoGenre> allGenres = mongoTemplate.findAll(MongoGenre.class);
        List<MongoBook> allBooks = mongoTemplate.findAll(MongoBook.class);
        List<MongoComment> allComments = mongoTemplate.findAll(MongoComment.class);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
        assertThat(allAuthors).hasSize(3);
        assertThat(allGenres).hasSize(6);
        assertThat(allBooks).hasSize(3);
        assertThat(allComments).hasSize(6);
    }
}
