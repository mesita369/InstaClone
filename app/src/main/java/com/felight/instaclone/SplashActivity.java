package com.felight.instaclone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loader = (ImageView) findViewById(R.id.loader);
        loader.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 3 seconds
                    sleep(3 * 1000);
                    Intent intent = new Intent(getApplicationContext(), ImageEdit.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }
}

