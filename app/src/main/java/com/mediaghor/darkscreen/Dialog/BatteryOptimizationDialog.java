package com.mediaghor.darkscreen.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mediaghor.darkscreen.R;

public class BatteryOptimizationDialog extends Dialog {

    public interface DialogButtonClickListener {
        void onOkClicked();
        void onCancelClicked();
    }

    private DialogButtonClickListener listener;

    public BatteryOptimizationDialog(Context context, DialogButtonClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.custom_battery_permission_dialog);

        // Initialize buttons
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnOk = findViewById(R.id.btn_ok);

        // Set click listeners
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancelClicked();
                }
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOkClicked();
                }
                dismiss();
            }
        });
    }
}