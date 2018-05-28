package com.ats.barstockexchange.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.util.PrintHelper;

public class SplashActivity extends AppCompatActivity {

    private Button btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btnPrint = findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintHelper printHelper = new PrintHelper(SplashActivity.this, "TCP:192.168.1.150", 9);
                printHelper.runPrintReceiptSequence();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, 3000);
    }
}
