package com.dingo.channel.controller

import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.service.OssService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/oss")
open class OssController {
    @Autowired
    lateinit var ossService: OssService

    @GetMapping("/{id}")
    open fun get(@PathVariable id: Long): OssEntity = ossService.get(id)

    @PutMapping
    open fun upload(
        @RequestPart @RequestParam("file") file: MultipartFile,
        @RequestParam("bucketName") bucketName: String
    ): OssEntity = ossService.upload(file, bucketName)
}