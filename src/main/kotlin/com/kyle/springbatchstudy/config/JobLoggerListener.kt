package com.kyle.springbatchstudy.config

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener

class JobLoggerListener: JobExecutionListener {

    companion object {
        private const val START_MESSAGE = "%s is beginning execution"
        private const val END_MESSAGE = "%s has completed with the status %s"
    }

    override fun beforeJob(jobExecution: JobExecution) {
        println(String.format(START_MESSAGE, jobExecution.jobInstance.jobName))
    }

    override fun afterJob(jobExecution: JobExecution) {
        println(String.format(END_MESSAGE, jobExecution.jobInstance.jobName, jobExecution.status))
    }
}