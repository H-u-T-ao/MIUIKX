package top.sankokomi.xposed.miuix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.tools.command
import top.sankokomi.xposed.miuix.ui.composable.MIUIKXSurface
import top.sankokomi.xposed.miuix.ui.composable.RealColumn

class MIUIKXMainUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MIUIKXSurface {
                MIUIKXMain()
            }
        }
    }
}

@Composable
fun MIUIKXMain() {
    RealColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                command("pkill -f com.android.systemui", true)
            }
        ) {
            Text(text = stringResource(id = R.string.restart_system_ui))
        }
    }
}