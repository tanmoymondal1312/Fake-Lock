package com.mediaghor.darkscreen.OverlaySystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
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
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.mediaghor.darkscreen.Helper.SaveMyLayout;
import com.mediaghor.darkscreen.R;

import java.util.function.IntConsumer;

public class FloatingIconService extends Service {
    private WindowManager windowManager;
    private View floatingIconView;
    private View lockOverlayView;
    private WindowManager.LayoutParams floatingParams;
    private WindowManager.LayoutParams lockOverlayParams;
    private ImageView imgMainIcon;

    // Animation variables
    private SpringAnimation xAnimation;
    private SpringAnimation yAnimation;
    private float lastTouchX, lastTouchY;
    private int initialX, initialY;

    // Gesture detection
    private GestureDetector gestureDetector;
    private boolean isDragging = false;
    private int touchSlop;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

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

    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                prepareLockOverlay();
                showLockOverlay();


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

    private void prepareLockOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int savedId = SaveMyLayout.getSavedLayoutId(this);
        if (savedId!=-1){
            lockOverlayView = inflater.inflate(savedId, null);
        }else {
            lockOverlayView = inflater.inflate(R.layout.activity_fake_lock, null);
            SaveMyLayout.saveSelectedLayoutId(this,R.layout.activity_fake_lock);

        }


        int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        lockOverlayParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lockOverlayParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        lockOverlayParams.gravity = Gravity.TOP | Gravity.START;

        ImageView unlockBtn = lockOverlayView.findViewById(R.id.unlock_fl1);
        unlockBtn.setOnClickListener(v -> removeLockOverlay());

        // System UI visibility control
        lockOverlayView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                hideSystemUI();
            }
        });
    }

    private void showLockOverlay() {
        if (lockOverlayView.getParent() != null) return;

        animateFloatingIconOut(() -> {
            try {
                windowManager.addView(lockOverlayView, lockOverlayParams);
                hideSystemUI();

                // Periodic check to ensure system UI stays hidden
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (lockOverlayView != null && lockOverlayView.isAttachedToWindow()) {
                            hideSystemUI();
                            uiHandler.postDelayed(this, 1000);
                        }
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                animateFloatingIconIn();
            }
        });
    }

    private void hideSystemUI() {
        if (lockOverlayView != null) {
            lockOverlayView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LOW_PROFILE
            );
        }
    }

    private void removeLockOverlay() {
        if (lockOverlayView != null && lockOverlayView.isAttachedToWindow()) {
            windowManager.removeView(lockOverlayView);
            animateFloatingIconIn();
        }
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
                        floatingIconView.animate().setListener(null);
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
                .setListener(null)
                .start();
    }

    private void initializeSpringAnimations() {
        SpringForce springForceX = new SpringForce(0)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

        xAnimation = new SpringAnimation(floatingIconView, SpringAnimation.TRANSLATION_X)
                .setSpring(springForceX);

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
        uiHandler.removeCallbacksAndMessages(null);

        if (floatingIconView != null && floatingIconView.isAttachedToWindow()) {
            windowManager.removeView(floatingIconView);
        }
        if (lockOverlayView != null && lockOverlayView.isAttachedToWindow()) {
            windowManager.removeView(lockOverlayView);
        }
    }

    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static void startService(Context context) {
        if (hasOverlayPermission(context)) {
            Intent intent = new Intent(context, FloatingIconService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, FloatingIconService.class));
    }
}