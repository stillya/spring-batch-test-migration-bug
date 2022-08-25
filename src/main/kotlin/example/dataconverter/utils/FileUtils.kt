package dataflow.dataconverter.utils

import org.apache.hadoop.conf.Configuration
import org.apache.parquet.example.data.Group
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.ParquetReader
import org.apache.parquet.hadoop.example.GroupReadSupport
import org.apache.parquet.hadoop.metadata.ParquetMetadata
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.schema.MessageType
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import org.apache.hadoop.fs.Path as HadoopPath

const val CSV_DELIMITER = ","

fun convertParquetToCSV(parquetFile: File, csvOutputFile: File) {
    val parquetFilePath = HadoopPath(parquetFile.toURI())
    val configuration = Configuration(true)
    val readSupport = GroupReadSupport()
    val readFooter: ParquetMetadata =
        ParquetFileReader.open(HadoopInputFile.fromPath(parquetFilePath, configuration)).footer
    val schema: MessageType = readFooter.fileMetaData.schema
    val writer = BufferedWriter(FileWriter(csvOutputFile))
    val reader: ParquetReader<Group> = ParquetReader.builder(readSupport, parquetFilePath).build()
    writer.use { w ->
        var g: Group?
        w.write(schema.fields.joinToString(CSV_DELIMITER) { it.name } + "\n")
        while (reader.read().also { g = it } != null) {
            for (j in 0 until schema.fieldCount) {
                if (j > 0) {
                    w.write(CSV_DELIMITER)
                }
                val valueToString = g!!.getValueToString(j, 0)
                w.write(valueToString)
            }
            w.write("\n")
        }
        reader.close()
    }
}