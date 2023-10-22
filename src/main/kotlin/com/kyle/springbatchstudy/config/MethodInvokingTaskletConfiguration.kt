package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
class MethodInvokingTaskletConfiguration {

    companion object : Log()

    @Bean
    fun methodInvokingJob(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("methodInvokingJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .start(methodInvokingStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun methodInvokingStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("methodInvokingStep", jobRepository)
            .tasklet(methodInvokingTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun methodInvokingTasklet(): MethodInvokingTaskletAdapter {
        val methodInvokingTaskletAdapter = MethodInvokingTaskletAdapter()

        methodInvokingTaskletAdapter.setTargetObject(service())
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod")

        return methodInvokingTaskletAdapter
    }

    @Bean
    fun service(): CustomService = CustomService()

}

class CustomService {

    companion object : Log()

    fun serviceMethod() {
        log.info("service method was called")
    }
}
