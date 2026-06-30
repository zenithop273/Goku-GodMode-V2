package com.goku.assistant;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.animation.ValueAnimator;
import java.util.Locale;

public class GokuService extends Service implements TextToSpeech.OnInitListener {
    private WindowManager windowManager;
    private View floatingView;
    private TextToSpeech tts;
    private View glowCircle;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Setup Floating Window
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);
        glowCircle = floatingView.findViewById(R.id.glowCircle);

        int layoutFlag = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0; params.y = 100;
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        // 2. Setup TTS (Sweet Girl Voice)
        tts = new TextToSpeech(this, this);

        // Tap to simulate listening
        floatingView.setOnClickListener(v -> {
            animateListening();
            speak("Yes Boss, I am listening.");
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("hi", "IN")); // Indian Accent
            tts.setPitch(1.3f); // Higher pitch for sweet female voice
            tts.setSpeechRate(1.0f);
            speak("System online, Boss. Ready for bug bounty.");
        }
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void animateListening() {
        ValueAnimator anim = ValueAnimator.ofFloat(1f, 1.5f, 1f);
        anim.setDuration(800);
        anim.setRepeatCount(2);
        anim.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            glowCircle.setScaleX(scale);
            glowCircle.setScaleY(scale);
        });
        anim.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
        if (tts != null) { tts.stop(); tts.shutdown(); }
    }
}
