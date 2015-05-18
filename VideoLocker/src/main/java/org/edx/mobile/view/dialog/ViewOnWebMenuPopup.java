package org.edx.mobile.view.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import org.edx.mobile.R;

public class ViewOnWebMenuPopup extends PopupWindow {
    public interface OnViewOnWebClickListener {
        void OnViewOnWebClick();
    }

    private final OnViewOnWebClickListener clickListener;

    public ViewOnWebMenuPopup(Activity activity,
            final OnViewOnWebClickListener clickListener) {
        View contentView = LayoutInflater.from(activity).inflate(
                R.layout.popup_view_on_web, (FrameLayout)
                        activity.findViewById(android.R.id.content), false);
        setContentView(contentView);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        setWidth(params.width);
        setHeight(params.height);
        setFocusable(true);
        setBackgroundDrawable(contentView.getContext().getResources()
                .getDrawable(R.drawable.white_rounded_bordered_bg));
        this.clickListener = clickListener;
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnViewOnWebClick();
                dismiss();
            }
        });
    }

    public void show() {
        Activity activity = (Activity) getContentView().getContext();
        DisplayMetrics displayMetrics =
                activity.getResources().getDisplayMetrics();
        showAtLocation(activity.findViewById(android.R.id.content),
                Gravity.END | Gravity.TOP,
                Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 9, displayMetrics)),
                Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics)));
    }
}