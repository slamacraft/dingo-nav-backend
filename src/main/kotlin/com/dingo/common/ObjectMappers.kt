package com.dingo.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object ObjectMappers {
    val jsonMapper = ObjectMapper()

    init {
        //        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}