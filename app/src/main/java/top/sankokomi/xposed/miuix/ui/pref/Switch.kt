package top.sankokomi.xposed.miuix.ui.pref

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.sankokomi.xposed.miuix.ui.composable.RealColumn
import kotlin.reflect.KMutableProperty0

@Composable
fun MIUIKXBooleanPrefSwitch(
    property: KMutableProperty0<Boolean>,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var cacheValue by remember { mutableStateOf(property.get()) }
    RealColumn(
        modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "$text ${property.get()}")
        Switch(
            checked = cacheValue,
            onCheckedChange = {
                property.set(it)
                cacheValue = it
            },
            enabled = enabled
        )
    }
}