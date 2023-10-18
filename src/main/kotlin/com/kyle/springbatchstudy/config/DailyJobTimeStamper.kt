package com.kyle.springbatchstudy.config

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersIncrementer
import java.util.*

class DailyJobTimeStamper: JobParametersIncrementer {

    override fun getNext(parameters: JobParameters?): JobParameters {
        return JobParametersBuilder(parameters ?: JobParameters())
            .addDate("currentDate", Date())
            .toJobParameters()
    }
}