package top.sankokomi.xposed.miuix.tools

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun currentContext(): Context = LocalContext.current