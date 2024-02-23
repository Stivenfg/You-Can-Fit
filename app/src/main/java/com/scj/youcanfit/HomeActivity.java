package com.scj.youcanfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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


    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.firstFragment);



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