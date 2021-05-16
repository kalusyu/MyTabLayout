package com.kalus.mytablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;

import static com.google.android.material.animation.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR;

/**
 * desc:
 *
 * @author biaowen.yu
 * @date 2021/5/15 16:21
 **/
public class TabLayout extends HorizontalScrollView {

    private ArrayList<Tab> tabs = new ArrayList<>();
    final SlidingTabIndicator slidingTabIndicator;
    private Tab selectedTab;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        slidingTabIndicator = new SlidingTabIndicator(context);
        super.addView(
                slidingTabIndicator,
                0,
                new HorizontalScrollView.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    public Tab newTab() {
        Tab tab = new Tab();
        tab.parent = this;
        tab.view = createTabView(tab);
        return tab;
    }

    private TabView createTabView(Tab tab) {
        TabView tabView = new TabView(getContext());
        tabView.setFocusable(true);
        tabView.setTab(tab);
        return tabView;
    }

    public void addTab(Tab tab) {
        addTab(tab, tabs.isEmpty());
    }

    private void addTab(Tab tab, boolean setSelected) {
        addTab(tab, tabs.size(), setSelected);
    }

    private void addTab(Tab tab, int position, boolean setSelected) {
        configureTab(tab, position);
        addTabView(tab);

        if (setSelected) {
            tab.select();
        }
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        tabs.add(position, tab);
    }

    private void addTabView(Tab tab) {
        final TabView tabView = tab.view;
        tabView.setSelected(false);
        tabView.setActivated(false);
        slidingTabIndicator.addView(tabView, tab.getPosition(), createLayoutParamsForTabs());
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        final LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        lp.weight = 0;
        return lp;
    }

    /**
     * Selects the given tab.
     *
     * @param tab The tab to select, or {@code null} to select none.
     */
    public void selectTab(@Nullable Tab tab) {
        selectTab(tab, true);
    }

    /**
     * Selects the given tab. Will always animate to the selected tab if the current tab is
     * reselected, regardless of the value of {@code updateIndicator}.
     *
     * @param tab             The tab to select, or {@code null} to select none.
     * @param updateIndicator Whether to animate to the selected tab.
     */
    public void selectTab(@Nullable final Tab tab, boolean updateIndicator) {
        final Tab currentTab = selectedTab;

        if (currentTab == tab) {
            if (currentTab != null) {
//                dispatchTabReselected(tab);
                animateToTab(tab.getPosition());
            }
        } else {
            final int newPosition = tab != null ? tab.getPosition() : Tab.INVALID_POSITION;
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == Tab.INVALID_POSITION)
                        && newPosition != Tab.INVALID_POSITION) {
                    // If we don't currently have a tab, just draw the indicator
                    setScrollPosition(newPosition, 0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != Tab.INVALID_POSITION) {
                    setSelectedTabView(newPosition);
                }
            }
            // Setting selectedTab before dispatching 'tab unselected' events, so that currentTab's state
            // will be interpreted as unselected
            selectedTab = tab;
//            if (currentTab != null) {
//                dispatchTabUnselected(currentTab);
//            }
//            if (tab != null) {
//                dispatchTabSelected(tab);
//            }
        }
    }

    public int getSelectedTabPosition() {
        return selectedTab != null ? selectedTab.getPosition() : -1;
    }

    private void animateToTab(int newPosition) {
        if (newPosition == Tab.INVALID_POSITION) {
            return;
        }

        if (getWindowToken() == null
                || !ViewCompat.isLaidOut(this)
                || slidingTabIndicator.childrenNeedLayout()) {
            // If we don't have a window token, or we haven't been laid out yet just draw the new
            // position now
            setScrollPosition(newPosition, 0f, true);
            return;
        }

        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(newPosition, 0);

        if (startScrollX != targetScrollX) {
            ensureScrollAnimator();

            scrollAnimator.setIntValues(startScrollX, targetScrollX);
            scrollAnimator.start();
        }

        // Now animate the indicator
        slidingTabIndicator.animateIndicatorToPosition(newPosition, 350);
    }

    private ValueAnimator scrollAnimator;

    private void ensureScrollAnimator() {
        if (scrollAnimator == null) {
            scrollAnimator = new ValueAnimator();
            scrollAnimator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            scrollAnimator.setDuration(350);
            scrollAnimator.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(@NonNull ValueAnimator animator) {
                            scrollTo((int) animator.getAnimatedValue(), 0);
                        }
                    });
        }
    }


    private int calculateScrollXForTab(int position, float positionOffset) {
        final View selectedChild = slidingTabIndicator.getChildAt(position);
        final View nextChild =
                position + 1 < slidingTabIndicator.getChildCount()
                        ? slidingTabIndicator.getChildAt(position + 1)
                        : null;
        final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
        final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

        // base scroll amount: places center of tab in center of parent
        int scrollBase = selectedChild.getLeft() + (selectedWidth / 2) - (getWidth() / 2);
        // offset amount: fraction of the distance between centers of tabs
        int scrollOffset = (int) ((selectedWidth + nextWidth) * 0.5f * positionOffset);

        return (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR)
                ? scrollBase + scrollOffset
                : scrollBase - scrollOffset;
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    /**
     * Set the scroll position of the tabs. This is useful for when the tabs are being displayed as
     * part of a scrolling container such as {@link androidx.viewpager.widget.ViewPager}.
     *
     * <p>Calling this method does not update the selected tab, it is only used for drawing purposes.
     *
     * @param position                current scroll position
     * @param positionOffset          Value from [0, 1) indicating the offset from {@code position}.
     * @param updateSelectedText      Whether to update the text's selected state.
     * @param updateIndicatorPosition Whether to set the indicator to the given position and offset.
     * @see #setScrollPosition(int, float, boolean)
     */
    public void setScrollPosition(
            int position,
            float positionOffset,
            boolean updateSelectedText,
            boolean updateIndicatorPosition) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= slidingTabIndicator.getChildCount()) {
            return;
        }

        // Set the indicator position, if enabled
        if (updateIndicatorPosition) {
            slidingTabIndicator.setIndicatorPositionFromTabPosition(position, positionOffset);
        }

        // Now update the scroll position, canceling any running animation
        if (scrollAnimator != null && scrollAnimator.isRunning()) {
            scrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        // Update the 'selected state' view as we scroll, if enabled
        if (updateSelectedText) {
            setSelectedTabView(roundedPosition);
        }
    }

    private void setSelectedTabView(int position) {
        final int tabCount = slidingTabIndicator.getChildCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {
                final View child = slidingTabIndicator.getChildAt(i);
                child.setSelected(i == position);
                child.setActivated(i == position);
            }
        }
    }


}
