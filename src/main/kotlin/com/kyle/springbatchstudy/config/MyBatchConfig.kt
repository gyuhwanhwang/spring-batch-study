package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersValidator
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.DefaultJobParametersValidator
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
class MyBatchConfig {

    @Bean
    fun job(jobRepository: JobRepository, step1: Step): Job {
        return JobBuilder("myJob", jobRepository)
            .start(step1)
            .build()
    }

    @Bean
    fun step(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("step1", jobRepository)
            .tasklet(helloWorldTasklet(null), transactionManager)
            .build()
    }

    @StepScope
    @Bean
    fun helloWorldTasklet(
        @Value("#{jobParameters['name']}") name: String?
    ): Tasklet {

        return Tasklet { contribution, chunkContext ->
//            val name = chunkContext.stepContext
//                .jobParameters["-name"] as String

            println("Hello, $name!")

            RepeatStatus.FINISHED
        }
    }
}