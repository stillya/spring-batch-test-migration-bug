package dataflow.dataconverter.tasklets

import dataflow.dataconverter.utils.convertParquetToCSV
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component
import java.io.File

@Component
class ParquetToCsvTasklet : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val parquetFile = File(chunkContext.stepContext.jobParameters["parquetFile"] as String)
        val csvFile = File(chunkContext.stepContext.jobParameters["csvFile"] as String)

        convertParquetToCSV(parquetFile, csvFile)

        return RepeatStatus.FINISHED
    }
}