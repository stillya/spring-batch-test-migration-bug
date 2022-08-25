package dataflow.dataconverter.batch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dataflow.dataconverter.configuration.ParquetToCsvJobConfiguration;
import dataflow.dataconverter.repositories.AbstractJooqTest;
import dataflow.dataconverter.tasklets.ParquetToCsvTasklet;
import java.io.File;
import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBatchTest
// UNCOMMENT THIS TO MAKE TEST GREEN!!!
//@ImportAutoConfiguration(BatchAutoConfiguration.class) 
@ContextConfiguration(classes = {ParquetToCsvJobConfiguration.class, TestBatchConfiguration.class,
    ParquetToCsvTasklet.class})
@TestPropertySource(properties = {
    "spring.batch.job.enabled=false",
    "spring.batch.jdbc.initialize-schema=always",
})
public class ParquetToCsvJobBatchTest extends AbstractJooqTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;

  @After
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  public void testParquetToDwhJob() throws Exception {
    // given
    var parquetFile = new File("src/test/resources/test.parquet");
    var csvfile = Files.newTemporaryFile();

    var jobParameters = new JobParametersBuilder()
        .addString("parquetFile", parquetFile.getAbsolutePath())
        .addString("csvFile", csvfile.getAbsolutePath()).toJobParameters();

    // when
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    await().atMost(5, SECONDS).until(() -> jobExecution.getExitStatus().getExitCode().equals("COMPLETED"));

    assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    assertThat(csvfile.exists()).isTrue();

    assertEquals(5, java.nio.file.Files.lines(csvfile.toPath())
        .count()); // hardcoded number of lines in test.parquet (it's not visible in IDEA UI, go to file and check manually)
  }
}