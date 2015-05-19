package org.edx.mobile.view.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.third_party.iconify.IconDrawable;
import org.edx.mobile.third_party.iconify.Iconify;

public class CourseModesMenuPopup extends PopupWindow {
    public enum CourseMode {
        VIDEO_ONLY(R.id.videos_only, Iconify.IconValue.fa_film),
        FULL_COURSE(R.id.full_course, Iconify.IconValue.fa_list);

        private final int layoutId;
        private final Iconify.IconValue iconValue;
        CourseMode(@IdRes int layoutId, Iconify.IconValue iconValue) {
            this.layoutId = layoutId;
            this.iconValue = iconValue;
        }
    }

    public interface OnCourseModeSelectedListener {
        void onCourseModeClick(CourseMode courseMode);
    }

    private final OnCourseModeSelectedListener itemSelectedListener;

    public CourseModesMenuPopup(Activity activity,
            OnCourseModeSelectedListener itemSelectedListener) {
        this(activity, CourseMode.FULL_COURSE, itemSelectedListener);
    }

    public CourseModesMenuPopup(Activity activity, CourseMode selectedMode,
            OnCourseModeSelectedListener itemSelectedListener) {
        View contentView = LayoutInflater.from(activity).inflate(
                R.layout.popup_course_modes, (FrameLayout)
                        activity.findViewById(android.R.id.content), false);
        setContentView(contentView);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        setWidth(params.width);
        setHeight(params.height);
        setFocusable(true);
        // Setting a background is necessary for outside touches to register
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        for (CourseMode courseMode : CourseMode.values()) {
            TextView item = (TextView) contentView.findViewById(courseMode.layoutId);
            Drawable iconDrawable = new IconDrawable(activity, courseMode.iconValue).sizeDp(15);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                item.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
            } else {
                item.setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null);
            }
            if (courseMode == selectedMode) {
                item.setSelected(true);
            }
            item.setOnClickListener(new ItemClickListener(courseMode));
        }
        this.itemSelectedListener = itemSelectedListener;
    }

    public void show() {
        Activity activity = (Activity) getContentView().getContext();
        DisplayMetrics displayMetrics =
                activity.getResources().getDisplayMetrics();
        showAtLocation(activity.findViewById(android.R.id.content),
                Gravity.END | Gravity.TOP,
                Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics)),
                Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics)));
    }

    private final class ItemClickListener implements View.OnClickListener {
        private final CourseMode courseMode;

        ItemClickListener(CourseMode courseMode) {
            this.courseMode = courseMode;
        }

        @Override
        public void onClick(View v) {
            View view = getContentView();
            for (CourseMode courseMode : CourseMode.values()) {
                View item = view.findViewById(courseMode.layoutId);
                item.setSelected(item == v);
            }
            itemSelectedListener.onCourseModeClick(courseMode);
            dismiss();
        }
    }
}