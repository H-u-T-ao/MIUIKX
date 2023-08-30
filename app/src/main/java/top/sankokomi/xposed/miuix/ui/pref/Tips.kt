package top.sankokomi.xposed.miuix.ui.pref

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.tools.onTap
import top.sankokomi.xposed.miuix.ui.theme.MIUIKXPurple

@Composable
fun Tips(
    tips: String
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    Box {
        Image(
            painter = painterResource(id = R.drawable.ic_help_tips),
            modifier = Modifier
                .size(20.dp)
                .onTap {
                    isMenuExpanded = true
                },
            contentDescription = null
        )
        DropdownMenu(
            expanded = isMenuExpanded,
            modifier = Modifier
                .background(MIUIKXPurple)
                .fillMaxWidth(0.4F)
                .padding(vertical = 2.dp, horizontal = 12.dp),
            onDismissRequest = {
                isMenuExpanded = false
            }
        ) {
            Text(text = tips)
        }
    }
}