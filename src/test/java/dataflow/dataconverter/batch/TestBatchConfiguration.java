package dataflow.dataconverter.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
public class TestBatchConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource ds) {
    return new JdbcTemplate(ds);
  }

}
