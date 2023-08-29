package top.sankokomi.xposed.miuix.core.systemui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.AttributeSet
import android.view.View
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.core.base.IResourcesInitHooker
import top.sankokomi.xposed.miuix.pref.preference
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.accessFunction
import top.sankokomi.xposed.miuix.tools.copyTo
import top.sankokomi.xposed.miuix.tools.getField
import top.sankokomi.xposed.miuix.tools.hookAllFunctions
import top.sankokomi.xposed.miuix.tools.hookFunction
import top.sankokomi.xposed.miuix.tools.invokeConstructor
import top.sankokomi.xposed.miuix.tools.invokeFunction
import top.sankokomi.xposed.miuix.tools.requireField
import top.sankokomi.xposed.miuix.tools.setField
import top.sankokomi.xposed.miuix.tools.wildClass

private const val TAG = "ControlCenterHookCore"

@Suppress("PrivatePropertyName")
private val MIUI_SYSTEM_UI_PLUGIN_FACTORY_CLS: String
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            "com.android.systemui.shared.plugins.PluginInstance\$Factory"
        } else {
            "com.android.systemui.shared.plugins.PluginManagerImpl"
        }
    }

private const val MIUI_QS_PAGER_CLS = "miui.systemui.controlcenter.qs.QSPager"

private const val MIUI_TILE_LAYOUT_CLS = "miui.systemui.controlcenter.qs.TileLayoutImpl"

private const val MIUI_BIG_TILE_CTRL_CLS =
    "miui.systemui.controlcenter.qs.tileview.BigTileGroupController"

private const val ANDROIDX_VIEW_PAGER_CLS = "androidx.viewpager.widget.ViewPager"

private const val ANDROIDX_PAGER_ADAPTER_CLS = "androidx.viewpager.widget.PagerAdapter"

object ControlCenterHooker : IPackageInitHooker {

    private var isLoaded: Boolean = false

    override fun isPackageHookEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (isLoaded) return false
        if (param.packageName != PackageName.ANDROID_SYSTEM_UI_PK) return false
        isLoaded = true
        val row = preference.qsPagerRow.coerceAtLeast(2)
        val column = preference.qsPagerColumn.coerceAtLeast(1)
        if (row != 4 || column != 4) {
            // 跟 miui 不一样才 hook
            hookToGetPluginClassLoader(param.classLoader) {
                adjustControlCenterSwitchPageSize(
                    it,
                    row,
                    column
                )
            }
        }
        return true
    }

    /**
     * 取得真正的插件类加载器
     * */
    private fun hookToGetPluginClassLoader(
        defaultClassLoader: ClassLoader,
        callback: (ClassLoader) -> Unit
    ) {
        val pluginFactoryCls = XposedHelpers.findClass(
            MIUI_SYSTEM_UI_PLUGIN_FACTORY_CLS,
            defaultClassLoader
        )
        hookAllFunctions(
            pluginFactoryCls,
            "getClassLoader"
        ) {
            after afterGetClassLoader@{
                if (_args.isEmpty()) return@afterGetClassLoader
                val appInfo = _args.first() ?: return@afterGetClassLoader
                if (appInfo !is ApplicationInfo) return@afterGetClassLoader
                if (appInfo.packageName == PackageName.MIUI_SYSTEM_UI_PLUGIN_PK) {
                    // 系统界面组件是插件化的，这里是要获得加载了系统界面组件的类加载器
                    callback(result as ClassLoader)
                }
            }
        }
    }

    /**
     * 调整新版控制中心页面尺寸
     * */
    private fun adjustControlCenterSwitchPageSize(
        pluginClassLoader: ClassLoader,
        row: Int,
        column: Int
    ) {
        hookFunction(
            MIUI_QS_PAGER_CLS,
            pluginClassLoader,
            "distributeTiles",
            emptyArray()
        ) {
            after afterPagerDistributeTiles@{
                if (_self requireField "collapse") {
                    // 这个变量存储了所有的按钮
                    val records = _self.requireField<ArrayList<*>>("records")
                    if (records.isEmpty()) {
                        // 一个按钮都没有，不用改
                        return@afterPagerDistributeTiles
                    }
                    // 移除原有的页面
                    val pages = (_self.requireField<ArrayList<Any>>("pages"))
                    for (page in pages) {
                        page.invokeFunction("removeTiles")
                    }
                    // 控制中心上方的 header
                    val header = _self getField "header"
                    // 计算每个页面上应该存在的按钮
                    // 这里返回的列表不可能为空，没有按钮的情况已经在上面考虑了
                    val newPages = calculateSwitchLocation(records, column, row)
                    for (i in newPages.indices) {
                        if (i >= pages.size) {
                            // 页面不够的情况下就新加一个
                            val origin = pages[0] as View
                            val tileLayoutCls =
                                MIUI_TILE_LAYOUT_CLS.wildClass(pluginClassLoader)
                            // 先新建一个默认的
                            val instance = tileLayoutCls.invokeConstructor(
                                Context::class.java to origin.context,
                                AttributeSet::class.java to null
                            )
                            // 用原来的实例通过拷贝得到一个新的
                            origin.copyTo(instance)
                            pages.add(instance)
                        }
                        // 设置页面
                        pages[i].invokeFunction(
                            "setTiles",
                            Collection::class.java to newPages[i],
                            // 第一页把 header 加上去
                            MIUI_BIG_TILE_CTRL_CLS.wildClass(
                                pluginClassLoader
                            ) to if (i == 0) header else null
                        )
                    }
                    while (pages.size > newPages.size) {
                        pages.removeLast()
                    }
                    _self.setField("rows", row)
                    _self.setField("pages", pages)
                    val pageIndicator = _self getField "pageIndicator"
                    pageIndicator?.invokeFunction(
                        "setNumPages",
                        Int::class.java to pages.size
                    )
                    // 这里必须用插件的类加载器来加载这些安卓库，否则它们并不是同一个类
                    ANDROIDX_VIEW_PAGER_CLS.wildClass(pluginClassLoader).accessFunction(
                        "setAdapter",
                        ANDROIDX_PAGER_ADAPTER_CLS.wildClass(pluginClassLoader)
                    ).invoke(_self, _self getField "adapter")
                }
            }
        }
    }

    /**
     * 计算每个页面包含的按钮
     * */
    private fun calculateSwitchLocation(
        records: List<*>,
        column: Int,
        row: Int
    ): ArrayList<ArrayList<*>> {
        val result = arrayListOf<ArrayList<*>>()
        val currentPageSwitches = arrayListOf<Any>()
        // 行数从索引 2 开始是因为控制中心上方的 header 占两行
        var currentRow = 2
        // 列数稍后会 +1 ，因此此处设置为 -1
        var currentColumn = -1
        // 当前页面索引
        var currentPage = 0
        for (record in records) {
            record ?: continue
            currentColumn++
            if (currentColumn >= column) {
                // 列数满了，要换行了
                currentColumn = 0
                currentRow++
            }
            if (currentRow >= row) {
                // 行数满了，要换页了
                currentRow = 0
                // 把当前页的所有开关添加到返回结果的列表中
                result.add(ArrayList(currentPageSwitches))
                // 清除当前页开关的临时存储列表，准备进入新的一页
                currentPageSwitches.clear()
                currentPage++
            }
            // 将当前遍历到的按钮添加到当前页开关的临时存储列表中
            currentPageSwitches.add(record)
        }
        if (currentPageSwitches.isNotEmpty()) {
            // 将最后剩下的未满一页的开关添加到最后一页
            result.add(currentPageSwitches)
        }
        return result
    }

}