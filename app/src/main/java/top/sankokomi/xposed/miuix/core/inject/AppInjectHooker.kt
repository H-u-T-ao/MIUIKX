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

object AppInjectHooker : IPackageInitHooker {

    private val injectList = listOf(
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