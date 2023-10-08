package top.sankokomi.xposed.miuix.global

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import top.sankokomi.xposed.miuix.BuildConfig
import top.sankokomi.xposed.miuix.tools.LogTools

private const val TAG = "HostGlobal"

@Suppress("StaticFieldLeak")
object HostGlobal {

    fun inject(app: Application) {
        _context = app
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                LogTools.d(TAG, "${app.packageName} ---> foreground")
                _isAppForeground = true
            }

            override fun onActivityPaused(activity: Activity) {
                _isAppForeground = false
                LogTools.d(TAG, "${app.packageName} ---> background")
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })
    }

    val isDebug: Boolean = BuildConfig.DEBUG

    val context: Context get() = _context!!

    val miuikxContext: Context by lazy {
        context.createPackageContext(
            BuildConfig.APPLICATION_ID,
            Context.CONTEXT_IGNORE_SECURITY
        ).createDeviceProtectedStorageContext()
    }

    /**
     * 当前是 MIUIKX
     * */
    val isMIUIKX: Boolean get() = _context == null

    /**
     * 当前是被 hook 的宿主
     * */
    val isHookHost: Boolean get() = _context != null

    /**
     * 当前宿主是否在前台
     * */
    val isAppForeground: Boolean get() = _isAppForeground

    private var _context: Context? = null

    private var _isAppForeground: Boolean = false

}