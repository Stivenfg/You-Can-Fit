package com.scj.youcanfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scj.youcanfit.fragments.FirstFragment;
import com.scj.youcanfit.fragments.SecondFragment;
import com.scj.youcanfit.fragments.ThirdFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);





        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.thirdFragment);

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