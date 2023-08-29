@file:Suppress("NOTHING_TO_INLINE")

package top.sankokomi.xposed.miuix.tools

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

inline infix fun <reified T> String.defClass(loader: ClassLoader): Class<T> {
    @Suppress("UNCHECKED_CAST")
    return Class.forName(this, true, loader) as Class<T>
}

inline infix fun String.wildClass(loader: ClassLoader): Class<*> {
    return Class.forName(this, true, loader)
}

fun Class<*>.accessConstructor(vararg args: Class<*>): Constructor<*> {
    return getDeclaredConstructor(*args).apply {
        isAccessible = true
    }
}

infix fun Class<*>.accessField(name: String): Field {
    return getDeclaredField(name).apply {
        isAccessible = true
    }
}

fun Class<*>.accessFunction(name: String, vararg args: Class<*>): Method {
    return getDeclaredMethod(name, *args).apply {
        isAccessible = true
    }
}

inline fun <reified T> T.copyTo(target: T) {
    val cls = T::class.java
    cls.declaredFields.map {
        it?.isAccessible = true
        it.set(target, it.get(this@copyTo))
    }
}

fun Class<*>.invokeConstructor(vararg args: Pair<Class<*>, Any?>): Any {
    val size = args.size
    val inputClass = Array(size) { args[it].first }
    val inputInstance = Array(size) { args[it].second }
    return this.accessConstructor(*inputClass).newInstance(*inputInstance)
}

inline infix fun <reified T> Any.requireField(field: String): T {
    return this::class.java.accessField(field).get(this) as T
}

infix fun Any.getField(field: String): Any? {
    return this::class.java.accessField(field).get(this)
}

inline fun <reified T> Any.setField(field: String, value: T) {
    return this::class.java.accessField(field).set(this, value)
}

fun Any.invokeFunction(func: String, vararg args: Pair<Class<*>, Any?>): Any? {
    val size = args.size
    val inputClass = Array(size) { args[it].first }
    val inputInstance = Array(size) { args[it].second }
    return this::class.java.accessFunction(func, *inputClass).invoke(this, *inputInstance)
}

fun hookConstructor(
    cls: String,
    loader: ClassLoader,
    args: Array<Class<*>>,
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
    args: Array<Class<*>>,
    builder: HookBuilder<Constructor<*>>.() -> Unit
) {
    XposedHelpers.findAndHookConstructor(
        cls,
        *args,
        HookBuilder<Constructor<*>>().apply(builder)
    )
}

fun hookAllConstructors(
    cls: String,
    loader: ClassLoader,
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedBridge.hookAllConstructors(
        XposedHelpers.findClass(cls, loader),
        HookBuilder<Method>().apply(builder)
    )
}

fun hookAllConstructors(
    cls: Class<*>,
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedBridge.hookAllConstructors(
        cls,
        HookBuilder<Method>().apply(builder)
    )
}

fun hookFunction(
    cls: String,
    loader: ClassLoader,
    func: String,
    args: Array<Class<*>>,
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
    args: Array<Class<*>>,
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedHelpers.findAndHookMethod(
        cls,
        func,
        *args,
        HookBuilder<Method>().apply(builder)
    )
}

fun hookAllFunctions(
    cls: String,
    loader: ClassLoader,
    func: String,
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedBridge.hookAllMethods(
        XposedHelpers.findClass(cls, loader),
        func,
        HookBuilder<Method>().apply(builder)
    )
}

fun hookAllFunctions(
    cls: Class<*>,
    func: String,
    builder: HookBuilder<Method>.() -> Unit
) {
    XposedBridge.hookAllMethods(
        cls,
        func,
        HookBuilder<Method>().apply(builder)
    )
}
