package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
class FlowJob {

    @Bean
    fun loadStockFile(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("The stock file has been loaded")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun loadCustomerFile(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("The customer file has been loaded")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun updateStart(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("The start has been updated")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun runBatchTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("The batch has been run")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun preProcessingFlow(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Flow {
        return FlowBuilder<Flow>("preProcessingFlow")
            .start(loadFileStep(jobRepository, transactionManager))
            .next(loadCustomerStep(jobRepository, transactionManager))
            .next(updateStartStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun preProcessingJob(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("preProcessing", jobRepository)
            .start(loadFileStep(jobRepository, transactionManager))
            .next(loadCustomerStep(jobRepository, transactionManager))
            .next(updateStartStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun conditionalStepLogicJob(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("conditionalStepLogicJob", jobRepository)
            .incrementer(DailyJobTimeStamper())
            .start(initializeBatch(jobRepository, transactionManager))
            .next(runBatch(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun initializeBatch(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("initalizeBatch", jobRepository)
            .job(preProcessingJob(jobRepository, transactionManager))
            .parametersExtractor(DefaultJobParametersExtractor())
            .build()
    }

    @Bean
    fun loadFileStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("loadFileStep", jobRepository)
            .tasklet(loadStockFile(), transactionManager)
            .build()
    }

    @Bean
    fun loadCustomerStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("loadCustomerStep", jobRepository)
            .tasklet(loadCustomerFile(), transactionManager)
            .build()
    }

    @Bean
    fun updateStartStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("updateStartStep", jobRepository)
            .tasklet(updateStart(), transactionManager)
            .build()
    }

    @Bean
    fun runBatch(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("runBatch", jobRepository)
            .tasklet(runBatchTasklet(), transactionManager)
            .build()
    }
}