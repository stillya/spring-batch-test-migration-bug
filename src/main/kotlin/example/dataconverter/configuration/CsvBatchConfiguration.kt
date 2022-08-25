package dataflow.dataconverter.configuration

import org.apache.commons.io.FileUtils
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.FieldSet
import org.springframework.batch.item.file.transform.LineAggregator
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import java.io.File
import java.nio.charset.StandardCharsets

@Configuration
class CsvBatchConfiguration(val stepBuilderFactory: StepBuilderFactory) {

    //
    // Transformers
    //

    @Bean
    @JobScope
    fun csvItemReader(
        @Value("#{jobParameters['csvFile']}") srcCsvFile: String,
        @Value("#{jobParameters['columnsNames']}") columnsNames: String,
        @Value("#{jobParameters['csvDelimiter']}") csvDelimiter: String,
    ): FlatFileItemReader<FieldSet>? {
        val tokenizer = DelimitedLineTokenizer()
        tokenizer.setDelimiter(csvDelimiter)
        tokenizer.setNames(*columnsNames.split(",").toTypedArray())
        val lineMapper = DefaultLineMapper<FieldSet>()
        lineMapper.setLineTokenizer(tokenizer)
        lineMapper.setFieldSetMapper(IdentityFieldSetMapper())
        val reader = FlatFileItemReader<FieldSet>()
        reader.setResource(FileSystemResource(srcCsvFile))
        reader.setLineMapper(lineMapper)
        reader.setLinesToSkip(1) // header skip
        return reader
    }

    @Bean
    @JobScope
    fun csvItemWriter(
        @Value("#{jobParameters['csvFile']}") dstCsvFile: String,
        @Value("#{jobParameters['csvDelimiter']}") csvDelimiter: String
    ): FlatFileItemWriter<FieldSet> {
        val writer = FlatFileItemWriter<FieldSet>()
        writer.setResource(FileSystemResource(dstCsvFile))
        writer.setLineAggregator(FieldsetLineAggregator(csvDelimiter))
        writer.setAppendAllowed(true)
        return writer
    }

    //
    // Steps
    //

    @Bean
    @JobScope
    protected fun createCsvFileStep(
        @Value("#{jobParameters['csvFile']}") dstCsvFile: String,
        @Value("#{jobParameters['columnsNames']}") columnsNames: String,
        @Value("#{jobParameters['csvDelimiter']}") csvDelimiter: String,
    ): Step? {
        return stepBuilderFactory.get("createCsvFileStep")
            .tasklet(
                CreateCsvFileTasklet(
                    csvDelimiter,
                    dstCsvFile,
                    columnsNames
                )
            )
            .build()
    }

    //
    // Tasklets
    //

    class CreateCsvFileTasklet(
        private val csvDelimiter: String,
        private val dstFileName: String,
        private val columnsNames: String
    ) :
        Tasklet {
        override fun execute(
            contribution: StepContribution,
            chunkContext: ChunkContext
        ): RepeatStatus {
            val file = File(dstFileName)
            val data: String = columnsNames.split(",")
                .joinToString(separator = csvDelimiter) + System.lineSeparator()
            FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8)
            return RepeatStatus.FINISHED
        }
    }

    //
    // Field set line aggregator
    //

    class FieldsetLineAggregator(private val csvDelimiter: String) :
        LineAggregator<FieldSet> {
        override fun aggregate(item: FieldSet): String {
            val values = item.values
            return values.joinToString(separator = csvDelimiter)
        }
    }

    //
    // Filed set mappers
    //

    class IdentityFieldSetMapper : FieldSetMapper<FieldSet> {
        override fun mapFieldSet(fieldSet: FieldSet): FieldSet {
            return fieldSet
        }
    }

}