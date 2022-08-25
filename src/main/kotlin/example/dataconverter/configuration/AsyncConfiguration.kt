package dataflow.dataconverter.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig {

    /**
     * Executor for data frame converting
     */
    @Bean(name = ["convertingExecutor"])
    fun convertingExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = CONVERSION_EXECUTOR_POOL_SIZE
            setQueueCapacity(QUEUE_CAPACITY)
            initialize()
        }
    }

    companion object {
        private const val CONVERSION_EXECUTOR_POOL_SIZE = 10
        private const val QUEUE_CAPACITY = 100
    }
}
