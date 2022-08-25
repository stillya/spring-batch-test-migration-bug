package dataflow.dataconverter.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app-config")
data class AppConfigProperties(
    val dbAccessorProperties: AppConfigPropertiesDbAccessorProperties,
) {
    data class AppConfigPropertiesDbAccessorProperties(
        val specialSchema: String, // sequences
        val tempObjectIdSequence: String // name inside special schema
    )
}
