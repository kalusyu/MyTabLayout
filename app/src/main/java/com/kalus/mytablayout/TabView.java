package com.kalus.mytablayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

/**
 * desc:
 *
 * @author biaowen.yu
 * @date 2021/5/15 16:21
 **/
public final class TabView extends LinearLayout {
    private Tab tab;
    private TextView textView;
    private int defaultMaxLines = 2;

    public TabView(@NonNull Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setClickable(true);
        ViewCompat.setPointerIcon(
                this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
    }


    @Override
    public boolean performClick() {
        final boolean handled = super.performClick();

        if (tab != null) {
            if (!handled) {
                playSoundEffect(SoundEffectConstants.CLICK);
            }
            tab.select();
            return true;
        } else {
            return handled;
        }
    }

    @Override
    public void setSelected(final boolean selected) {
        super.setSelected(selected);

        // Always dispatch this to the child views, regardless of whether the value has
        // changed
        if (textView != null) {
            textView.setSelected(selected);
        }

    }


    @Override
    public void onMeasure(final int origWidthMeasureSpec, final int origHeightMeasureSpec) {
        super.onMeasure(origWidthMeasureSpec, origHeightMeasureSpec);
        /*final int specWidthSize = MeasureSpec.getSize(origWidthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(origWidthMeasureSpec);
        final int maxWidth = getTabMaxWidth();

        final int widthMeasureSpec;
        final int heightMeasureSpec = origHeightMeasureSpec;

        if (maxWidth > 0 && (specWidthMode == MeasureSpec.UNSPECIFIED || specWidthSize > maxWidth)) {
            // If we have a max width and a given spec which is either unspecified or
            // larger than the max width, update the width spec using the same mode
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(tabMaxWidth, MeasureSpec.AT_MOST);
        } else {
            // Else, use the original width spec
            widthMeasureSpec = origWidthMeasureSpec;
        }

        // Now lets measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We need to switch the text size based on whether the text is spanning 2 lines or not
        if (textView != null) {
            float textSize = tabTextSize;
            int maxLines = defaultMaxLines;

            if (iconView != null && iconView.getVisibility() == VISIBLE) {
                // If the icon view is being displayed, we limit the text to 1 line
                maxLines = 1;
            } else if (textView != null && textView.getLineCount() > 1) {
                // Otherwise when we have text which wraps we reduce the text size
                textSize = tabTextMultiLineSize;
            }

            final float curTextSize = textView.getTextSize();
            final int curLineCount = textView.getLineCount();
            final int curMaxLines = TextViewCompat.getMaxLines(textView);

            if (textSize != curTextSize || (curMaxLines >= 0 && maxLines != curMaxLines)) {
                // We've got a new text size and/or max lines...
                boolean updateTextView = true;

                if (mode == MODE_FIXED && textSize > curTextSize && curLineCount == 1) {
                    // If we're in fixed mode, going up in text size and currently have 1 line
                    // then it's very easy to get into an infinite recursion.
                    // To combat that we check to see if the change in text size
                    // will cause a line count change. If so, abort the size change and stick
                    // to the smaller size.
                    final Layout layout = textView.getLayout();
                    if (layout == null
                            || approximateLineWidth(layout, 0, textSize)
                            > getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) {
                        updateTextView = false;
                    }
                }

                if (updateTextView) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    textView.setMaxLines(maxLines);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        }*/
    }

    void setTab(@Nullable final Tab tab) {
        if (tab != this.tab) {
            this.tab = tab;
            update();
        }
    }

    void reset() {
        setTab(null);
        setSelected(false);
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }

    final void update() {
        final Tab tab = this.tab;
        if (this.textView == null) {
            inflateAndAddDefaultTextView();
            defaultMaxLines = TextViewCompat.getMaxLines(this.textView);
        }
        this.textView.setTextColor(createColorStateList(Color.RED, Color.BLUE));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        textView.setText(tab.getText());
        // Finally update our selected state
        setSelected(tab != null && tab.isSelected());
    }


    private void inflateAndAddDefaultTextView() {
        ViewGroup textViewParent = this;
        this.textView =
                (TextView)
                        LayoutInflater.from(getContext())
                                .inflate(R.layout.design_layout_tab_text, textViewParent, false);
        ViewCompat.setPaddingRelative(this, 30, 0, 30, 0);
        textViewParent.addView(textView);
    }

    @Nullable
    public Tab getTab() {
        return tab;
    }

}
