package com.dingo

import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssTable
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.jetbrains.exposed.sql.SchemaUtils

@SpringBootApplication
@EnableConfigurationProperties
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
open class RobotApplication

fun main(args: Array<String>) {
    val springApplication = SpringApplication(RobotApplication::class.java)
    springApplication.run(*args)
}
