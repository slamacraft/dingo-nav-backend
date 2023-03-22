package com.slamacraft.robot.client.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.slamacraft.robot.client.component.AsyncUrlImg
import com.slamacraft.robot.client.store.BotsStore
import com.slamacraft.robot.client.store.LocalStore
import com.slamacraft.robot.client.type.Page


@Preview
@Composable
fun AppPreview() {
    PageSide(LocalStore.pageList)
}

