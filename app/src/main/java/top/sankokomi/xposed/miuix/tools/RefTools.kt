@file:Suppress("NOTHING_TO_INLINE")

package top.sankokomi.xposed.miuix.tools

import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

inline fun <reified T> String.defClass(): Class<T> {
    @Suppress("UNCHECKED_CAST")
    return Class.forName(this) as Class<T>
}

inline fun String.wildClass(): Class<*> {
    return Class.forName(this)
}

fun Class<*>.accessFunction(name: String, vararg args: Class<*>): Method {
    return getDeclaredMethod(name, *args).apply {
        isAccessible = true
    }
}

fun Class<*>.accessField(name: String): Field {
    return getDeclaredField(name).apply {
        isAccessible = true
    }
}

fun hookFunction(
    cls: String,
    loader: ClassLoader,
    func: String,
    args: Array<Class<*>> = emptyArray(),
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedHelpers.findAndHookMethod(
        cls,
        loader,
        func,
        *args,
        HookBuilder<Method>().apply(builder)
    )
}

fun hookFunction(
    cls: Class<*>,
    func: String,
    args: Array<Class<*>> = emptyArray(),
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedHelpers.findAndHookMethod(
        cls,
        func,
        *args,
        HookBuilder<Method>().apply(builder)
    )
}

fun hookConstructor(
    cls: String,
    loader: ClassLoader,
    args: Array<Class<*>> = emptyArray(),
    builder: HookBuilder<Constructor<*>>.() -> Unit
) {
    XposedHelpers.findAndHookConstructor(
        cls,
        loader,
        *args,
        HookBuilder<Constructor<*>>().apply(builder)
    )
}

fun hookConstructor(
    cls: Class<*>,
    args: Array<Class<*>> = emptyArray(),
    builder: HookBuilder<Constructor<*>>.() -> Unit
) {
    XposedHelpers.findAndHookConstructor(
        cls,
        *args,
        HookBuilder<Constructor<*>>().apply(builder)
    )
}
