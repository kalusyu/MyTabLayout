package com.kalus.mytablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import static com.google.android.material.animation.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR;

/**
 * desc:
 *
 * @author biaowen.yu
 * @date 2021/5/15 16:22
 **/
class SlidingTabIndicator extends LinearLayout {
    ValueAnimator indicatorAnimator;
    int selectedPosition = -1;
    // selectionOffset is only used when a tab is being slid due to a viewpager swipe.
    // selectionOffset is always the offset to the right of selectedPosition.
    float selectionOffset;

    private int layoutDirection = -1;
    private int mScreenWidth = 0;

    SlidingTabIndicator(Context context) {
        super(context);
        setWillNotDraw(false);
        mScreenWidth = getScreenWidth(context);
    }


    boolean childrenNeedLayout() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            if (child.getWidth() <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the indicator position based on an offset between two adjacent tabs.
     *
     * @param position       The position from which the offset should be calculated.
     * @param positionOffset The offset to the right of position where the indicator should be
     *                       drawn. This must be a value between 0.0 and 1.0.
     */
    void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
            indicatorAnimator.cancel();
        }

        selectedPosition = position;
        selectionOffset = positionOffset;

        final View selectedTitle = getChildAt(selectedPosition);
        final View nextTitle = getChildAt(selectedPosition + 1);

        tweenIndicatorPosition(selectedTitle, nextTitle, selectionOffset);
    }

    float getIndicatorPosition() {
        return selectedPosition + selectionOffset;
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);

        // Workaround for a bug before Android M where LinearLayout did not re-layout itself when
        // layout direction changed
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (this.layoutDirection != layoutDirection) {
                requestLayout();
                this.layoutDirection = layoutDirection;
            }
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            // HorizontalScrollView will first measure use with UNSPECIFIED, and then with
            // EXACTLY. Ignore the first call since anything we do will be overwritten anyway
            return;
        }
    }


    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    boolean layouted = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View lastVisibleChild = null;
        int lastIndex = 0;
        if (!layouted) {
            int totalWidth = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                totalWidth += child.getMeasuredWidth();
                if (totalWidth > mScreenWidth) {
                    lastVisibleChild = child;
                    lastIndex = i;
                    break;

                }
            }
            if (totalWidth > mScreenWidth) {
            int lastVisibleChildWidth = lastVisibleChild.getMeasuredWidth() / 2;
            int halfLastWidth = lastVisibleChildWidth;
                int restWidth = totalWidth - mScreenWidth;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (restWidth < halfLastWidth) {
                        int total = halfLastWidth - restWidth;
                        int offset = total / (lastIndex + 1) / 2;
                        child.setPadding(child.getPaddingStart() + offset, 0, child.getPaddingEnd() + offset, 0);
                    } else if (restWidth > halfLastWidth) {
                        int total = restWidth - halfLastWidth;
                        int offset = total / (lastIndex + 1) / 2;
                        child.setPadding(child.getPaddingStart() - offset, 0, child.getPaddingEnd() - offset, 0);
                    }
                }
            } else if (totalWidth < mScreenWidth) {
                int totalOffset = mScreenWidth - totalWidth;
                int offset = totalOffset / getChildCount() / 2;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.setPadding(child.getPaddingStart() + offset, 0, child.getPaddingEnd() + offset, 0);
                }
            }
            layouted = true;
        }


        if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
            // It's possible that the tabs' layout is modified while the indicator is animating (ex. a
            // new tab is added, or a tab is removed in onTabSelected). This would change the target end
            // position of the indicator, since the tab widths are different. We need to modify the
            // animation's updateListener to pick up the new target positions.
            updateOrRecreateIndicatorAnimation(
                    /* recreateAnimation= */ false, selectedPosition, /* duration= */ -1);
        } else {
            // If we've been laid out, update the indicator position
            jumpIndicatorToSelectedPosition();
        }
    }

    /**
     * Immediately update the indicator position to the currently selected position.
     */
    private void jumpIndicatorToSelectedPosition() {
        /*final View currentView = getChildAt(selectedPosition);
        tabIndicatorInterpolator.setIndicatorBoundsForTab(
                com.google.android.material.tabs.TabLayout.this, currentView, tabSelectedIndicator);*/
    }

    /**
     * Update the position of the indicator by tweening between the currently selected tab and the
     * destination tab.
     *
     * <p>This method is called for each frame when either animating the indicator between
     * destinations or driving an animation through gesture, such as with a viewpager.
     *
     * @param startTitle The tab which should be selected (as marked by the indicator), when
     *                   fraction is 0.0.
     * @param endTitle   The tab which should be selected (as marked by the indicator), when fraction
     *                   is 1.0.
     * @param fraction   A value between 0.0 and 1.0 that indicates how far between currentTitle and
     *                   endTitle the indicator should be drawn. e.g. If a viewpager attached to this TabLayout is
     *                   currently half way slid between page 0 and page 1, fraction will be 0.5.
     */
    private void tweenIndicatorPosition(View startTitle, View endTitle, float fraction) {
        /*boolean hasVisibleTitle = startTitle != null && startTitle.getWidth() > 0;
        if (hasVisibleTitle) {
            tabIndicatorInterpolator.setIndicatorBoundsForOffset(
                    TabLayout.this, startTitle, endTitle, fraction, tabSelectedIndicator);
        } else {
            // Hide the indicator by setting the drawable's width to 0 and off screen.
            tabSelectedIndicator.setBounds(
                    -1, tabSelectedIndicator.getBounds().top, -1, tabSelectedIndicator.getBounds().bottom);
        }*/

        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Animate the position of the indicator from its current position to a new position.
     *
     * <p>This is typically used when a tab destination is tapped. If the indicator should be moved
     * as a result of a gesture, see {@link #setIndicatorPositionFromTabPosition(int, float)}.
     *
     * @param position The new position to animate the indicator to.
     * @param duration The duration over which the animation should take place.
     */
    void animateIndicatorToPosition(final int position, int duration) {
        if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
            indicatorAnimator.cancel();
        }

        updateOrRecreateIndicatorAnimation(/* recreateAnimation= */ true, position, duration);
    }

    /**
     * Animate the position of the indicator from its current position to a new position.
     *
     * @param recreateAnimation Whether a currently running animator should be re-targeted to move
     *                          the indicator to it's new position.
     * @param position          The new position to animate the indicator to.
     * @param duration          The duration over which the animation should take place.
     */
    private void updateOrRecreateIndicatorAnimation(
            boolean recreateAnimation, final int position, int duration) {
        final View currentView = getChildAt(selectedPosition);
        final View targetView = getChildAt(position);
        if (targetView == null) {
            // If we don't have a view, just update the position now and return
            jumpIndicatorToSelectedPosition();
            return;
        }

        // Create the update listener with the new target indicator positions. If we're not recreating
        // then animationStartLeft/Right will be the same as when the previous animator was created.
        ValueAnimator.AnimatorUpdateListener updateListener =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                        tweenIndicatorPosition(currentView, targetView, valueAnimator.getAnimatedFraction());
                    }
                };

        if (recreateAnimation) {
            // Create & start a new indicatorAnimator.
            ValueAnimator animator = indicatorAnimator = new ValueAnimator();
            animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            animator.setDuration(duration);
            animator.setFloatValues(0F, 1F);
            animator.addUpdateListener(updateListener);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            selectedPosition = position;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            selectedPosition = position;
                        }
                    });
            animator.start();
        } else {
            // Reuse the existing animator. Updating the listener only modifies the target positions.
            indicatorAnimator.removeAllUpdateListeners();
            indicatorAnimator.addUpdateListener(updateListener);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        /*int indicatorHeight = tabSelectedIndicator.getBounds().height();
        if (indicatorHeight < 0) {
            indicatorHeight = tabSelectedIndicator.getIntrinsicHeight();
        }

        int indicatorTop = 0;
        int indicatorBottom = 0;

        switch (tabIndicatorGravity) {
            case INDICATOR_GRAVITY_BOTTOM:
                indicatorTop = getHeight() - indicatorHeight;
                indicatorBottom = getHeight();
                break;
            case INDICATOR_GRAVITY_CENTER:
                indicatorTop = (getHeight() - indicatorHeight) / 2;
                indicatorBottom = (getHeight() + indicatorHeight) / 2;
                break;
            case INDICATOR_GRAVITY_TOP:
                indicatorTop = 0;
                indicatorBottom = indicatorHeight;
                break;
            case INDICATOR_GRAVITY_STRETCH:
                indicatorTop = 0;
                indicatorBottom = getHeight();
                break;
            default:
                break;
        }

        // Ensure the drawable actually has a width and is worth drawing
        if (tabSelectedIndicator.getBounds().width() > 0) {
            // Use the left and right bounds of the drawable, as set by the indicator interpolator.
            // Update the top and bottom to respect the indicator gravity property.
            Rect indicatorBounds = tabSelectedIndicator.getBounds();
            tabSelectedIndicator.setBounds(
                    indicatorBounds.left, indicatorTop, indicatorBounds.right, indicatorBottom);
            Drawable indicator = tabSelectedIndicator;

            // If a tint color has been specified using TabLayout's setSelectedTabIndicatorColor, wrap
            // the drawable and tint it as specified.
            if (tabSelectedIndicatorColor != Color.TRANSPARENT) {
                indicator = DrawableCompat.wrap(indicator);
                if (VERSION.SDK_INT == VERSION_CODES.LOLLIPOP) {
                    indicator.setColorFilter(tabSelectedIndicatorColor, PorterDuff.Mode.SRC_IN);
                } else {
                    DrawableCompat.setTint(indicator, tabSelectedIndicatorColor);
                }
            }

            indicator.draw(canvas);
        }*/

        // Draw the tab item contents (icon and label) on top of the background + indicator layers
        super.draw(canvas);
    }
}
