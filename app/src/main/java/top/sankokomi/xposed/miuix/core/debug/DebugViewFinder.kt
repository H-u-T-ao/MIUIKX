package top.sankokomi.xposed.miuix.core.debug

import android.view.View
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.tools.ClassName
import top.sankokomi.xposed.miuix.tools.hookFunction
import top.sankokomi.xposed.miuix.tools.printSuperViewTree

object DebugViewFinder : IPackageInitHooker {

    private var isLoaded: Boolean = false

    override fun isPackageHookEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        hookView(param)
        return true
    }

    private fun hookView(param: XC_LoadPackage.LoadPackageParam) {
        hookFunction(
            ClassName.ANDROID_VIEW_CLS,
            param.classLoader,
            "setContentDescription",
            arrayOf(
                CharSequence::class.java
            )
        ) {
            before {
                if (_args[0].toString() == "手电筒,关闭") {
                    (_self as View).printSuperViewTree()
                }
            }
        }
    }

}