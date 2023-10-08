package top.sankokomi.xposed.miuix.core.miuihome

import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.global.HostGlobal
import top.sankokomi.xposed.miuix.pref.preference
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.accessField
import top.sankokomi.xposed.miuix.tools.command
import top.sankokomi.xposed.miuix.tools.getField
import top.sankokomi.xposed.miuix.tools.hookFunction
import top.sankokomi.xposed.miuix.tools.invokeFunction
import top.sankokomi.xposed.miuix.tools.wildClass

private const val TAG = "MIUIHomeHooker"

private const val MIUI_DOCKER_SHORTCUT_ADAPTER_CLS =
    "com.miui.home.launcher.hotseats.HotSeatsListContentAdapter"

private const val MIUI_DOCKER_SHORTCUT_ABSTRACT_VIEW_HOLDER_CLS =
    "com.miui.home.launcher.hotseats.HotSeatsListContentAdapter\$ViewHolder"

object MIUIHomeShortcutHooker : IPackageInitHooker {

    private var isLoaded: Boolean = false

    override fun isPackageHookEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (isLoaded) return false
        if (param.packageName != PackageName.MIUI_HOME_PK) return false
        isLoaded = true
        redirectShortcutSearchIcon(param)
        return true
    }

    private fun redirectShortcutSearchIcon(
        param: XC_LoadPackage.LoadPackageParam
    ) {
        if (preference.shortcutSearchIconRedirect) {
            findShortcutSearchIcon(param) {
                hookShortcutSearchIcon(param, it)
            }
        }
    }

    private fun findShortcutSearchIcon(
        param: XC_LoadPackage.LoadPackageParam,
        callback: (Any) -> Unit
    ) {
        hookFunction(
            MIUI_DOCKER_SHORTCUT_ADAPTER_CLS,
            param.classLoader,
            "onCreateViewHolder",
            arrayOf(
                ViewGroup::class.java,
                Int::class.java
            )
        ) {
            after {
                // onCreateViewHolder 的第二个参数即 viewType
                // 此处 viewType = 2 表示是搜索图标
                // 将建好的 ViewHolder 返回
                if (_args[1] == 2) result?.let { callback(it) }
            }
        }
    }

    private fun hookShortcutSearchIcon(
        param: XC_LoadPackage.LoadPackageParam,
        holder: Any
    ) {
        // docker 栏搜索图标
        val searchIcon = MIUI_DOCKER_SHORTCUT_ABSTRACT_VIEW_HOLDER_CLS
            .wildClass(param.classLoader)
            .accessField("content")
            .get(holder)!!
        val isDrawerMode =
            (searchIcon getField "mLauncher")?.invokeFunction("isDrawerMode") as Boolean
        if (isDrawerMode) {
            // 重新配置点击事件
            // 开启了抽屉模式，展示抽屉
            (searchIcon as View).setOnClickListener {
                if (!HostGlobal.isAppForeground) {
                    // 明确系统桌面不在前台时，调用指令调回前台并打开抽屉
                    command("input keyevent 3", false)
                }
                (searchIcon getField "mLauncher")?.invokeFunction("showAppView")
            }
        }
    }

}