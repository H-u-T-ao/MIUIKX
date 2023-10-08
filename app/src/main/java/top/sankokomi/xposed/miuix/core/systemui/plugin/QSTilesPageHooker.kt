package top.sankokomi.xposed.miuix.core.systemui.plugin

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sankokomi.xposed.miuix.R
import top.sankokomi.xposed.miuix.core.base.IPackageInitHooker
import top.sankokomi.xposed.miuix.global.HostGlobal
import top.sankokomi.xposed.miuix.pref.preference
import top.sankokomi.xposed.miuix.tools.HookBuilder
import top.sankokomi.xposed.miuix.tools.PackageName
import top.sankokomi.xposed.miuix.tools.accessFunction
import top.sankokomi.xposed.miuix.tools.copyTo
import top.sankokomi.xposed.miuix.tools.getField
import top.sankokomi.xposed.miuix.tools.hookFunction
import top.sankokomi.xposed.miuix.tools.invokeConstructor
import top.sankokomi.xposed.miuix.tools.invokeFunction
import top.sankokomi.xposed.miuix.tools.requireField
import top.sankokomi.xposed.miuix.tools.setField
import top.sankokomi.xposed.miuix.tools.wildClass
import java.lang.reflect.Method

private const val TAG = "QSTilesHooker"

private const val MIUI_QS_PAGER_CLS = "miui.systemui.controlcenter.qs.QSPager"

private const val MIUI_TILE_LAYOUT_CLS = "miui.systemui.controlcenter.qs.TileLayoutImpl"

private const val MIUI_BIG_TILE_CTRL_CLS =
    "miui.systemui.controlcenter.qs.tileview.BigTileGroupController"

private const val ANDROIDX_VIEW_PAGER_CLS = "androidx.viewpager.widget.ViewPager"

private const val ANDROIDX_PAGER_ADAPTER_CLS = "androidx.viewpager.widget.PagerAdapter"

private const val MIUI_EXPANDABLE_ICON_VIEW_CLS =
    "miui.systemui.controlcenter.qs.tileview.ExpandableIconView"

object QSTilesPageHooker : IPackageInitHooker {

    private var isLoaded: Boolean = false

    private val verticalPageSize = TilesPageSize(4, 4)

    private val orientationPageSize = TilesPageSize(4, 4)

    override fun isPackageHookEnable(): Boolean = !isLoaded

    override fun hook(param: XC_LoadPackage.LoadPackageParam): Boolean {
        if (isLoaded) return false
        if (param.packageName != PackageName.ANDROID_SYSTEM_UI_PK) return false
        isLoaded = true
        adjustQSTilesPagePreference(param)
        return true
    }

    private fun adjustQSTilesPagePreference(
        param: XC_LoadPackage.LoadPackageParam
    ) {
        verticalPageSize.row = preference.qsPagerVerticalRow.coerceAtLeast(2)
        verticalPageSize.column = preference.qsPagerVerticalColumn.coerceAtLeast(1)
        orientationPageSize.row = preference.qsPagerOrientationRow.coerceAtLeast(2)
        orientationPageSize.column = preference.qsPagerOrientationColumn.coerceAtLeast(1)
        hookSystemUIPluginClassLoader(param.classLoader) {
            hookTilesPageSize(it)
            hookTilesCornerRadius(it)
            hookSystemUIPluginComponentCreate(it) {
                before {
                    hookTilesCornerRadius(_args[0] as Context)
                }
            }
        }
    }

    private fun hookTilesCornerRadius(pluginClassLoader: ClassLoader) {
        if (!preference.qsTilesCornerEnable) return
        val expandableIconViewCls = MIUI_EXPANDABLE_ICON_VIEW_CLS.wildClass(pluginClassLoader)
        hookFunction(
            expandableIconViewCls,
            "setCornerRadius",
            arrayOf(Float::class.java)
        ) {
            before {
                _args[0] = HostGlobal.context.resources.displayMetrics.density * 18F
            }
        }
    }

    /**
     * 调整磁贴为圆角矩形
     * */
    @Suppress("DiscouragedApi")
    private fun hookTilesCornerRadius(context: Context) {
        if (!preference.qsTilesCornerEnable) return

        val resourcesCls = Resources::class.java
        val themeCls = Resources.Theme::class.java

        val enableBgResId = context.resources
            .getIdentifier(
                "qs_background_enabled",
                "drawable",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            )
        val disableBgResId = context.resources
            .getIdentifier(
                "qs_background_disabled",
                "drawable",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            )
        val unavailableBgResId = context.resources
            .getIdentifier(
                "qs_background_unavailable",
                "drawable",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            )
        val restrictBgResId = context.resources
            .getIdentifier(
                "qs_background_restricted",
                "drawable",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            )
        val enableColor = context.resources.getColor(
            context.resources.getIdentifier(
                "qs_enabled_color",
                "color",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            ), null
        ) // qsEnableColor
        val disableColor = context.resources.getColor(
            context.resources.getIdentifier(
                "qs_disabled_color",
                "color",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            ), null
        ) // qsDisableColor
        val unavailableColor = context.resources.getColor(
            context.resources.getIdentifier(
                "qs_unavailable_color",
                "color",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            ), null
        ) // qsUnavailableColor
        val restrictColor = context.resources.getColor(
            context.resources.getIdentifier(
                "qs_restrict_color",
                "color",
                PackageName.MIUI_SYSTEM_UI_PLUGIN_PK
            ), null
        ) // qsRestrictColor

        val qsTileBgColorMap = mapOf(
            enableBgResId to enableColor,
            disableBgResId to disableColor,
            unavailableBgResId to unavailableColor,
            restrictBgResId to restrictColor,
        )

        hookFunction(
            resourcesCls,
            "getDrawable",
            arrayOf(Int::class.java)
        ) {
            replaceTilesBgRes(qsTileBgColorMap)
        }

        hookFunction(
            themeCls,
            "getDrawable",
            arrayOf(Int::class.java)
        ) {
            replaceTilesBgRes(qsTileBgColorMap)
        }
    }

    private fun HookBuilder<Method>.replaceTilesBgRes(
        replacementMap: Map<Int, Int>
    ) {
        before {
            val color = replacementMap[_args[0] as Int] ?: return@before
            result = HostGlobal.miuikxContext.resources.getDrawable(
                R.drawable.bg_qs_tile_corner_rect,
                null
            ).apply {
                setTint(color)
            }
        }
    }

    /**
     * 调整新版控制中心页面尺寸
     * */
    private fun hookTilesPageSize(pluginClassLoader: ClassLoader) {
        if (verticalPageSize.row == 4 && verticalPageSize.column == 4
            && orientationPageSize.row == 4 && orientationPageSize.column == 4
        ) {
            // 默认 4 * 4 不做修改
            return
        }
        hookFunction(
            MIUI_QS_PAGER_CLS,
            pluginClassLoader,
            "distributeTiles",
            emptyArray()
        ) {
            after afterPagerDistributeTiles@{
                if (_self requireField "collapse") {
                    // 取得当前屏幕方向
                    val rotation = requireRotation()
                    val row: Int
                    val column: Int
                    if (rotation == 0) {
                        // 纵向，0
                        row = verticalPageSize.row
                        column = verticalPageSize.column
                        // 未修改纵向的尺寸配置
                        if (row == 4 && column == 4) return@afterPagerDistributeTiles
                    } else {
                        // 横向，90
                        row = orientationPageSize.row
                        column = orientationPageSize.column
                        // 未修改横向的尺寸配置
                        if (row == 4 && column == 4) return@afterPagerDistributeTiles
                    }
                    // 这个变量存储了所有的磁贴
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
                    // 计算每个页面上应该存在的磁贴
                    // 这里返回的列表不可能为空，没有磁贴的情况已经在上面考虑了
                    val newPages = calculateTilesLocation(records, column, row)
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
                    // 这里必须用插件的类加载器来加载这些安卓依赖库
                    ANDROIDX_VIEW_PAGER_CLS.wildClass(pluginClassLoader).accessFunction(
                        "setAdapter",
                        ANDROIDX_PAGER_ADAPTER_CLS.wildClass(pluginClassLoader)
                    ).invoke(_self, _self getField "adapter")
                }
            }
        }
    }

    /**
     * 计算每个页面包含的磁贴
     * */
    private fun calculateTilesLocation(
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
                // 把当前页的所有磁贴添加到返回结果的列表中
                result.add(ArrayList(currentPageSwitches))
                // 清除当前页磁贴的临时存储列表，准备进入新的一页
                currentPageSwitches.clear()
                currentPage++
            }
            // 将当前遍历到的磁贴添加到当前页开关的临时存储列表中
            currentPageSwitches.add(record)
        }
        if (currentPageSwitches.isNotEmpty()) {
            // 将最后剩下的未满一页的磁贴添加到最后一页
            result.add(currentPageSwitches)
        }
        return result
    }

    /**
     * 获取屏幕旋转角度，取值有2个
     * - 0 to 纵向
     * - 90 to 横向
     * */
    private fun requireRotation(): Int {
        if (HostGlobal.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 90
        }
        return 0
    }

}

private data class TilesPageSize(
    var row: Int = 4,
    var column: Int = 4
)