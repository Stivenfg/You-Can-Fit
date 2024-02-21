package com.scj.youcanfit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public class AuthActivity extends AppCompatActivity {

    EditText emailEditText, contrasenyaEditText;
    TextView registerButton, contrasenyaOblidada;
    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient googleSignInClient;
    ProgressBar pb;
    Button loginButton;
    ImageView google_btn;

    int RC_SIGN_IN = 20;
    @Override
    public void onStart() { // Verificamos si se ha iniciado una sesion con anterioridad y en caso de que sea asi, nos envie directamente al HomeActivity.
        super.onStart();

        //Fem instancia del firebase y la base de dades
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //firebaseDatabase = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        emailEditText = findViewById(R.id.emailEditText);

        if(currentUser != null){
            if (currentUser.isEmailVerified()){
                Toast.makeText(AuthActivity.this,"S'ha iniciat la sessió",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

        String email=i.getStringExtra("email");
        if ( email!=null){
            emailEditText.setText(email);
        }



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        emailEditText = findViewById(R.id.emailEditText);
        contrasenyaEditText =findViewById(R.id.contrasenyaEditText);
        pb = findViewById(R.id.progressbar);
        loginButton = findViewById(R.id.logInButton);
        registerButton = findViewById(R.id.registerLayoutButton);
        google_btn = findViewById(R.id.google_btn);
        contrasenyaOblidada = findViewById(R.id.contrasenyaOblidada);

        //fem instancia de la base de dades y del firebase per a fer la conexió
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance();

        //Solicitem el token y recuperem el email per a fer el inici de sesió
        @SuppressLint("ResourceType")
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);


        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();

                pb.setVisibility(View.VISIBLE);
                mAuth = FirebaseAuth.getInstance(); //instanciamos la base de datos para poder autentificarnos
                //creamos un string de contraseña y email le asignamos los valores escritos por los usuarios
                String email = String.valueOf(emailEditText.getText()).toLowerCase().trim();
                String contrasenya = String.valueOf(contrasenyaEditText.getText());

                //ponemos los mensajes de error en caso de que el usuario no ponga bien los datos de inicio de sesion
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir un email y una contrasenya", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                }else if (TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir una contrasenya", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir una email", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                }


                mAuth.signInWithEmailAndPassword( email, contrasenya) // creamos el inicio de sesion de los usuarios
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        if(user.isEmailVerified()) {
                                            Toast.makeText(AuthActivity.this, "S'ha iniciat la sessió", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else Toast.makeText(AuthActivity.this,"El correu d'usuari no s'ha verificat",Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(AuthActivity.this, "No s'ha pogut iniciar la sessió.",
                                                Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.GONE);

                                    }
                                }

                        });
            }
        });

        contrasenyaOblidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, forgotPassword.class);
                startActivity(intent);
                finish();
            }
        });


        //Entrar a la Actividad de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }

        });


    }

    private void googleSignIn() { //iniciamos la actividad de google para seleciconar los usuarios
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){ // si el requestCode es correcto recuperamos el ID para iniciar sesion
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) { //con el token inciamos sesion y guardamos los datos de usuario en la base del firestore
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document =task.getResult();
                                                if (!document.exists()){
                                                    HashMap<String,Object> userData = new HashMap<>();
                                                    userData.put("Nom",user.getDisplayName());
                                                    userData.put("Foto",user.getPhotoUrl().toString());
                                                    userData.put("Email",user.getEmail());

                                                    db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                                                            .set(userData);
                                                }
                                            }
                                        }
                                    });

                            Intent intent = new Intent(AuthActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(AuthActivity.this,"Algo a sortit malament",Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }
}



