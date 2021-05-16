package com.meitu.materiallinkage.abs

/**
 * tab和indicator适配接口，对外扩展可变动的tab和indicator
 *
 * @author ybw  2021.05.17
 *
 *
 **/
interface ITabAdapter {

    /**
     * 创建对应位置的Tab
     *
     * @param position 当前第几个tab
     *
     * @return 为null则没有tab
     */
    fun createTab(position: Int): AbsTab?

    /**
     * 创建Indicator
     *
     * @return 为null则没有indicator
     */
    fun createIndicator(): AbsTabIndicator?

    /**
     * Tab数量
     *
     * @return 返回0或-1 则没有tab
     */
    fun getTabCount(): Int

}