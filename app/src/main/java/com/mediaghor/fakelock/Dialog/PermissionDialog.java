package com.mediaghor.fakelock.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.mediaghor.fakelock.Activities.MainActivity;
import com.mediaghor.fakelock.R;

public class PermissionDialog extends Dialog {

    private final Activity activity;
    private final PermissionDialogListener listener;
    Button allowNotificationBtn,allowDisplayBtn;



    public interface PermissionDialogListener {
        void onNotificationAllowed();
        void onDisplayAllowed();
        void onDialogClosed();
    }

    public PermissionDialog(Activity activity, PermissionDialogListener listener) {
        super(activity);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_simple_close);

        // Set dialog width to match parent
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Don't close on outside touch
        setCanceledOnTouchOutside(false);



        // Views
        ImageButton closeBtn = findViewById(R.id.btn_close);
        allowNotificationBtn = findViewById(R.id.allowdisButton);
        allowDisplayBtn = findViewById(R.id.allowButton);

        // Listeners
        closeBtn.setOnClickListener(v -> {
            listener.onDialogClosed();
            dismiss();
        });

        allowNotificationBtn.setOnClickListener(v -> listener.onNotificationAllowed());
        allowDisplayBtn.setOnClickListener(v -> listener.onDisplayAllowed());
    }


    @Override
    public void dismiss() {
        // Reset activity background when dialog dismissed
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(activity, android.R.color.white))); // or your default background
        super.dismiss();
    }

    public void handleButtonBehaviour(String state) {
        if (state.equals("notification_allowed")) {
            allowNotificationBtn.setText("Allowed ✅");
            allowNotificationBtn.setEnabled(false);
            allowNotificationBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.un_clickable_green));

            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showToast("Notification permission granted!");
            }

        } else if (state.equals("display_allowed")) {
            allowDisplayBtn.setText("Allowed ✅");
            allowDisplayBtn.setEnabled(false);
            allowDisplayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.un_clickable_green));

            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showToast("Display over apps permission granted!");
            }
        }
    }

}
