package top.sankokomi.xposed.miuix.core

import de.robv.android.xposed.callbacks.XC_LoadPackage

interface IHookCore {

    fun isEnable(): Boolean = true

    fun hook(param: XC_LoadPackage.LoadPackageParam)

}