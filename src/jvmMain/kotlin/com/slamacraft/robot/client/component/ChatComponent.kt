package com.slamacraft.robot.client.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.slamacraft.robot.client.store.ImgMd5MsgData
import com.slamacraft.robot.client.store.ImgUrlMsgData
import com.slamacraft.robot.client.store.MsgData
import com.slamacraft.robot.client.store.TextMsgData


@Composable
fun chatScrolling(
    msgList: MutableList<MsgData>,
    state: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier.padding(8.dp)
        .fillMaxWidth()
        .fillMaxHeight()
) {

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colors.background,
        elevation = 3.dp,
        modifier = modifier
    ) {
        LazyColumn(state = state) {
            items(msgList) {
                MessageCard(it)
            }
        }
    }
}

@Composable
fun MessageCard(msg: MsgData) {
    if (msg.isSelf) {
        MyMsg(msg)
    } else {
        TheirMsg(msg)
    }
}

@Composable
fun TheirMsg(msg: MsgData) {
    Row(
        modifier = Modifier.padding(all = 8.dp).fillMaxWidth()
    ) {


        AsyncImage(
            load = { loadImgBitmap(msg.headImg) },
            painterFor = { BitmapPainter(it) },
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.author,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            MsgSurface(msg)
        }
    }
}

@Composable
fun MsgSurface(msg: MsgData, color: Color = MaterialTheme.colors.onBackground) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        elevation = 1.dp,
        color = color
    ) {
        when (msg) {
            is TextMsgData -> Text(
                text = msg.content,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2
            )

            is ImgMd5MsgData -> AsyncImage(
                load = { loadMd5Img(msg.md5) },
                painterFor = { BitmapPainter(it) },
            )

            is ImgUrlMsgData -> AsyncUrlImg(url = msg.url)
        }

    }
}

@Composable
fun MyMsg(msg: MsgData) {
    Row(
        modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.author,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            MsgSurface(msg, color = MaterialTheme.colors.secondary)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource("/head.png"),
            contentDescription = "图片",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )
    }
}
