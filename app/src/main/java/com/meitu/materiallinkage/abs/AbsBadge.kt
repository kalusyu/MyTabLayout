package com.meitu.materiallinkage.abs

import android.graphics.Canvas

/**
 * 用户可以自定义绘制一些标记
 *
 * @author ybw  2021.05.17
 *
 *
 **/
abstract class AbsBadge(val tab: AbsTab) {
    private var show: Boolean = true

    /**
     * 隐藏标记
     */
    open fun hide() {
        if (!show) return
        show = false
    }

    /**
     * 显示标记
     */
    open fun show() {
        show = true
        tab.invalidate()
    }

    /**
     * 绘制标记内容
     *
     * @param canvas 画布
     */
    abstract fun draw(canvas: Canvas)
}
