package com.siskopsya.siskopsya;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Launch extends AppCompatActivity {
    int login = 0;
    final Handler handler = new Handler();
    SharedPreferences sharedpreferences;
    Boolean session=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean("session_status", false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 5000ms
                if(session){
                    Intent sudahLogin = new Intent(Launch.this, MainActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    Toast.makeText(Launch.this, "Selamat Datang Kembali",
                            Toast.LENGTH_LONG).show();
                    startActivity(sudahLogin);
                }else{
                    Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    Toast.makeText(Launch.this, "Harap Login Dahulu!",
                            Toast.LENGTH_LONG).show();
                    startActivity(belumLogin);
                }
            }
        }, 3500);
    }
}
