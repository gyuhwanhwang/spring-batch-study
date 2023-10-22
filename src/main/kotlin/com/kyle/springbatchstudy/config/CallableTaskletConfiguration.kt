package com.kyle.springbatchstudy.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager
import java.util.concurrent.Callable

//@Configuration
class CallableTaskletConfiguration {

    companion object : Log()

    @Bean
    fun callableJob(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("callableJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .start(callableStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun callableStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("callableStep", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build()
    }

    @Bean
    fun tasklet(): CallableTaskletAdapter {
        val callableTaskletAdapter = CallableTaskletAdapter()

        callableTaskletAdapter.setCallable(callableObject())

        return callableTaskletAdapter
    }

    @Bean
    fun callableObject(): Callable<RepeatStatus> {
        return Callable {
            log.info("This was executed in another thread.")
            RepeatStatus.FINISHED
        }
    }


}