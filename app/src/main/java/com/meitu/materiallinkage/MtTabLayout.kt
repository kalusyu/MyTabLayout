package com.meitu.materiallinkage

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.meitu.materiallinkage.abs.AbsTabIndicator
import com.meitu.materiallinkage.abs.ITabAdapter

/**
 *
 *
 * @author ybw  2021.05.17
 *
 *
 **/
class MtTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var indicator: AbsTabIndicator? = null

    /**
     * 当前tab的位置，从0开始算
     */
    var currentItem: Int = 0
        private set

    var contentAdapter: ITabAdapter? = null
        set(value) {
            field = value
            init()
        }

    fun init() {
        removeAllViews()
        contentAdapter?.let { adapter ->
            val tabCount = adapter.getTabCount()
            if (tabCount <= 0) {
                indicator = null
                return@let
            }

            if (currentItem > tabCount - 1) {
                currentItem = tabCount - 1
            }

            // 添加Tab
            (0 until tabCount).forEach { position ->
                adapter.createTab(position)?.let {
                    if (position == currentItem) {
                        it.selectTab()
                    } else {
                        // TODO 使用unSelectTab
                        it.reset()
                    }
                    addView(it as View)
                }
            }

            // 添加indicator
            indicator = adapter.createIndicator()
            if (indicator != null) {
                setWillNotDraw(false)
            }

            if (tabCount == 0 && indicator == null) {
                throw  IllegalArgumentException("tab和indicator至少设置一个")
            }

            // TODO 名字改一下，布局发生变动需要重新确定indicator的位置
            indicator?.init()
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }


}