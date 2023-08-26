package top.sankokomi.xposed.miuix.core

import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.systemui.NavigationBarHookCore
import top.sankokomi.xposed.miuix.tools.LogTools

private const val TAG = "MIUIKXCore"

@Keep
@Suppress("UNUSED")
class MIUIKXCore : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hookCoreList = listOf<IHookCore>(
        NavigationBarHookCore()
    )

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            LogTools.d(TAG, "pm = ${lpparam.packageName}")
            hookCoreList.map {
                try {
                    if (!it.isEnable()) {
                        LogTools.d(TAG, "${it.javaClass.simpleName} enable false")
                        return@map
                    }
                    LogTools.d(TAG, "${it.javaClass.simpleName} launch")
                    val result = it.hook(lpparam)
                    LogTools.d(TAG, "${it.javaClass.simpleName} load $result")
                } catch (t: Throwable) {
                    // catch to protect the process
                    LogTools.e(TAG, "${it.javaClass.simpleName} crash", t)
                }
            }
        } catch (t: Throwable) {
            // catch to protect the process
            LogTools.e(TAG, "crash", t)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        LogTools.d(TAG, "(zygote) init")
    }

}