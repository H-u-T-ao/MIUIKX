package top.sankokomi.xposed.miuix.ui.pref

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.ui.composable.RealColumn
import top.sankokomi.xposed.miuix.ui.theme.Gray
import top.sankokomi.xposed.miuix.ui.theme.MIUIKXPurple
import kotlin.reflect.KMutableProperty0

@Composable
fun MIUIKXIntPrefSlideBar(
    property: KMutableProperty0<Int>,
    text: String,
    modifier: Modifier = Modifier,
    tips: String? = null,
    enabled: Boolean = true,
    valueRange: ClosedRange<Int> = 0..10,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var lazyValue by remember { mutableIntStateOf(property.get()) }
    RealColumn(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Medium
            )
            if (tips != null) {
                Tips(tips = tips)
            }
        }
        Text(
            text = "${stringResource(id = R.string.current_value)} $lazyValue",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 12.sp,
            color = Gray
        )
        Slider(
            value = lazyValue.toFloat(),
            onValueChange = { lazyValue = it.toInt() },
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
            steps = valueRange.endInclusive - valueRange.start - 1,
            onValueChangeFinished = { if (enabled) property.set(lazyValue) },
            colors = SliderDefaults.colors(
                thumbColor = MIUIKXPurple,
                activeTrackColor = MIUIKXPurple
            ),
            interactionSource = interactionSource
        )
    }
}