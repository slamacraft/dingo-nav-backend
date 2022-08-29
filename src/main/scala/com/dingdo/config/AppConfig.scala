package com.dingdo.config

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean

@Configurable
class AppConfig {
  @Bean def applicationStartListener = new AppRunListener
}
