package com.mediaghor.fakelock.Helper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.mediaghor.fakelock.R;
import com.mediaghor.fakelock.adapter.SliderAdapter;

public class ButtonManager {

    /**
     * Apply or revert the applied state inside a ViewHolder's FrameLayout
     *
     * @param holder  The ViewHolder containing frameLayoutContainer + applyButton
     * @param state   true -> replace Apply button with checked icon
     *                false -> revert back to Apply button
     */
    public static void ApplyAppliedStateController(SliderAdapter.SliderViewHolder holder, boolean state) {
        FrameLayout frameLayout = holder.itemView.findViewById(R.id.frameLayoutContainer);
        MaterialButton applyButton = holder.applyButton;
        int appliedLayoutId = R.layout.checked_button; // Your checked button layout

        if (frameLayout == null || applyButton == null) return;

        if (state) {
            // Hide the Apply button
            applyButton.setVisibility(View.GONE);

            // Check if checked button is already added
            if (frameLayout.findViewById(R.id.checkedBtn) == null) {
                // Inflate the checked_button layout
                View checkedButton = LayoutInflater.from(holder.itemView.getContext())
                        .inflate(appliedLayoutId, frameLayout, false);

                // Optionally, set an ID if not set in XML
                checkedButton.setId(R.id.checkedBtn);

                // Add it to the FrameLayout
                frameLayout.addView(checkedButton);
            }
        } else {
            // Remove checked ImageButton if exists
            View checkedBtn = frameLayout.findViewById(R.id.checkedBtn);
            if (checkedBtn != null) {
                frameLayout.removeView(checkedBtn);
            }

            // Show Apply button again
            applyButton.setVisibility(View.VISIBLE);
        }
    }

}
