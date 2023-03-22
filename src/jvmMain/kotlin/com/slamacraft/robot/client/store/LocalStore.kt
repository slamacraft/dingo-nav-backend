package com.slamacraft.robot.client.store

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.slamacraft.robot.client.component.AsyncUrlImg
import com.slamacraft.robot.client.component.LoginRequired
import com.slamacraft.robot.client.type.Page
import com.slamacraft.robot.client.view.BotInfoView
import com.slamacraft.robot.client.view.DesktopChatView

object LocalStore {

    @OptIn(ExperimentalComposeUiApi::class)
    val pageList = mutableStateListOf(
        Page({
            AsyncUrlImg(
                url = BotsStore.curBotHeadImg.value,
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
        }) {
            BotInfoView()
        },
        Page({
            Icon(
                imageVector = Icons.Outlined.Chat, contentDescription = "聊天",
                modifier = Modifier.fillMaxSize()
            )
        }) {
            LoginRequired {
                DesktopChatView()
            }
        },
        Page({
            Icon(
                imageVector = Icons.Outlined.Settings, contentDescription = "测试",
                modifier = Modifier.fillMaxSize()
            )
        }) { Text("nihao") },
        Page({
            Icon(
                imageVector = Icons.Outlined.List, contentDescription = "第三页",
                modifier = Modifier.fillMaxSize()
                    .onPointerEvent(PointerEventType.Enter){

                    }
            )
        }) { Text("这是第三页") }
    )

}