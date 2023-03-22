package com.slamacraft.robot.client.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xml.sax.InputSource
import java.io.IOException
import java.net.URL

@Composable
fun AsyncUrlImg(
    url: String,
    contentDesc: String? = null,
    placeholderImg: Painter = painterResource("/head.png"),
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    AsyncImage(
        load = { loadImgBitmap(url) },
        painterFor = { BitmapPainter(it) },
        contentDesc,
        placeholderImg,
        modifier, contentScale
    )
}

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDesc: String? = null,
    placeholderImg: Painter = painterResource("/head.png"),
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val image = produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
    val painter = if (image.value == null) {
        placeholderImg
    } else {
        painterFor(image.value!!)
    }
    Image(
        painter = painter,
        contentDescription = contentDesc,
        contentScale = contentScale,
        modifier = modifier
    )
}

fun loadImgBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(url: String, density: Density): Painter =
    URL(url).openStream().buffered().use { androidx.compose.ui.res.loadSvgPainter(it, density) }

fun loadXmlImgVector(url: String, density: Density): ImageVector =
    URL(url).openStream().buffered().use { loadXmlImageVector(InputSource(it), density) }

fun loadMd5Img(md5: ByteArray):ImageBitmap = md5.inputStream().buffered().use(::loadImageBitmap)