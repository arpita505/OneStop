package com.example.arpita.try1;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);


        ProgressBar p;
        p = (ProgressBar)findViewById(R.id.progressBar1);
        if (p.getVisibility()!= View.VISIBLE){
            p.setVisibility(View.VISIBLE);
        }


        Thread welcome = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcome.start();

    }
}
