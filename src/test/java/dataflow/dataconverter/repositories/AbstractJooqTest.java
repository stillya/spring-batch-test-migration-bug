package dataflow.dataconverter.repositories;

import org.junit.ClassRule;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles(AbstractJooqTest.PROFILE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractJooqTest {

  public static final String PROFILE = "jooq";

  @ClassRule
  public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12")
      .withDatabaseName("df_data_converter")
      .withUsername("postgres")
      .withPassword("postgres");

  static {
    container.start();
  }

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.datasource.username", container::getUsername);
  }
}
