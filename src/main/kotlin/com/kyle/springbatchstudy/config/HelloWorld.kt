package com.kyle.springbatchstudy.config

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class HelloWorld: Tasklet {

    companion object {
        private const val HELLO_WORLD = "Hello, %s"
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val name = chunkContext.stepContext
            .jobParameters["name"] as String

        val jobContext = chunkContext.stepContext
            .stepExecution
            .jobExecution
            .executionContext

        jobContext.put("user.name", name)

        println(String.format(HELLO_WORLD, name))
        return RepeatStatus.FINISHED
    }
}