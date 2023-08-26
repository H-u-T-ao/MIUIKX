package top.sankokomi.xposed.miuix.core

import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.systemui.SystemUIHookCore

@Keep
@Suppress("UNUSED")
class MIUIKXCore : IXposedHookLoadPackage {

    private val hookCoreList = listOf<IHookCore>(
        SystemUIHookCore()
    )

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam ?: return
        hookCoreList.map {
            if (!it.isEnable()) return@map
        }
    }

}