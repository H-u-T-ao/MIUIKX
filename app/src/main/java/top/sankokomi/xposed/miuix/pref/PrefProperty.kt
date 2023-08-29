package top.sankokomi.xposed.miuix.pref

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Pref(
    val desc: String,
    val def: String
)

interface IPrefProperty<T> : ReadWriteProperty<IPrefSp, T>

class BooleanPrefProperty(
    private val key: String,
    private val default: Boolean
) : IPrefProperty<Boolean> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): Boolean {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: Boolean) {
        return thisRef.write(key, value)
    }

}

class IntPrefProperty(
    private val key: String,
    private val default: Int
) : IPrefProperty<Int> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): Int {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: Int) {
        return thisRef.write(key, value)
    }

}

class LongPrefProperty(
    private val key: String,
    private val default: Long
) : IPrefProperty<Long> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): Long {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: Long) {
        return thisRef.write(key, value)
    }

}

class FloatPrefProperty(
    private val key: String,
    private val default: Float
) : IPrefProperty<Float> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): Float {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: Float) {
        return thisRef.write(key, value)
    }

}

class StringPrefProperty(
    private val key: String,
    private val default: String
) : IPrefProperty<String> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): String {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: String) {
        return thisRef.write(key, value)
    }

}

class StringSetPrefProperty(
    private val key: String,
    private val default: Set<String>
) : IPrefProperty<Set<String>> {

    override fun getValue(thisRef: IPrefSp, property: KProperty<*>): Set<String> {
        return thisRef.read(key, default)
    }

    override fun setValue(thisRef: IPrefSp, property: KProperty<*>, value: Set<String>) {
        return thisRef.write(key, value)
    }

}