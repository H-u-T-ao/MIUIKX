package top.sankokomi.xposed.miuix.tools

import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Member

class HookBuilder<T : Member> : XC_MethodHook() {

    private lateinit var hookParam: MethodHookParam

    @Suppress("UNCHECKED_CAST")
    val method: T get() = hookParam.method as T

    val self: Any get() = hookParam.thisObject

    val args: Any get() = hookParam.args

    private var replacement = false

    private var replace: (() -> Any?)? = null

    private var before: (() -> Unit)? = null

    private var after: (() -> Unit)? = null

    fun replace(replace: () -> Any?) {
        replacement = true
        this.replace = replace
    }

    fun before(before: () -> Unit) {
        replacement = false
        this.before = before
    }

    fun after(after: () -> Unit) {
        replacement = false
        this.after = after
    }

    override fun beforeHookedMethod(param: MethodHookParam) {
        this.hookParam = param
        if (!replacement) {
            before?.invoke()
        } else {
            try {
                param.result = replace?.invoke()
            } catch (t: Throwable) {
                param.throwable = t
            }
        }
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        this.hookParam = param
        if (!replacement) {
            after?.invoke()
        }
    }

}