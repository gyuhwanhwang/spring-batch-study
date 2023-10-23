package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.batch.item.file.mapping.PassThroughLineMapper
import org.springframework.batch.item.file.transform.PassThroughLineAggregator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.WritableResource
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
class BatchConfiguration {

    @Bean
    fun job(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Job {
        return JobBuilder("job", jobRepository)
            .start(step1(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun step1(jobRepository: JobRepository, transactionManager: JdbcTransactionManager): Step {
        return StepBuilder("step1", jobRepository)
            .chunk<String, String>(10, transactionManager)
            .reader(itemReader(null))
            .writer(itemWriter(null))
            .build()
    }

    @Bean
    @StepScope
    fun itemReader(
        @Value("#{jobParameters['inputFile']}") inputFile: Resource?
    ): FlatFileItemReader<String> {

        return FlatFileItemReaderBuilder<String>()
            .name("itemReader")
            .resource(inputFile!!)
            .lineMapper(PassThroughLineMapper())
            .build()

    }

    @Bean
    @StepScope
    fun itemWriter(
        @Value("#{jobParameters['outputFile']}") outputFile: Resource?
    ): FlatFileItemWriter<String> {

        return FlatFileItemWriterBuilder<String>()
            .name("itemWriter")
            .resource(outputFile as WritableResource)
            .lineAggregator(PassThroughLineAggregator<String>())
            .build()

    }

}