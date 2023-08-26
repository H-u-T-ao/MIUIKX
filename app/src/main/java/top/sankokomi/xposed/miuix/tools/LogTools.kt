package top.sankokomi.xposed.miuix.tools

import android.util.Log
import top.sankokomi.xposed.miuix.global.Global

private const val XP_LOG_TAG = "LSPosed-Bridge"

private const val MIUIKX_LOG_TAG = "MIUIKX"

object LogTools {

    fun v(tag: String, msg: String, t: Throwable? = null) {
        if (!Global.isDebug) return
        Log.v(tag, msg, t)
        // also log in xposed
        Log.v(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

    fun d(tag: String, msg: String, t: Throwable? = null) {
        if (!Global.isDebug) return
        Log.d(tag, msg, t)
        // also log in xposed
        Log.d(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

    fun i(tag: String, msg: String, t: Throwable? = null) {
        if (!Global.isDebug) return
        Log.i(tag, msg, t)
        // also log in xposed
        Log.i(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

    fun w(tag: String, msg: String, t: Throwable? = null) {
        Log.w(tag, msg, t)
        // also log in xposed
        Log.w(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

    fun e(tag: String, msg: String, t: Throwable? = null) {
        Log.e(tag, msg, t)
        // also log in xposed
        Log.e(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

    fun wtf(tag: String, msg: String, t: Throwable? = null) {
        Log.wtf(tag, msg, t)
        // also log in xposed
        Log.wtf(XP_LOG_TAG, "[$MIUIKX_LOG_TAG] $tag $msg", t)
    }

}