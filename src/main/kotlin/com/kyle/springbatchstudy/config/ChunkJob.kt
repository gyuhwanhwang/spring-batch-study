package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.CompletionPolicy
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager
import java.util.*

@Configuration
class ChunkJob {

    @Bean
    fun chunkBasedJob(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("chunkBasedJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .start(chunkStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun chunkStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("chunkStep", jobRepository)
            .chunk<String, String>(completionPolicy(), transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .build()
    }

    @Bean
    fun itemReader(): ListItemReader<String> {
//        val items = ArrayList<String>(100_000)
//
//        for (i in 0 until 100_000) {
//            items.add(UUID.randomUUID().toString())
//        }
//
//        return ListItemReader(items)
        val items = List(100_000) { UUID.randomUUID().toString() }
        return ListItemReader(items)
    }

    @Bean
    fun itemWriter(): ItemWriter<String> {
        return ItemWriter { println(">> current item = $it") }
    }

    @Bean
    fun completionPolicy(): CompletionPolicy {
        val policy = CompositeCompletionPolicy()

        policy.setPolicies(arrayOf(
            TimeoutTerminationPolicy(3),
            SimpleCompletionPolicy(1000)
        ))

        return policy
    }

}