package top.sankokomi.xposed.miuix.core.inject

import android.app.Application
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.global.HostGlobal
import top.sankokomi.xposed.miuix.tools.ClassName
import top.sankokomi.xposed.miuix.tools.LogTools
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.hookFunction

private const val TAG = "AppInjectHooker"

/**
 * 在指定包名的 APP 启动时直接 hook 得到其 Application 以供后续使用
 *
 * 增添新的需要 hook 的包名时需要在 [AppInjectHooker.injectList] 内添加
 *
 * 由于不同的 app 会跑在不同的进程内，因此总是会只注册到一个目标 app
 * */
object AppInjectHooker : IPackageInitHooker {

    private val injectList = listOf(
        PackageName.ANDROID_SYSTEM_UI_PK,
        PackageName.MIUI_HOME_PK
    )

    override fun isPackageHookEnable(): Boolean = true

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (param.packageName !in injectList) return false
        hookFunction(
            ClassName.ANDROID_APPLICATION_CLS,
            param.classLoader,
            "onCreate",
            emptyArray()
        ) {
            after {
                HostGlobal.inject(_self as Application)
                LogTools.i(TAG, "injected = ${param.packageName}")
            }
        }
        return true
    }

}