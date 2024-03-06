package com.scj.youcanfit;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AuthActivity extends AppCompatActivity {

    EditText emailEditText, contrasenyaEditText;
    TextView registerButton, contrasenyaOblidada;
    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient googleSignInClient;
    Button loginButton;
    ImageView google_btn;
    int RC_SIGN_IN = 20;

    @Override
    public void onStart() { // Verificamos si se ha iniciado una sesion con anterioridad y en caso de que sea asi, nos envie directamente al HomeActivity.
        super.onStart();

        //Miramos si se puede recuperar un usaurio activo desde la ultima conexion
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent i = getIntent();

        if(currentUser != null){ //si el usuario que se trata de recuperar no es nulo y esta verificado, inicia la sesion y entra en la cuenta de dicho usuario
                Toast.makeText(AuthActivity.this,"S'ha iniciat la sessió",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FormulariUsuari.class);
                startActivity(intent);
                finish();
            }
        String email=i.getStringExtra("email"); // Miramos si no envian un putExtra desde otras activities, y si es asi que lo escriba en el EditText del correo
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
        loginButton = findViewById(R.id.logInButton);
        registerButton = findViewById(R.id.registerLayoutButton);
        google_btn = findViewById(R.id.google_btn);
        contrasenyaOblidada = findViewById(R.id.contrasenyaOblidada);

        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        // Punto 1 - Solicitamos el token y usuario de google
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
                mAuth = FirebaseAuth.getInstance(); //instanciamos FirebaseAuth para poder autentificarnos
                //creamos un string de contraseña y email le asignamos los valores escritos por los usuarios
                String email = String.valueOf(emailEditText.getText()).toLowerCase().trim();
                String contrasenya = String.valueOf(contrasenyaEditText.getText());

                //ponemos los mensajes de error en caso de que el usuario no ponga bien los datos de inicio de sesion
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir un email y una contrasenya", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir una contrasenya", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AuthActivity.this, "S'ha d'introduir una email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(contrasenya) ) {
                    iniciarSesio(email,contrasenya);
                }


            }
        });

        contrasenyaOblidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });


        //Entrar a la Actividad de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

        });
    }
    private void iniciarSesio(String email, String contrasenya) {
        mAuth.signInWithEmailAndPassword(email,contrasenya)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ // Si se encuentra el correo dentro de los usuarios de Firebase , antes de entrar a la cuenta del usuario filtramos para saber si se ha verificado la cuenta
                            FirebaseUser user =mAuth.getCurrentUser();
                            if (user.isEmailVerified()){
                                Toast.makeText(AuthActivity.this, "S'ha iniciat la sessió", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AuthActivity.this, FormulariUsuari.class);
                                startActivity(intent);
                                finish();
                            }else Toast.makeText(AuthActivity.this,"El correu d'usuari no s'ha verificat",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AuthActivity.this, "No s'ha pogut iniciar la sessió.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //Punto 2 - iniciamos la actividad de google para seleciconar los usuarios existentes dentro del sistema android
    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }
    @Override
    // si el requestCode es correcto, recuperamos el ID para iniciar sesion con google
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class); // Recuperamos la cuenta de google
                firebaseAuth(account.getIdToken());//Iniciamos la clase firebaseAuth pasandole el tokenID del usuario recuperado

            }catch (Exception e){
                Toast.makeText(this,"ERROR: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) { //con el token inciamos sesion y guardamos los datos de usuario en la base del firestore
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null); //Recuperamos las credenciales del usaurio gracias al token
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,task -> {
                    if (task.isSuccessful()){ //Si se inicia la sesion con las credenciales recuperadas
                        FirebaseUser user = mAuth.getCurrentUser();//Recuperamos el usuario
                        //Verificamos si la coleccion existe
                        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        if (!document.exists()){
                                            //en caso de que no exista significa que es la primera vez que el usuario entra asi que le creamos un documento nuevo en la base de datos
                                            HashMap<String,Object> userData = new HashMap<>();
                                            userData.put("Nom",user.getDisplayName());
                                            userData.put("Foto",user.getPhotoUrl().toString());
                                            userData.put("Email",user.getEmail());

                                            db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                                                    .set(userData); //Insertamos los datos del usuario en Firestore
                                        }
                                    }
                                });
                        Intent intent = new Intent(this,FormulariUsuari.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(AuthActivity.this,"Algo a sortit malament",Toast.LENGTH_SHORT).show();

                    }
                });


    }
}