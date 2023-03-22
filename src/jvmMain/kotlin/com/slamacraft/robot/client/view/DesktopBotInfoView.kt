package com.slamacraft.robot.client.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Password
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slamacraft.robot.client.mirai.MiraiRobotInitializer
import com.slamacraft.robot.client.store.BotsStore
import com.slamacraft.robot.client.component.BotItem
import com.slamacraft.robot.client.component.AsyncUrlImg
import com.slamacraft.robot.client.store.LocalStore
import com.slamacraft.robot.client.type.Page
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun BotInfoView() {

    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        BotList(color = MaterialTheme.colors.surface,
            modifier = Modifier.fillMaxWidth(0.3f))
        LoginView(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun BotList(
    color:Color = MaterialTheme.colors.onBackground,
    modifier: Modifier){

    Surface(
        color = color,
        modifier = modifier
    ) {
        LazyColumn {
            itemsIndexed(items = MiraiRobotInitializer.bots) { index, it->
                BotItem(it.first, it.second.nick, it.second.avatarUrl)
            }
        }
    }
}


/**
 * 登录页面
 */
@Composable
fun LoginView(modifier: Modifier) {
    var id = remember { mutableStateOf("") }
    var pw = remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxWidth()
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "登录QQ",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            )
            TextField(
                value = id.value,
                singleLine = true,
                label = { Text("QQ号") },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                trailingIcon = { Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "账号") },
                onValueChange = { id.value = it },
            )
            TextField(
                value = pw.value,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation('*'),
                label = { Text("密码") },
                trailingIcon = { Icon(imageVector = Icons.Outlined.Password, contentDescription = "密码") },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                onValueChange = { pw.value = it },
            )
            Button(
                onClick = {
                    GlobalScope.launch {
                        BotsStore.curBot.value = MiraiRobotInitializer.login(id.value.toLong(), pw.value)
                        BotsStore.curBotHeadImg.value = BotsStore.curBot.value!!.avatarUrl
                        LocalStore.pageList[0] = Page({
                            AsyncUrlImg(
                                url = BotsStore.curBotHeadImg.value,
                                modifier = Modifier.size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
                            )
                        }) {
                            BotInfoView()
                        }
                    }
                }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 20.dp)
                    .height(55.dp)
            ) {
                Text("登录")
            }
        }

    }
}
