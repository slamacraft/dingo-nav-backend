package com.slamacraft.robot.client.view

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slamacraft.robot.client.component.AsyncUrlImg
import com.slamacraft.robot.client.component.chatScrolling
import com.slamacraft.robot.client.mirai.SimpleMsgSender
import com.slamacraft.robot.client.store.BotsStore
import com.slamacraft.robot.client.store.ChatInfo
import com.slamacraft.robot.client.store.TextMsgData
import com.slamacraft.robot.client.type.Page
import com.slamacraft.robot.client.type.PageDirection
import com.slamacraft.robot.client.type.PageList
import kotlinx.coroutines.launch

@Composable
fun DesktopChatView() {
    val chatPageList = BotsStore.curChatList()?.chatList?.map {
        Page({
            ChatIcon(it)
        }) {
            ChatArea(it)
        }
    }?.toMutableStateList()

    PageSide(
        pageList = chatPageList ?: PageList(),
        direction = PageDirection.RIGHT
    )

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatIcon(chatInfo: ChatInfo) {

    BoxWithConstraints(modifier = Modifier.size(40.dp), contentAlignment = Alignment.BottomEnd) {
        val width = this.maxWidth
        // Calculate the size and position of the text
        val textSize = width / 3

        AsyncUrlImg(
            url = chatInfo.headImg,
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )

        AnimatedVisibility(
            visible = chatInfo.unreadMsg.value > 0,
            enter = scaleIn(),
            exit = scaleOut()
        ){
            Surface(
                color = Color.Red,
                modifier = Modifier.size(15.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
            ) {
                Text(
                    text = chatInfo.unreadMsg.value.toString(),
                    color = Color.White,
                    fontSize = textSize.value.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ChatArea(chatInfo: ChatInfo) {
    chatInfo.unreadMsg.value = 0
    Row {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth(0.93f)) {
            val boxWidth = maxWidth
            val width = boxWidth - 64.dp - 16.dp;
            Column {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = chatInfo.chatName,
                        color = MaterialTheme.colors.secondaryVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                chatInfo.coroutineScope = rememberCoroutineScope()
                chatInfo.listState = rememberLazyListState()

                chatScrolling(
                    chatInfo.msgList,
                    state = chatInfo.listState,
                    modifier = Modifier.padding(8.dp)
                        .weight(0.7f)
                        .fillMaxWidth()
                )

                InputArea(
                    chatInfo = chatInfo,
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth()
                        .weight(0.3f)
                ) {
                    if(it.isNotBlank()){
                        chatInfo.coroutineScope.launch {
                            chatInfo.msgList.add(
                                TextMsgData(author = "ME", content = it, isSelf = true)
                            )
                            chatInfo.listState.animateScrollToItem(chatInfo.msgList.size - 1)
                            SimpleMsgSender.sendMsg(it, chatInfo.chatId, chatInfo.chatType)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputArea(
    chatInfo: ChatInfo, modifier: Modifier = Modifier,
    sendKey: Key = Key.Enter,
    onSend: (String) -> Unit
) {
    TextField(
        value = chatInfo.inputArea.value,
        modifier = modifier.onKeyEvent {
            if (it.isCtrlPressed && it.key == sendKey) {
                onSend(chatInfo.inputArea.value.trim())
                chatInfo.inputArea.value = ""
            }
            true
        },
        placeholder = { Text("请输入") },
        onValueChange = { chatInfo.inputArea.value = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
    )
}