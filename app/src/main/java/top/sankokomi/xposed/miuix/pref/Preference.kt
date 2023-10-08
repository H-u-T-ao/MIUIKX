package top.sankokomi.xposed.miuix.pref

import android.content.Context
import de.robv.android.xposed.XSharedPreferences
import top.sankokomi.xposed.miuix.BuildConfig
import top.sankokomi.xposed.miuix.global.Global
import top.sankokomi.xposed.miuix.tools.PrefKey

class Preference(
    private val prefSp: IPrefSp
) : IPrefSp by prefSp {

    @Pref(
        "新版控制中心纵向每页开关行数",
        "默认 4 , >= 3"
    )
    var qsPagerVerticalRow by IntPrefProperty(
        "qs_pager_vertical_row",
        4
    )

    @Pref(
        "新版控制中心纵向每页开关列数",
        "默认 4 , >= 1"
    )
    var qsPagerVerticalColumn by IntPrefProperty(
        "qs_pager_vertical_column",
        4
    )

    @Pref(
        "新版控制中心横向每页开关行数",
        "默认 4 , >= 3"
    )
    var qsPagerOrientationRow by IntPrefProperty(
        "qs_pager_orientation_row",
        4
    )

    @Pref(
        "新版控制中心横向每页开关列数",
        "默认 4 , >= 1"
    )
    var qsPagerOrientationColumn by IntPrefProperty(
        "qs_pager_orientation_column",
        4
    )

    @Pref(
        "系统桌面快捷栏的搜索按钮重定向，" +
                "true 表示若开启了抽屉模式，则重定向到打开抽屉，其余情况不生效",
        "默认 false"
    )
    var shortcutSearchIconRedirect by BooleanPrefProperty(
        "shortcut_search_icon_redirect",
        false
    )

    @Pref(
        "是否开启控制中心磁贴圆角矩形",
        "默认 false"
    )
    var qsTilesCornerEnable by BooleanPrefProperty(
        "qs_tiles_corner_enable",
        false
    )

}

val preference by lazy { create() }

@Suppress("DEPRECATION", "WorldReadableFiles")
private fun create(key: String = PrefKey.PREFERENCE_MAIN_KEY): Preference {
    return if (Global.isMIUIKX) {
        Preference(
            PrefSp(
                Global.context
                    .createDeviceProtectedStorageContext()
                    .getSharedPreferences(key, Context.MODE_WORLD_READABLE)
            )
        )
    } else {
        val sp = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
        Preference(
            if (!sp.file.canRead()) {
                object : IPrefSp {}
            } else {
                PrefSp(sp)
            }
        )
    }
}