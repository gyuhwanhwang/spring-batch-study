package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter
import org.springframework.beans.factory.annotation.Value
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
            .tasklet(methodInvokingTasklet(null), transactionManager)
            .build()
    }

    @StepScope
    @Bean
    fun methodInvokingTasklet(
        @Value("#{jobParameters['message']}") message: String?
    ): MethodInvokingTaskletAdapter {
        val methodInvokingTaskletAdapter = MethodInvokingTaskletAdapter()

        methodInvokingTaskletAdapter.setTargetObject(service())
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod")
        methodInvokingTaskletAdapter.setArguments(arrayOf(message))

        return methodInvokingTaskletAdapter
    }

    @Bean
    fun service(): CustomService = CustomService()

}

class CustomService {

    companion object : Log()

    fun serviceMethod(message: String) {
        log.info("{}", message)
    }
}
