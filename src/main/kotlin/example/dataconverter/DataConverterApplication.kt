package dataflow.dataconverter

import dataflow.dataconverter.configuration.properties.AppConfigProperties
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["example"])
@EnableConfigurationProperties(AppConfigProperties::class)
@EnableBatchProcessing
class DataConverterApplication

fun main(args: Array<String>) {
    runApplication<DataConverterApplication>(*args)
}

