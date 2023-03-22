package com.slamacraft.robot.client.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slamacraft.robot.client.store.BotsStore

@Composable
fun LoginRequired(content: @Composable () -> Unit) {
    if (BotsStore.curBot.value == null) {
        BoxWithConstraints {
            Text(
                text = "请先登录",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
                    .padding(0.dp, maxHeight / 2 - 10.dp)
            )
        }
    } else {
        content()
    }
}