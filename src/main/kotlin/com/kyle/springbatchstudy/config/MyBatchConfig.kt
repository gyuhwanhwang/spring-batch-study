package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersValidator
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.CompositeJobParametersValidator
import org.springframework.batch.core.job.DefaultJobParametersValidator
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.listener.ExecutionContextPromotionListener
import org.springframework.batch.core.listener.JobListenerFactoryBean
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
    fun validator(): CompositeJobParametersValidator {
        val validator = CompositeJobParametersValidator()

        val defaultJobParametersValidator = DefaultJobParametersValidator(
            arrayOf("fileName"),
            arrayOf("name", "currentDate")
        )

        defaultJobParametersValidator.afterPropertiesSet()

        validator.setValidators(
            listOf(ParameterValidator(),
            defaultJobParametersValidator)
        )

        return validator
    }

    @Bean
    fun job(jobRepository: JobRepository, step1: Step, step2: Step): Job {
        return JobBuilder("myJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .validator(validator())
            .listener(JobListenerFactoryBean.getListener(
                JobLoggerListener()))
            .start(step1)
            .next(step2)
            .build()
    }

    @Bean
    fun step1(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("step1", jobRepository)
            .tasklet(HelloWorld(), transactionManager)
            .listener(promotionListener())
            .build()
    }

    @Bean
    fun step2(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet(GoodBye(), transactionManager)
            .build()
    }

    @Bean
    fun promotionListener(): StepExecutionListener {
        val listener = ExecutionContextPromotionListener()

        listener.setKeys(arrayOf("name"))

        return listener
    }
}