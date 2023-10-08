package top.sankokomi.xposed.miuix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.pref.preference
import top.sankokomi.xposed.miuix.tools.command
import top.sankokomi.xposed.miuix.ui.pref.MIUIKXIntPrefSlideBar
import top.sankokomi.xposed.miuix.ui.composable.MIUIKXSurface
import top.sankokomi.xposed.miuix.ui.composable.RealColumn
import top.sankokomi.xposed.miuix.ui.pref.MIUIKXBooleanPrefSwitch

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
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            ),
    ) {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                command("pkill -f com.android.systemui", true)
            }
        ) {
            Text(text = stringResource(id = R.string.restart_system_ui))
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                command("pkill -f com.miui.home", true)
            }
        ) {
            Text(text = stringResource(id = R.string.restart_miui_home))
        }
        MIUIKXIntPrefSlideBar(
            property = preference::qsPagerVerticalRow,
            text = stringResource(id = R.string.qs_pager_vertical_row_count),
            tips = stringResource(id = R.string.qs_pager_row_count_tips),
            valueRange = 3..8
        )
        MIUIKXIntPrefSlideBar(
            property = preference::qsPagerVerticalColumn,
            text = stringResource(id = R.string.qs_pager_vertical_column_count),
            tips = stringResource(id = R.string.qs_pager_column_count_tips),
            enabled = false,
            valueRange = 1..6
        )
        MIUIKXIntPrefSlideBar(
            property = preference::qsPagerOrientationRow,
            text = stringResource(id = R.string.qs_pager_orientation_row_count),
            tips = stringResource(id = R.string.qs_pager_row_count_tips),
            valueRange = 3..8
        )
        MIUIKXIntPrefSlideBar(
            property = preference::qsPagerOrientationColumn,
            text = stringResource(id = R.string.qs_pager_orientation_column_count),
            tips = stringResource(id = R.string.qs_pager_column_count_tips),
            enabled = false,
            valueRange = 1..6
        )
        MIUIKXBooleanPrefSwitch(
            property = preference::shortcutSearchIconRedirect,
            text = stringResource(id = R.string.shortcut_search_icon_redirect),
            tips = stringResource(id = R.string.shortcut_search_icon_redirect_tips)
        )
        MIUIKXBooleanPrefSwitch(
            property = preference::qsTilesCornerEnable,
            text = stringResource(id = R.string.qs_tiles_corner_enable)
        )
    }
}