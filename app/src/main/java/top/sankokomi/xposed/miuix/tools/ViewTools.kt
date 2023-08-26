package top.sankokomi.xposed.miuix.tools

import android.view.View
import android.view.ViewParent

private const val TAG = "ViewTools"

fun View.printViewTree() {
    if (parent == null) LogTools.v(TAG, this@printViewTree::class.java.name)
    LogTools.v(
        TAG, parent.printViewTreeInternal(
            StringBuilder("\n").apply {
                append(this@printViewTree::class.java.name)
                append("\n-")
                append(parent::class.java.name)
            },
            1
        ).toString()
    )
}

private fun ViewParent.printViewTreeInternal(str: StringBuilder, deep: Int): StringBuilder {
    if (parent == null) return str
    str.append("\n")
    for (i in 0..deep) {
        str.append("-")
    }
    str.append(parent::class.java.name)
    return parent.printViewTreeInternal(str, deep + 1)
}