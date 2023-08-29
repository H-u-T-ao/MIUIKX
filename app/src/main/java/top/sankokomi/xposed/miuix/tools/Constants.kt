package top.sankokomi.xposed.miuix.tools

object PackageName {
    const val ANDROID_SYSTEM_UI_PK = "com.android.systemui"
    const val MIUI_SYSTEM_UI_PLUGIN_PK = "miui.systemui.plugin"
    const val MIUI_HOME_PK = "com.miui.home"
}

object ClassName {
    const val ANDROID_APPLICATION_CLS = "android.app.Application"
    const val ANDROID_VIEW_CLS = "android.view.View"
    const val ANDROID_VIEW_ON_CLICK_LISTENER_CLS = "android.view.View\$OnClickListener"
    const val ANDROID_IMAGE_VIEW_CLS = "android.widget.ImageView"
}

object PrefKey {
    const val PREFERENCE_MAIN_KEY = "miuikx_preference_store"
}