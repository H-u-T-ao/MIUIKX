package top.sankokomi.xposed.miuix.ui.pref

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.ui.composable.RealRow
import top.sankokomi.xposed.miuix.ui.theme.Gray
import top.sankokomi.xposed.miuix.ui.theme.MIUIKXPurple
import kotlin.reflect.KMutableProperty0

@Composable
fun MIUIKXBooleanPrefSwitch(
    property: KMutableProperty0<Boolean>,
    text: String,
    modifier: Modifier = Modifier,
    tips: String? = null,
    enabled: Boolean = true
) {
    var cacheValue by remember { mutableStateOf(property.get()) }
    RealRow(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                text = "${stringResource(id = R.string.current_value)} $cacheValue",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                color = Gray
            )
        }
        Switch(
            checked = cacheValue,
            onCheckedChange = {
                property.set(it)
                cacheValue = it
            },
            enabled = enabled,
            modifier = Modifier.align(Alignment.CenterVertically),
            colors = SwitchDefaults.colors(
                checkedTrackColor = MIUIKXPurple,
                checkedBorderColor = MIUIKXPurple
            )
        )
    }
}