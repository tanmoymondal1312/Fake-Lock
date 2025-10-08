package com.mediaghor.darkscreen.Dialog;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mediaghor.darkscreen.R;

public class FullScreenDialogBatteryOptInst extends DialogFragment {

    private OnDialogResultListener listener;

    public interface OnDialogResultListener {
        void onPositiveResult();
        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the fixed theme that's available in your project
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_MyApp_MaterialFullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fullscreen_batter_opt_inst, container, false);

        // Make sure the dialog is not canceled when touching outside
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find buttons in your layout and set click listeners
        // Adjust these IDs based on your actual layout
//        Button positiveButton = view.findViewById(R.id.btn_positive);
//        Button negativeButton = view.findViewById(R.id.btn_negative);
//
//        if (positiveButton != null) {
//            positiveButton.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onPositiveResult();
//                }
//                dismiss();
//            });
//        }
//
//        if (negativeButton != null) {
//            negativeButton.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onNegativeResult();
//                }
//                dismiss();
//            });
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        if (window != null) {
            // Set dialog dimensions to full screen
            //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            // Clear any default dialog decorations
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Remove dialog dim
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            // Make status bar transparent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Set status bar to transparent
                window.setStatusBarColor(Color.TRANSPARENT);

                // Add flags to draw behind status bar
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // For API 23+ (Marshmallow), set light status bar icons if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int flags = window.getDecorView().getSystemUiVisibility();
                    // Add or remove light status bar flag based on your app's theme
                    // Use this for dark background: flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    // Use this for light background: flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);
                }
            }
        }
    }
}