package com.kyle.springbatchstudy.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

//@Configuration
////@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
//class BatchConfig {
//
//    @Bean
//    fun dataSource(): DataSource {
//        return EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
//            .generateUniqueName(true)
//            .build()
//    }
//
//    @Bean
//    fun transactionManager(dataSource: DataSource): JdbcTransactionManager {
//        return JdbcTransactionManager(dataSource)
//    }
//
//}