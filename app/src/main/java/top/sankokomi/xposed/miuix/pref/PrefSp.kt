package top.sankokomi.xposed.miuix.pref

import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences
import top.sankokomi.xposed.miuix.tools.LogTools

private const val TAG = "PrefSp"

interface IPrefSp {
    fun write(key: String, value: Any) {
        LogTools.e(TAG, "XSharedPreferences(failed) is read-only")
    }
    fun <T> read(key: String, default: T): T {
        LogTools.e(TAG, "XSharedPreferences(failed)")
        return default
    }
}

class PrefSp(
    private val sp: SharedPreferences
) : IPrefSp {

    private val editor by lazy { sp.edit() }

    @Suppress("UNCHECKED_CAST")
    override fun write(key: String, value: Any) {
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Set<*> -> editor.putStringSet(key, value as Set<String>)
            else -> editor.putString(key, value.toString())
        }
        editor.apply()
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun <T> read(key: String, default: T): T {
        return when (default) {
            is Boolean -> sp.getBoolean(key, default)
            is Int -> sp.getInt(key, default)
            is Long -> sp.getLong(key, default)
            is Float -> sp.getFloat(key, default)
            is Set<*> -> sp.getStringSet(key, default as Set<String>)
            else -> sp.getString(key, default.toString())
        } as T
    }

}