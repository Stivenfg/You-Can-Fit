package com.scj.youcanfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button btmRestore;
    EditText email;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.restoreEmail);
        btmRestore = findViewById(R.id.btnRestore);

        String correo = email.getText().toString().trim().toLowerCase();
        btmRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!correo.isEmpty()){
                    sendEmail(String.valueOf(email.getText()).toLowerCase().trim());
                }else{
                    Toast.makeText(ForgotPassword.this, "No hi ha cap email",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail(String correo) { // Instanciamos FirebaseAuth para verificar el correo y enviamos un correo para poder restablecer la contraseña
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ForgotPassword.this, "S'ha enviat un mail al correu per cambiar la contrasenya.",
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ForgotPassword.this,AuthActivity.class);
                        i.putExtra("email",correo);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(ForgotPassword.this, "Correu invalid",
                                Toast.LENGTH_SHORT).show();
                    }
                    }
                });

    }
}