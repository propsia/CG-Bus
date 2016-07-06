package com.nadeem.cgbus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * This class will set up Intrestial add to the application
 * Created by Nadeem Ahmed on 07/05/16.
 */
public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), TabDisplay.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}
