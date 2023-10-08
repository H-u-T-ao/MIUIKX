package top.sankokomi.xposed.miuix.tools

import android.content.res.XResources

private const val TAG = "ResTools"

fun XResources.safeReplace(
    packageName: String,
    type: String,
    name: String,
    value: Any?
) {
    try {
        setReplacement(packageName, type, name, value)
    } catch (t: Throwable) {
        LogTools.i(TAG, "${t::class.java.simpleName}: ${t.message}")
    }
}