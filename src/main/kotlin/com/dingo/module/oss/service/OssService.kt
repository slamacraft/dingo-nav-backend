package com.dingo.module.oss.service

import com.dingo.channel.model.OssAddReq
import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssTable
import com.dingo.module.oss.entity.OssTable.getByIdOrNull
import com.dingo.module.oss.entity.OssTable.name
import com.dingo.module.oss.entity.OssTable.size
import com.dingo.module.oss.entity.OssTable.url
import com.dingo.util.MinioUtil
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Component
open class OssService {

    @Transactional
    open fun get(id: Long): OssEntity = OssTable.getByIdOrNull(id) ?: throw RuntimeException("oss不存在")

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
            url = fileUrl
        }
    }

    @Transactional
    open fun add(req: OssAddReq): OssEntity {
        val entity = OssTable.insert(OssEntity {
            name = req.name
            size = req.size
            url = req.url
        })
        return entity
    }

}