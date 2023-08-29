package top.sankokomi.xposed.miuix.core.base

import de.robv.android.xposed.callbacks.XC_LoadPackage

interface IPackageInitHooker {

    fun isPackageHookEnable(): Boolean

    fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean

}