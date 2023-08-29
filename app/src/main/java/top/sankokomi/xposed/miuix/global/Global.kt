package top.sankokomi.xposed.miuix.global

import android.app.Application
import android.content.Context
import top.sankokomi.xposed.miuix.BuildConfig

@Suppress("StaticFieldLeak")
object Global {

    fun init(app: Application) {
        _context = app
    }

    val isDebug: Boolean = BuildConfig.DEBUG

    val context: Context get() = _context!!

    /**
     * 当前是 MIUIKX
     * */
    val isMIUIKX: Boolean get() = _context != null

    /**
     * 当前是被 hook 的宿主
     * */
    val isHookHost: Boolean get() = _context == null

    private var _context: Context? = null

}