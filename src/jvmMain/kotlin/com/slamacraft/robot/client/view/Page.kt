package com.slamacraft.robot.client.view

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slamacraft.robot.client.type.PageDirection
import com.slamacraft.robot.client.type.PageList


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageSide(
    pageList: PageList,
    direction: PageDirection = PageDirection.LEFT,
    state: LazyListState = rememberLazyListState()
) {

    var curBar by remember { mutableStateOf(0) }
    var slideDown by remember { mutableStateOf(false) }
    var lastbar by remember { mutableStateOf(curBar) }
    val transition = updateTransition(curBar)

    @Composable
    fun LazyPageBar() {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(64.dp),
            color = MaterialTheme.colors.primaryVariant
        ) {
            LazyColumn(
                state = state,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(pageList) { index, item ->
                    val size = transition.animateDp {
                        if (index == curBar) {
                            48.dp
                        } else {
                            35.dp
                        }
                    }
                    val color = transition.animateColor {
                        if (index == curBar) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.primaryVariant
                        }
                    }
                    Surface(color = color.value, modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            {
                                curBar = index
                                slideDown = curBar < lastbar
                                lastbar = curBar
                            }, modifier = Modifier.padding(8.dp)
                                .size(size.value)
                        ) {
                            pageList[index].first.invoke()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PageView() {
        AnimatedContent(targetState = curBar,
            transitionSpec = {
                if (slideDown) {
                    // 从下到上滑入滑出，淡入淡出
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                } else {
                    // 从上到下滑入滑出，淡入淡出
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                }.using(
                    // 禁用裁剪、因为滑入滑出应该显示超出界限
                    SizeTransform(clip = false)
                )
            }) {
            pageList[it].second.invoke(this)
        }
    }


    MaterialTheme {
        Row {
            if (direction == PageDirection.LEFT) {
                LazyPageBar()
            }

            PageView()

            if (direction == PageDirection.RIGHT) {
                LazyPageBar()
            }
        }

    }
}