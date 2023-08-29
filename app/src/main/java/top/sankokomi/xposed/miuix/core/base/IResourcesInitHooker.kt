package top.sankokomi.xposed.miuix.core.base

import de.robv.android.xposed.callbacks.XC_InitPackageResources

interface IResourcesInitHooker {

    fun isResourcesHookEnable(): Boolean

    fun hook(param: XC_InitPackageResources.InitPackageResourcesParam): Boolean

}