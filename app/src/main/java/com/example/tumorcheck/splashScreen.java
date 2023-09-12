package com.example.tumorcheck;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class splashScreen extends AppCompatActivity {

    // Duration of the splash screen in milliseconds
    private static final long SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        GifImageView splashImageView = findViewById(R.id.gifImageView);

        // Load the GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.splash)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(splashImageView);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashScreen.this, main_dashboard.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}