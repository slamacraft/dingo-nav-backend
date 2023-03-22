package com.slamacraft.robot.client.type

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

typealias Page = Pair<
        @Composable () -> Unit, // page icon
        @Composable AnimatedVisibilityScope.() -> Unit> // page view

typealias PageList = SnapshotStateList<Page>

enum class PageDirection {
    LEFT,
    RIGHT
}