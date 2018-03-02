package com.time.cat.mvp.view.label_tag_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.time.cat.R;

/**
 * @author dlink
 * @date 2018/1/26
 * @discription
 * @usage

<com.time.cat.mvp.view.label_tag_view.SegmentedRadioGroup
    android:id="@+id/label_group"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dip"
    android:checkedButton="@+id/label_important_urgent"
    android:gravity="center_vertical"
    android:orientation="horizontal">

    <RadioButton
        android:id="@+id/label_important_urgent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:button="@null"
        android:gravity="center"
        android:minHeight="33dip"
        android:minWidth="40dip"
        android:text="@string/label_important_urgent"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:textColor="@color/label_important_urgent_color"
        android:textSize="10sp"/>

    <RadioButton
        android:id="@+id/label_important_not_urgent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:button="@null"
        android:gravity="center"
        android:minHeight="33dip"
        android:minWidth="40dip"
        android:text="@string/label_important_not_urgent"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:textColor="@color/label_important_not_urgent_color"
        android:textSize="10sp"/>

    <RadioButton
        android:id="@+id/label_not_important_urgent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:button="@null"
        android:gravity="center"
        android:minHeight="33dip"
        android:minWidth="40dip"
        android:text="@string/label_not_important_urgent"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:textColor="@color/label_not_important_urgent_color"
        android:textSize="10sp"/>

    <RadioButton
        android:id="@+id/label_not_important_not_urgent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:button="@null"
        android:gravity="center"
        android:minHeight="33dip"
        android:minWidth="40dip"
        android:text="@string/label_not_important_not_urgent"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:textColor="@color/label_not_important_not_urgent_color"
        android:textSize="10sp"/>
</com.time.cat.mvp.view.label_tag_view.SegmentedRadioGroup>
// 2.
    //-//<RadioGroup.OnCheckedChangeListener>------------------------------------------------------
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.label_important_urgent:
            case R.id.label_important_not_urgent:
            case R.id.label_not_important_urgent:
            case R.id.label_not_important_not_urgent:
        }
    }
    //-//</RadioGroup.OnCheckedChangeListener>------------------------------------------------------
 */
public class SegmentedRadioGroup extends RadioGroup {
        public SegmentedRadioGroup(Context context) {
        super(context);
    }

    public SegmentedRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        changeButtonsImages();
    }

    private void changeButtonsImages() {
        int count = super.getChildCount();

        if (count > 1) {
            super.getChildAt(0).setBackgroundResource(R.drawable.segment_radio_left);
            for (int i = 1; i < count - 1; i++) {
                super.getChildAt(i).setBackgroundResource(R.drawable.segment_radio_middle);
            }
            super.getChildAt(count - 1).setBackgroundResource(R.drawable.segment_radio_right);
        } else if (count == 1) {
            super.getChildAt(0).setBackgroundResource(R.drawable.segment_button);
        }
    }
}
