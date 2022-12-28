package com.dingo


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@EnableScheduling
@SpringBootApplication
class DingDoRobot

@main def runMyProgram(args: String*): Unit = {
  // your program here
  val springApplication = new SpringApplication(classOf[DingDoRobot])
  springApplication.run(args: _*)
}