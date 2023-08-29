package top.sankokomi.xposed.miuix.core

import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.core.base.IResourcesInitHooker
import top.sankokomi.xposed.miuix.core.base.IZygoteInitHooker
import top.sankokomi.xposed.miuix.core.systemui.ControlCenterHooker
import top.sankokomi.xposed.miuix.core.systemui.NavigationBarHooker
import top.sankokomi.xposed.miuix.tools.LogTools

private const val TAG_ZYGOTE = "MIUIKXCore-Zygote"

private const val TAG_PK = "MIUIKXCore-Package"

private const val TAG_RES = "MIUIKXCore-Resources"

@Keep
@Suppress("UNUSED")
class MIUIKXCore1 :
    IXposedHookZygoteInit,
    IXposedHookLoadPackage,
    IXposedHookInitPackageResources {

    private val zygoteInitHookList = listOf<IZygoteInitHooker>(
    )

    private val packageInitHookList = listOf<IPackageInitHooker>(
        NavigationBarHooker,
        ControlCenterHooker
    )

    private val resourcesInitHookList = listOf<IResourcesInitHooker>(
        ControlCenterHooker
    )

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        // LogTools.d(TAG_ZYGOTE, "(Zygote) init pm = ${startupParam.modulePath}")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // LogTools.d(TAG_PK, "(App) init pm = ${lpparam.packageName}")
        packageInitHookList.map {
            try {
                if (!it.isPackageHookEnable()) {
                    LogTools.d(TAG_PK, "${it.javaClass.simpleName} enable false")
                    return@map
                }
                val result = it.hook(lpparam)
                if (result) {
                    LogTools.d(TAG_PK, "${it.javaClass.simpleName} launch success")
                }
            } catch (t: Throwable) {
                // catch to protect the process
                LogTools.e(TAG_PK, "${it.javaClass.simpleName} crash", t)
            }
        }
    }

    override fun handleInitPackageResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam
    ) {
        // LogTools.d(TAG_RES, "(Resources) init pm = ${resparam.packageName}")
        resourcesInitHookList.map {
            try {
                if (!it.isResourcesHookEnable()) {
                    LogTools.d(TAG_RES, "${it.javaClass.simpleName} enable false")
                    return@map
                }
                val result = it.hook(resparam)
                if (result) {
                    LogTools.d(TAG_RES, "${it.javaClass.simpleName} launch success")
                }
            } catch (t: Throwable) {
                // catch to protect the process
                LogTools.e(TAG_RES, "${it.javaClass.simpleName} crash", t)
            }
        }
    }

}