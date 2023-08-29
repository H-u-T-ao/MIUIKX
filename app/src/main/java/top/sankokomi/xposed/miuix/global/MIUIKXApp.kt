package top.sankokomi.xposed.miuix.global

import android.app.Application

class MIUIKXApp: Application() {

    override fun onCreate() {
        Global.init(this)
        super.onCreate()
    }

}