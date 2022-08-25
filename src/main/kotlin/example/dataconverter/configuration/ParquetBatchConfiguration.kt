package dataflow.dataconverter.configuration

import dataflow.dataconverter.tasklets.ParquetToCsvTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class ParquetToCsvJobConfiguration(
    val steps: StepBuilderFactory,
    val parquetToCsvTasklet: ParquetToCsvTasklet,
    val jobs: JobBuilderFactory
) {
    @Bean(name = ["jobCopyParquet2Csv"])
    fun copyParquetToCsv(): Job = jobs.get("jobCopyParquet2Csv")
        .start(
            steps.get("step")
                .tasklet(parquetToCsvTasklet)
                .build()
        )
        .build()
}