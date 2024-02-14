package com.scj.youcanfit;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AuthActivity extends AppCompatActivity {

    EditText emailEditText, contrasenyaEditText;
    TextView registerButton;
    FirebaseAuth mAuth;
    ProgressBar pb;
    Button loginButton;
    ImageView google_btn;

    @Override
    public void onStart() { // Verificamos si se ha iniciado una sesion con anterioridad y en caso de que sea asi, nos envie directamente al HomeActivity.
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(getApplicationContext(),"S'ha iniciat la sessió",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
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
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                mAuth = FirebaseAuth.getInstance(); //instanciamos la base de datos para poder autentificarnos
                //creamos un string de contraseña y email le asignamos los valores escritos por los usuarios
                String email, contrasenya;
                email = String.valueOf(emailEditText.getText());
                contrasenya = String.valueOf(contrasenyaEditText.getText());

                //ponemos los mensajes de error en caso de que el usuario no ponga bien los datos
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
                                    Toast.makeText(getApplicationContext(),"S'ha iniciat la sessió",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
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

    }
}