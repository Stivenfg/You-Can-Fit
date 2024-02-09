package com.scj.youcanfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    TextView email, provider;
    Button logOut;
    FirebaseUser user;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        email = findViewById(R.id.emailTextView);
        provider = findViewById(R.id.providerTextView);
        logOut = findViewById(R.id.logoutButton);

        if(user == null){ // verificamos si el usuario tiene la sesion iniciada, y en caso de que no lo este no envie a AuthActivity
            Toast.makeText(getApplicationContext(),"No hi ha cap sesió iniciada",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
            finish();
        }else{
            email.setText(user.getEmail());
            provider.setText(user.getProviderId());
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Sesió tancada",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}