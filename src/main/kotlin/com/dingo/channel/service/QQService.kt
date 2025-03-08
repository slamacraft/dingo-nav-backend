package com.dingo.channel.service

import cn.hutool.core.util.HexUtil
import com.dingo.channel.model.ChannelDto
import com.dingo.channel.model.VerifyDto
import com.dingo.channel.model.VerifyVo
import com.dingo.config.properties.QQProperty
import com.dingo.core.dify.DifyMsgSender
import com.dingo.core.qq.QQMsgSender
import com.dingo.module.entity.conversation.ConversationEntity
import com.dingo.module.entity.conversation.ConversationTable
import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class QQService {
    @Autowired
    lateinit var qqProperty: QQProperty

    @Autowired
    lateinit var difyMsgSender: DifyMsgSender

    @Autowired
    lateinit var qqMsgSender: QQMsgSender

    /**
     * 校验qq报文
     */
    fun verify(dto: VerifyDto): VerifyVo {
        var seed = qqProperty.secret

        while (seed.length < 32) { // Ed25519 私钥长度为 32 字节
            seed += seed
        }
        seed = seed.take(32)

        // 生成秘钥对
        // 3. 生成密钥对
        val spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519)
        val privateKeySpec = EdDSAPrivateKeySpec(seed.toByteArray(), spec)
        val privateKey = EdDSAPrivateKey(privateKeySpec)

        val msg = dto.event_ts + dto.plain_token
        // 使用ed25519签名
        val edDSAEngine = EdDSAEngine();
        edDSAEngine.initSign(privateKey)
        edDSAEngine.update(msg.encodeToByteArray())
        val signature: ByteArray = edDSAEngine.sign()

        // 5. 构造响应
        return VerifyVo(
            plain_token = dto.plain_token,
            signature = HexUtil.encodeHexStr(signature)
        )
    }

    /**
     * 回复频道at消息
     */
    fun recallChannelAtMsg(dto: ChannelDto) {
        val content = "@${dto.author.username} 对你说：${dto.content}"
        val answer = sendChannelMsg(dto, content) {
            this["is_group_chat"] = "true"
        }
        qqMsgSender.recallChannelAtMsg(
            dto.channel_id, dto.id, answer
        )
    }

    /**
     * 回复频道私聊消息
     */
    fun recallChannelPrivateMsg(dto: ChannelDto) {
        val answer = sendChannelMsg(dto)
        qqMsgSender.recallChannelPrivateMsg(
            dto.guild_id, dto.id, answer
        )
    }

    /**
     * 回复频道里的消息
     */
    @Transactional
    open fun sendChannelMsg(
        dto: ChannelDto, content: String = dto.content,
        inputs: MutableMap<String, Any>.() -> Unit = {}
    ): String {
        // 通过channelId和guideId查询会话，如果存在则使用会话id发送消息
        val conversation = ConversationTable.selectAll()
            .where {
                (ConversationTable.guideId eq dto.guild_id) and
                        (ConversationTable.channelId eq dto.channel_id)
            }.firstOrNull()?.let {
                ConversationTable.buildEntity(it)
            }

        val userName = dto.author.username
        if (conversation != null) {
            // 如果有存在的会话，则直接使用会话id发送消息
            val (answer, _) = difyMsgSender.sendMsg(content, userName, conversation.conversationId, inputs)
            return answer
        }

        // 否则创建一个会话，并保存新的会话id
        val (answer, conversationId) = difyMsgSender.sendMsg(content, userName, "", inputs)
        ConversationTable.insert(ConversationEntity {
            this.channelId = channelId
            this.guideId = guideId
            this.conversationId = conversationId
        })
        return answer
    }

}