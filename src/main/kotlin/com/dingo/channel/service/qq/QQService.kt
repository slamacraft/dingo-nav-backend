package com.dingo.channel.service.qq

import cn.hutool.core.util.HexUtil
import com.dingo.channel.model.ChannelDto
import com.dingo.channel.model.VerifyDto
import com.dingo.channel.model.VerifyVo
import com.dingo.channel.service.common.InstructionsService
import com.dingo.config.properties.QQProperty
import com.dingo.core.qq.QQMsgSender
import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class QQService {
    @Autowired
    lateinit var qqProperty: QQProperty

    @Autowired
    lateinit var qqDifyService: QQDifyService

    @Autowired
    lateinit var qqMsgSender: QQMsgSender

    @Autowired
    lateinit var instructionsService: InstructionsService
    private val atMsgPattern = Regex("<@!?(\\d+)>\\s")

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
        dto.content = dto.content.replace(atMsgPattern, "")
        val answer = if (dto.content.startsWith("/")) {
            instructionsService.recallChannelAtMsg(dto)
        } else {
            qqDifyService.recallChannelAtMsg(dto)
        }
        qqMsgSender.recallChannelAtMsg(
            dto.channel_id, dto.id, answer
        )
    }

    /**
     * 回复频道私聊消息
     */
    fun recallChannelPrivateMsg(dto: ChannelDto) {
        val answer = if (dto.content.startsWith("/")) {
            instructionsService.recallChannelPrivateMsg(dto)
        } else {
            qqDifyService.recallChannelPrivateMsg(dto)
        }
        qqMsgSender.recallChannelAtMsg(
            dto.channel_id, dto.id, answer
        )
    }
}