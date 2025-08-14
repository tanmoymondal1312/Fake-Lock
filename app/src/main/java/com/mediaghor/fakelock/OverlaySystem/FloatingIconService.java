package com.mediaghor.fakelock.OverlaySystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.mediaghor.fakelock.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.IntConsumer;

public class FloatingIconService extends Service {
    private WindowManager windowManager;
    private View floatingIconView;
    private View fullScreenOverlayView;
    private WindowManager.LayoutParams floatingParams;
    private WindowManager.LayoutParams overlayParams;
    private ImageView imgMainIcon;
    private ImageView imgLockIcon,lock1cameraicon;

    // Animation variables
    private SpringAnimation xAnimation;
    private SpringAnimation yAnimation;
    private float lastTouchX, lastTouchY;
    private int initialX, initialY;

    // Gesture detection
    private GestureDetector gestureDetector;
    private boolean isDragging = false;
    private int touchSlop;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"ClickableViewAccessibility", "ForegroundServiceType"})
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(ServiceNotificationHelper.getNotificationId(),
                ServiceNotificationHelper.createNotification(this));

        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        setupGestureDetector();
        createFloatingIcon();
        prepareFullScreenOverlay();
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                showFullScreenOverlay();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Handle double tap if needed
                return true;
            }
        });
    }

    private void createFloatingIcon() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        floatingIconView = inflater.inflate(R.layout.floating_icon_layout, null);

        floatingParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        floatingParams.gravity = Gravity.TOP | Gravity.START;
        floatingParams.x = 0;
        floatingParams.y = 100;

        imgMainIcon = floatingIconView.findViewById(R.id.floating_icon);
        initializeSpringAnimations();
        setupTouchListener();

        try {
            windowManager.addView(floatingIconView, floatingParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareFullScreenOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        fullScreenOverlayView = inflater.inflate(R.layout.lock_screen1, null);

        overlayParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        // Initialize views and set current time/date
        TextView timeText = fullScreenOverlayView.findViewById(R.id.timeText);
        TextView dateText = fullScreenOverlayView.findViewById(R.id.dateText);
        imgLockIcon = fullScreenOverlayView.findViewById(R.id.unlock_fl1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());

        timeText.setText(currentTime);
        dateText.setText(currentDate);

        // Set click listener for the lock icon
        imgLockIcon.setOnClickListener(v -> {
            v.setEnabled(false); // Prevent multiple clicks
            hideFullScreenOverlay();
            v.postDelayed(() -> v.setEnabled(true), 500); // Re-enable after animation
        });
    }

     private void showFullScreenOverlay() {
        if (fullScreenOverlayView.getParent() != null || floatingIconView.getVisibility() != View.VISIBLE) {
            return;
        }

        // Reset any ongoing animations
        floatingIconView.animate().cancel();
        fullScreenOverlayView.animate().cancel();

        // Animate the floating icon out
        animateFloatingIconOut(() -> {
            try {
                if (fullScreenOverlayView.getParent() == null) {
                    windowManager.addView(fullScreenOverlayView, overlayParams);
                    animateOverlayIn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void hideFullScreenOverlay() {
        if (fullScreenOverlayView.getParent() == null) {
            return;
        }

        // Reset any ongoing animations
        fullScreenOverlayView.animate().cancel();
        floatingIconView.animate().cancel();

        animateOverlayOut(() -> {
            try {
                if (fullScreenOverlayView.getParent() != null) {
                    windowManager.removeView(fullScreenOverlayView);
                    animateFloatingIconIn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void animateFloatingIconOut(Runnable onComplete) {
        floatingIconView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        floatingIconView.setVisibility(View.GONE);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        floatingIconView.animate().setListener(null); // Clear listener
                    }
                })
                .start();
    }

    private void animateFloatingIconIn() {
        floatingIconView.setVisibility(View.VISIBLE);
        floatingIconView.setScaleX(0f);
        floatingIconView.setScaleY(0f);
        floatingIconView.setAlpha(0f);

        floatingIconView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(200)
                .setListener(null) // Clear any previous listeners
                .start();
    }

    private void animateOverlayIn() {
        fullScreenOverlayView.setAlpha(0f);
        fullScreenOverlayView.setScaleX(0.9f);
        fullScreenOverlayView.setScaleY(0.9f);
        fullScreenOverlayView.setVisibility(View.VISIBLE);

        fullScreenOverlayView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setListener(null) // Clear any previous listeners
                .start();
    }

    private void animateOverlayOut(Runnable onComplete) {
        fullScreenOverlayView.animate()
                .alpha(0f)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        fullScreenOverlayView.animate().setListener(null); // Clear listener
                    }
                })
                .start();
    }

    private void initializeSpringAnimations() {
        // For X axis
        SpringForce springForceX = new SpringForce(0)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

        xAnimation = new SpringAnimation(floatingIconView, SpringAnimation.TRANSLATION_X)
                .setSpring(springForceX);

        // For Y axis
        SpringForce springForceY = new SpringForce(0)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

        yAnimation = new SpringAnimation(floatingIconView, SpringAnimation.TRANSLATION_Y)
                .setSpring(springForceY);
    }

    private void setupTouchListener() {
        imgMainIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handleTouchDown(event);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        return handleTouchMove(event);

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handleTouchUp();
                        return true;
                }
                return false;
            }
        });
    }

    private void handleTouchDown(MotionEvent event) {
        xAnimation.cancel();
        yAnimation.cancel();
        lastTouchX = event.getRawX();
        lastTouchY = event.getRawY();
        initialX = floatingParams.x;
        initialY = floatingParams.y;
        isDragging = false;

        // Visual feedback
        imgMainIcon.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .start();
    }

    private boolean handleTouchMove(MotionEvent event) {
        float dx = Math.abs(event.getRawX() - lastTouchX);
        float dy = Math.abs(event.getRawY() - lastTouchY);

        if (!isDragging && (dx > touchSlop || dy > touchSlop)) {
            isDragging = true;
        }

        if (isDragging) {
            float newX = initialX + (event.getRawX() - lastTouchX);
            float newY = initialY + (event.getRawY() - lastTouchY);

            floatingParams.x = (int) newX;
            floatingParams.y = (int) newY;
            windowManager.updateViewLayout(floatingIconView, floatingParams);
        }
        return true;
    }

    private void handleTouchUp() {
        imgMainIcon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .start();

        if (isDragging) {
            snapToEdge();
        }
    }

    private void snapToEdge() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        floatingIconView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int viewWidth = floatingIconView.getMeasuredWidth();
        int viewHeight = floatingIconView.getMeasuredHeight();

        int centerX = floatingParams.x + viewWidth / 2;
        boolean snapToRight = centerX > metrics.widthPixels / 2;

        int finalX = snapToRight ? metrics.widthPixels - viewWidth : 0;
        int finalY = Math.max(0, Math.min(floatingParams.y, metrics.heightPixels - viewHeight));

        animateToPosition(finalX, finalY);
    }

    private void animateToPosition(int x, int y) {
        setupAndStartAnimator(floatingParams.x, x, (value) -> {
            floatingParams.x = value;
            windowManager.updateViewLayout(floatingIconView, floatingParams);
        });

        setupAndStartAnimator(floatingParams.y, y, (value) -> {
            floatingParams.y = value;
            windowManager.updateViewLayout(floatingIconView, floatingParams);
        });
    }

    private void setupAndStartAnimator(int start, int end, IntConsumer updateAction) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(animation ->
                updateAction.accept((int) animation.getAnimatedValue()));
        animator.setDuration(300);
        animator.setInterpolator(new OvershootInterpolator(0.5f));
        animator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingIconView != null && windowManager != null) {
            try {
                windowManager.removeView(floatingIconView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fullScreenOverlayView != null && windowManager != null) {
            try {
                windowManager.removeView(fullScreenOverlayView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, FloatingIconService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, FloatingIconService.class));
    }
}