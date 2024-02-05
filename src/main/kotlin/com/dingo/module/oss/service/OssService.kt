package com.dingo.module.oss.service

import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssTable
import com.dingo.module.oss.entity.OssTable.getByIdOrNull
import com.dingo.util.MinioUtil
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class OssService {

    fun get(id: Long) = OssTable.getByIdOrNull(id) ?: throw RuntimeException("oss不存在")

    fun upload(file: MultipartFile, bucketName: String): OssEntity {
        val fileUrl = MinioUtil.upload(file, bucketName)
        val entityID = OssTable.insertAndGetId {
            it[name] = file.originalFilename
            it[size] = file.size
            it[url] = fileUrl
        }
        return OssEntity {
            id = entityID.value
            name = file.originalFilename
            size = file.size
            url = url
        }
    }

}