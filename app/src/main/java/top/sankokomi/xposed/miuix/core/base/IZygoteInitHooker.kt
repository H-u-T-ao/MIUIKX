package top.sankokomi.xposed.miuix.core.base

import de.robv.android.xposed.IXposedHookZygoteInit

interface IZygoteInitHooker {

    fun isZygoteHookEnable(): Boolean

    fun hook(param: IXposedHookZygoteInit.StartupParam): Boolean

}