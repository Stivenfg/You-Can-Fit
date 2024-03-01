package com.scj.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.scj.youcanfit.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        iniciamos el splash screen y le ponemos un tiempo de 2 segundos
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setTheme(R.style.Theme_YouCanFit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}