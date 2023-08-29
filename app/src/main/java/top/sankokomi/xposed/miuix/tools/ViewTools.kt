package top.sankokomi.xposed.miuix.tools

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent

private const val TAG = "ViewTools"

fun filterView(
    filter: (View) -> Boolean,
    todo: (View) -> Unit = {}
) {
    hookConstructor(
        View::class.java,
        arrayOf(
            Context::class.java
        )
    ) {
        after {
            val sel = _self
            (sel as View).post {
                if (filter(sel)) todo(sel)
            }
        }
    }
    hookConstructor(
        View::class.java,
        arrayOf(
            Context::class.java,
            AttributeSet::class.java
        )
    ) {
        after {
            val sel = _self
            (sel as View).post {
                if (filter(sel)) todo(sel)
            }
        }
    }
    hookConstructor(
        View::class.java,
        arrayOf(
            Context::class.java,
            AttributeSet::class.java,
            Int::class.java
        )
    ) {
        after {
            val sel = _self
            (sel as View).post {
                if (filter(sel)) todo(sel)
            }
        }
    }
}

fun View.printSuperViewTree() {
    if (parent == null) LogTools.v(TAG, this@printSuperViewTree::class.java.name)
    LogTools.v(
        TAG, parent.printSuperViewTreeInternal(
            StringBuilder("\n").apply {
                append(this@printSuperViewTree::class.java.name)
                append("\n-")
                append(parent::class.java.name)
            },
            1
        ).toString()
    )
}

private fun ViewParent.printSuperViewTreeInternal(str: StringBuilder, deep: Int): StringBuilder {
    if (parent == null) return str
    str.append("\n")
    for (i in 0..deep) {
        str.append("-")
    }
    str.append(parent::class.java.name)
    return parent.printSuperViewTreeInternal(str, deep + 1)
}