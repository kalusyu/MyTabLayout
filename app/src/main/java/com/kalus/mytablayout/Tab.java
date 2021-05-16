package com.kalus.mytablayout;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

/**
 * desc:
 *
 * @author biaowen.yu
 * @date 2021/5/15 16:21
 **/
public class Tab {

    /**
     * An invalid position for a tab.
     *
     * @see #getPosition()
     */
    public static final int INVALID_POSITION = -1;


    @Nullable private CharSequence text;

    private int position = INVALID_POSITION;

    @Nullable public TabLayout parent;
    @NonNull
    public TabView view;

    // TODO(b/76413401): make package private constructor after the widget migration is finished
    public Tab() {
        // Private constructor
    }




    /**
     * Return the current position of this tab in the action bar.
     *
     * @return Current position, or {@link #INVALID_POSITION} if this tab is not currently in the
     *     action bar.
     */
    public int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
    }

    /**
     * Return the text of this tab.
     *
     * @return The tab's text
     */
    @Nullable
    public CharSequence getText() {
        return text;
    }


    /**
     * Set the text displayed on this tab. Text may be truncated if there is not room to display the
     * entire string.
     *
     * @param text The text to display
     * @return The current instance for call chaining
     */
    @NonNull
    public Tab setText(@Nullable CharSequence text) {

        this.text = text;
        updateView();
        return this;
    }

    /**
     * Set the text displayed on this tab. Text may be truncated if there is not room to display the
     * entire string.
     *
     * @param resId A resource ID referring to the text that should be displayed
     * @return The current instance for call chaining
     */
    @NonNull
    public Tab setText(@StringRes int resId) {
        if (parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }
        return setText(parent.getResources().getText(resId));
    }


    /** Select this tab. Only valid if the tab has been added to the action bar. */
    public void select() {
        if (parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }
        parent.selectTab(this);
    }

    /** Returns true if this tab is currently selected. */
    public boolean isSelected() {
        if (parent == null) {
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }
        return parent.getSelectedTabPosition() == position;
    }

    void updateView() {
        if (view != null) {
            view.update();
        }
    }
}