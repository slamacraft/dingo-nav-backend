package com.slamacraft.robot.client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.slamacraft.robot.client.mirai.FixProtocolVersion
import com.slamacraft.robot.client.view.AppPreview


fun main() = application {
    FixProtocolVersion.update()
    Window(
        title = "学习Compose",
        onCloseRequest = ::exitApplication
    ) {
        AppPreview()
    }
}




