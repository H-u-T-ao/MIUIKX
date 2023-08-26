package top.sankokomi.xposed.miuix.tools

import java.io.DataOutputStream

fun command(command: String, isSu: Boolean) {
    try {
        if (isSu) {
            val p = Runtime.getRuntime().exec("su")
            val outputStream = p.outputStream
            DataOutputStream(outputStream).apply {
                writeBytes(command)
                flush()
                close()
            }
            outputStream.close()
        } else {
            Runtime.getRuntime().exec(command)
        }
    } catch (ignored: Throwable) {
    }
}