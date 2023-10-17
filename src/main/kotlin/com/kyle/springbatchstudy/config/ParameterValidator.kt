package com.kyle.springbatchstudy.config

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.JobParametersValidator
import org.springframework.util.StringUtils

class ParameterValidator : JobParametersValidator {
    @Throws(JobParametersInvalidException::class)

    override fun validate(parameters: JobParameters?) {
        val fileName = parameters!!.getString("fileName")

        if (!StringUtils.hasText(fileName)) {
            throw JobParametersInvalidException("fileName parameter is missing")
        }
        else if (!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
            throw JobParametersInvalidException("fileName parameter does not use csv file extension")
        }
    }
}