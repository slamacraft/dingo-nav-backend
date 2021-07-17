package com.dingdo

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@MapperScan(basePackages = ["com.dingdo.**.mapper"])
@SpringBootApplication
@EnableConfigurationProperties
open class RobotApplication {
}

fun main(args: Array<String>) {
    val springApplication = SpringApplication(RobotApplication::class.java)
    springApplication.run(*args)
}
