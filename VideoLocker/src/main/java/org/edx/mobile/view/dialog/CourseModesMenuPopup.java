package org.edx.mobile.view.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import org.edx.mobile.R;

public class CourseModesMenuPopup extends PopupWindow {
    public enum CourseMode {
        VIDEO_ONLY(R.id.videos_only),
        FULL_COURSE(R.id.full_course);

        private final int layoutId;
        CourseMode(@IdRes int layoutId) {
            this.layoutId = layoutId;
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
            View item = contentView.findViewById(courseMode.layoutId);
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
    };
}