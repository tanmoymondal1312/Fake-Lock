package com.mediaghor.darkscreen.Dialog;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mediaghor.darkscreen.Activities.ThemeSettingsActivity;
import com.mediaghor.darkscreen.R;
import com.vimalcvs.switchdn.DayNightSwitch;
import com.vimalcvs.switchdn.DayNightSwitchListener;

public class ApSettingsDialog extends Dialog {
    ImageView dialogCloseBtn;
    LinearLayout themeSettingId;
    String callFrom;





    public ApSettingsDialog(Context context, String callFrom) {
        super(context);
        this.callFrom = callFrom;

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings_app);

        dialogCloseBtn = findViewById(R.id.close_dialog);
        themeSettingId = findViewById(R.id.id_theme_change);

        // Close button
        dialogCloseBtn.setOnClickListener(v -> dismiss());

        themeSettingId.postDelayed(() -> {
            themeSettingId.setOnClickListener(v -> {
                dismiss();
                Intent intent = new Intent(getContext(), ThemeSettingsActivity.class);
                getContext().startActivity(intent);
            });
        }, 200); // delay 200 ms

        if (callFrom.equals("THEME_SETTINGS")) {
            themeSettingId.setEnabled(false);

            themeSettingId.setForeground(new ColorDrawable(Color.parseColor("#80FFFFFF")));
        }





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
