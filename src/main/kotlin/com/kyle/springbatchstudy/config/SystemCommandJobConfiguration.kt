package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
class SystemCommandJobConfiguration {

    @Bean
    fun job(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("systemCommandJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .start(systemCommandStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun systemCommandStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("systemCommandStep", jobRepository)
            .tasklet(systemCommandTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun systemCommandTasklet(): SystemCommandTasklet {
        val systemCommandTasklet = SystemCommandTasklet()

//        systemCommandTasklet.setCommand("rm -rf /tmp.txt")
        systemCommandTasklet.setCommand("rm", "-rf", "/Users/gyuhwan/tmp.txt")

        systemCommandTasklet.setTimeout(5000) // 비동기로 실행되기 때문에
        systemCommandTasklet.setInterruptOnCancel(true)

        return systemCommandTasklet
    }
}