package com.mediaghor.fakelock.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mediaghor.fakelock.R;
import com.vimalcvs.switchdn.DayNightSwitch;
import com.vimalcvs.switchdn.DayNightSwitchListener;

public class ApSettingsDialog extends Dialog {
    ImageView dialogCloseBtn;
    DayNightSwitch dayNightSwitch;

    // Callback interface
    public interface OnThemeSwitchListener {
        void onThemeSwitched(boolean isNight);
    }

    private OnThemeSwitchListener listener;

    public ApSettingsDialog(Context context, OnThemeSwitchListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings_app);

        dialogCloseBtn = findViewById(R.id.close_dialog);
        dayNightSwitch = findViewById(R.id.dayNightSwitchBtn);

        // Close button
        dialogCloseBtn.setOnClickListener(v -> dismiss());

        // Switch listener
        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {
                if (listener != null) {
                    listener.onThemeSwitched(is_night); // notify activity/fragment
                }

                dismiss(); // optional: close dialog after switching
            }
        });

        // Dialog window customization
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.6f;
            window.setAttributes(params);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }
}
