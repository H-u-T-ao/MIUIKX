package top.sankokomi.xposed.miuix.core.systemui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.tools.LogTools
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.hookConstructor

private const val TAG = "NavigationBarHookCore"

private const val MIUI_NAVIGATION_BAR_FRAME_CLS =
    "com.android.systemui.navigationbar.NavigationBarFrame"

object NavigationBarHooker : IPackageInitHooker {

    private var isLoaded: Boolean = false

    override fun isPackageHookEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (isLoaded) return false
        if (param.packageName != PackageName.ANDROID_SYSTEM_UI_PK) return false
        isLoaded = true
        val navFrameCls = XposedHelpers.findClass(
            MIUI_NAVIGATION_BAR_FRAME_CLS,
            param.classLoader
        )
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
                    // 匹配 NavigationBarFrame
                    val frame = (self as FrameLayout)
                    frame.post {
                        // 将 NavigationBarFrame 隐藏掉
                        frame.visibility = View.GONE
                        LogTools.i(TAG, "hide NavigationBar success")
                    }
                }
            }
        }
        return true
    }

}