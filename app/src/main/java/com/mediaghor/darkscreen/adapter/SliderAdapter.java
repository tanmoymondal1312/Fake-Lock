package com.mediaghor.darkscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mediaghor.darkscreen.Helper.ButtonManager;
import com.mediaghor.darkscreen.Helper.SaveMyLayout;
import com.mediaghor.darkscreen.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<int[]> imageList;
    private Context context;
    private int selectedPosition = -1; // Track selected item
    ButtonManager buttonManager;

    public SliderAdapter(List<int[]> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;

        // Initialize selected position from saved layout
        int layoutSelected = SaveMyLayout.getSavedLayoutId(context);
        selectedPosition = getPositionUsingId(layoutSelected);

        buttonManager = new ButtonManager();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int[] item = imageList.get(position);

        holder.imageView.setImageResource(item[0]);

        // Set button state based on selectedPosition
        if (position == selectedPosition) {
            ButtonManager.ApplyAppliedStateController(holder, true);
        } else {
            ButtonManager.ApplyAppliedStateController(holder, false);
        }

        // Apply button click
        holder.applyButton.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            SaveMyLayout.saveSelectedLayoutId(v.getContext(), item[1]);

            // Refresh only old and new applied buttons
            if (oldPosition != -1) notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MaterialButton applyButton;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            applyButton = itemView.findViewById(R.id.applyButton);
        }
    }

    // Helper: get adapter position using saved layout ID
    public int getPositionUsingId(int id) {
        for (int i = 0; i < imageList.size(); i++) {
            int[] item = imageList.get(i);
            if (item[1] == id) {
                return i; // found
            }
        }
        return -1; // not found
    }

    // Set Apply button appearance

}
