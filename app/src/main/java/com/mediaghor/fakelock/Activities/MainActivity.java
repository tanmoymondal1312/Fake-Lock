package com.mediaghor.fakelock.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.kyleduo.switchbutton.SwitchButton;
import com.mediaghor.fakelock.R;
import com.mediaghor.fakelock.adapter.SliderAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private SwitchButton fancySwitchForDisplayIcon, fancySwitchForDisplayIcon2;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ScreenOff);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // In your MainActivity's onCreate:
        viewPager2 = findViewById(R.id.viewPager);

// Calculate dimensions for showing 3 items (1 full + 2 partial)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = (int) (screenWidth * 0.60f); // Each item takes 75% of screen width
        int peekWidth = (screenWidth - itemWidth) / 2; // Space for partial items

// Set padding to show partial items on both sides
        viewPager2.setPadding(peekWidth, 0, peekWidth, 0);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2); // Keep 2 pages in memory on each side

        List<Integer> imageList = Arrays.asList(
                R.drawable.lock_sample1,
                R.drawable.lock_sample1,
                R.drawable.lock_sample1,
                R.drawable.lock_sample1
        );

        SliderAdapter adapter = new SliderAdapter(imageList);
        viewPager2.setAdapter(adapter);

// Create composite transformer for combined effects
        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(8)); // Small margin between items
        compositeTransformer.addTransformer(new ViewPager2.PageTransformer() {
            private static final float MIN_SCALE = 0.8f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                if (Math.abs(position) > 1) {
                    page.setAlpha(0f);
                    return;
                }

                // Scale effect
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position) / 2);
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Alpha effect
                float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
                page.setAlpha(alphaFactor);

                // Translation effect to bring items closer
                page.setTranslationX(-position * peekWidth / 2);
            }
        });

        viewPager2.setPageTransformer(compositeTransformer);


    }



    private void initializeViews() {
        fancySwitchForDisplayIcon = findViewById(R.id.fancySwitchForDisplayIcon);
        fancySwitchForDisplayIcon2 = findViewById(R.id.fancySwitchForDisplayIcon2);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));
        }

        ColorStateList backColorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },     // Checked
                        new int[] { -android.R.attr.state_checked }     // Unchecked
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.switch_on),   // Checked color
                        ContextCompat.getColor(this, R.color.switch_off)   // Unchecked color
                }
        );
        fancySwitchForDisplayIcon.setBackColor(backColorStateList);
        fancySwitchForDisplayIcon2.setBackColor(backColorStateList);

        ColorStateList thumbColorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },  // Checked (ON state)
                        new int[] { -android.R.attr.state_checked }   // Unchecked (OFF state)
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.thumb_on_color),  // Color when ON
                        ContextCompat.getColor(this, R.color.thumb_off_color)   // Color when OFF

                }
        );
        fancySwitchForDisplayIcon.setThumbColor(thumbColorStateList);
        fancySwitchForDisplayIcon2.setThumbColor(thumbColorStateList);


    }
}