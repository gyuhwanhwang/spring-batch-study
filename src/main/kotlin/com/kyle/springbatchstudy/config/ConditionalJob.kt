package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager
import java.lang.RuntimeException
import java.util.Random

@Configuration
class ConditionalJob {

    @Bean
    fun passTasklet(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("First~")
            RepeatStatus.FINISHED
            throw RuntimeException("This is a failure")
        }
    }

    @Bean
    fun successTasklet(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("Success!")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun failTasklet(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("Failure")
            RepeatStatus.FINISHED
        }
    }

    @Bean
    fun job(jobRepository: JobRepository, transactionManager: JdbcTransactionManager,
            firstStep: Step, successStep: Step, failureStep: Step):Job {

        return JobBuilder("conditionalJob", jobRepository)
//            .incrementer(DailyJobTimeStamper())
            .start(firstStep)
            .on("FAILED").stopAndRestart(successStep)
            .from(firstStep).on("*").to(successStep)
            .end()
            .build()
    }

    @Bean
    fun decider(): JobExecutionDecider {
        return RandomDecider()
    }

    @Bean
    fun firstStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("firstStep", jobRepository)
            .tasklet(passTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun successStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("successStep", jobRepository)
            .tasklet(successTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun failureStep(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("failureStep", jobRepository)
            .tasklet(failTasklet(), transactionManager)
            .build()
    }
}

class RandomDecider(private val random: Random = Random()) : JobExecutionDecider {

    override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
        println("Decider Start")
        return if (random.nextBoolean()) {
            println("will COMPLETED")
            FlowExecutionStatus(FlowExecutionStatus.COMPLETED.name)
        } else {
            println("will FAILED")
            FlowExecutionStatus(FlowExecutionStatus.FAILED.name)
        }
    }

}