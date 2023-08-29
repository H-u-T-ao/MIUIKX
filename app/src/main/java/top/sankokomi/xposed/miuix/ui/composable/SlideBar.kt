package top.sankokomi.xposed.miuix.ui.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.reflect.KMutableProperty0

@Composable
fun MIUIKXIntPrefSlideBar(
    property: KMutableProperty0<Int>,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedRange<Int> = 0..10,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var lazyValue by remember { mutableIntStateOf(property.get()) }
    RealColumn(
        modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "$text $lazyValue")
        Slider(
            lazyValue.toFloat(),
            { lazyValue = it.toInt() },
            modifier,
            enabled,
            valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
            valueRange.endInclusive - valueRange.start - 1,
            { if (enabled) property.set(lazyValue) },
            colors,
            interactionSource
        )
    }
}