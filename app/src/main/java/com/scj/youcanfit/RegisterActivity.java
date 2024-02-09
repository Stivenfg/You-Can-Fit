package com.scj.youcanfit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText, contrasenyaEditText;
    TextView loginNow;
    Button registerButton;
    FirebaseAuth mAuth;
    ProgressBar pb;
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
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailEditText = findViewById(R.id.registerEmailEditText);
        contrasenyaEditText =findViewById(R.id.registerContrasenyaEditText);
        registerButton = findViewById(R.id.registerButton);
        pb = findViewById(R.id.progressbar);
        loginNow = findViewById(R.id.loginNow);
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir un email y una contrasenya", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                }else if (TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir una contrasenya", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir una email", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, contrasenya)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pb.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Compte creat correctament.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(RegisterActivity.this, "No s'ha pogut crear el compte.",
                                            Toast.LENGTH_SHORT).show();
                                    pb.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }
}