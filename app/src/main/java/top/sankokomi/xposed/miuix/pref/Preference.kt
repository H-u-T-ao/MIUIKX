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
        "新版控制中心每页开关行数",
        "默认 4 , >= 3"
    )
    var qsPagerRow by IntPrefProperty(
        "qs_pager_row",
        4
    )

    @Pref(
        "新版控制中心每页开关列数",
        "默认 4 , >= 1"
    )
    var qsPagerColumn by IntPrefProperty(
        "qs_pager_column",
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