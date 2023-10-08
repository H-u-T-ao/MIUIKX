package top.sankokomi.xposed.miuix.core.systemui.plugin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import de.robv.android.xposed.XposedHelpers
import top.sankokomi.xposed.miuix.tools.HookBuilder
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.hookAllFunctions
import top.sankokomi.xposed.miuix.tools.hookFunction
import java.lang.reflect.Method

@Suppress("PrivatePropertyName")
private val MIUI_SYSTEM_UI_PLUGIN_FACTORY_CLS: String
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            "com.android.systemui.shared.plugins.PluginInstance\$Factory"
        } else {
            "com.android.systemui.shared.plugins.PluginManagerImpl"
        }
    }

private const val MIUI_SYSTEM_UI_PLUGIN_COMPONENT_FACTORY_CLS =
    "miui.systemui.dagger.PluginComponentFactory"

/**
 * 取得真正的插件类加载器
 * */
fun hookSystemUIPluginClassLoader(
    defaultClassLoader: ClassLoader,
    callback: (ClassLoader) -> Unit
) {
    val pluginFactoryCls = XposedHelpers.findClass(
        MIUI_SYSTEM_UI_PLUGIN_FACTORY_CLS,
        defaultClassLoader
    )
    hookAllFunctions(
        pluginFactoryCls,
        "getClassLoader"
    ) {
        after afterGetClassLoader@{
            if (_args.isEmpty()) return@afterGetClassLoader
            val appInfo = _args.first() ?: return@afterGetClassLoader
            if (appInfo !is ApplicationInfo) return@afterGetClassLoader
            if (appInfo.packageName == PackageName.MIUI_SYSTEM_UI_PLUGIN_PK) {
                // 系统界面组件是插件化的，这里是要获得加载了系统界面组件的类加载器
                callback(result as ClassLoader)
            }
        }
    }
}

fun hookSystemUIPluginComponentCreate(
    pluginClassLoader: ClassLoader,
    builder: HookBuilder<Method>.() -> Unit
) {
    hookFunction(
        MIUI_SYSTEM_UI_PLUGIN_COMPONENT_FACTORY_CLS,
        pluginClassLoader,
        "create",
        arrayOf(Context::class.java),
        builder
    )
}