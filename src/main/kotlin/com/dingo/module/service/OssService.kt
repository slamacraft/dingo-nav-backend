package com.dingo.module.service

import com.dingo.module.entity.oss.OssEntity
import com.dingo.module.entity.oss.OssRefTable
import com.dingo.module.entity.oss.OssTable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Component
open class OssService {

    open fun get(id: Long): OssEntity = OssTable.getById(id) ?: throw RuntimeException("oss不存在")

    @Transactional
    open fun upload(file: MultipartFile, bucketName: String): OssEntity {

        OssRefTable.leftJoin(OssTable)

        return OssTable.insert(OssEntity {
            name = file.originalFilename
            this.bucketName = bucketName
            size = file.size
        })
    }

}