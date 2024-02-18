package com.scj.youcanfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scj.youcanfit.fragments.FirstFragment;
import com.scj.youcanfit.fragments.SecondFragment;
import com.scj.youcanfit.fragments.ThirdFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    TextView email, provider;
    Button logOut;
    FirebaseUser user;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        email = findViewById(R.id.emailTextView);
        provider = findViewById(R.id.providerTextView);
        logOut = findViewById(R.id.logoutButton);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.firstFragment);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

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

                googleSignInClient.signOut().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Sesió tancada",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });




    }

    FirstFragment primerFragment = new FirstFragment();
    SecondFragment segundoFragment = new SecondFragment();
    ThirdFragment tercerFragment= new ThirdFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){

            case R.id.firstFragment:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameMain,primerFragment)
                        .commit();
                return true;

            case  R.id.secondFragment:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameMain,segundoFragment)
                        .commit();
                return true;

            case  R.id.thirdFragment:Fragment:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameMain,tercerFragment)
                        .commit();
                return true;
        }

        return false;
    }


}