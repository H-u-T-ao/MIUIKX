@file:Suppress("NOTHING_TO_INLINE")

package top.sankokomi.xposed.miuix.tools

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import kotlin.random.Random

@Composable
inline fun currentContext(): Context = LocalContext.current

fun Modifier.randomBackground(): Modifier =
    this.then(
        Modifier.background(Color(Random.nextInt()))
    )

fun Modifier.onTap(onTap: (Offset) -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = onTap
            )
        }
    )

@Composable
inline fun Float.dxToDp(): Float = this / (LocalDensity.current.density + 0.5F)

@Composable
inline fun Int.dxToDp(): Float = this / (LocalDensity.current.density + 0.5F)