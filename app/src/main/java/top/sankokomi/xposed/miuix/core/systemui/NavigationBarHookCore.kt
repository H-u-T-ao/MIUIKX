package top.sankokomi.xposed.miuix.core.systemui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.IHookCore
import top.sankokomi.xposed.miuix.tools.LogTools
import top.sankokomi.xposed.miuix.tools.hookConstructor

private const val TAG = "NavigationBarHookCore"

private const val SYSTEM_UI_PK = "com.android.systemui"

private const val MIUI_NAVIGATION_BAR_FRAME_CLS =
    "com.android.systemui.navigationbar.NavigationBarFrame"

class NavigationBarHookCore : IHookCore {

    private var isLoaded: Boolean = false

    override fun isEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (isLoaded) return false
        if (param.packageName != SYSTEM_UI_PK) return false
        val navFrameCls = XposedHelpers.findClass(
            MIUI_NAVIGATION_BAR_FRAME_CLS,
            param.classLoader
        )
        isLoaded = true
        hookConstructor(
            FrameLayout::class.java,
            arrayOf(
                Context::class.java,
                AttributeSet::class.java,
                Int::class.java,
                Int::class.java
            )
        ) {
            after {
                if (self::class.java == navFrameCls) {
                    val frame = (self as FrameLayout)
                    frame.post {
                        frame.visibility = View.GONE
                        LogTools.i(TAG, "hide NavigationBar success")
                    }
                }
            }
        }
        return true
    }

}