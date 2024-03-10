package com.scj.youcanfit;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText, contrasenyaEditText, uNom;
    Button registerButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar pb;
    String fotoPerfil;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailEditText = findViewById(R.id.registerEmailEditText);
        contrasenyaEditText =findViewById(R.id.registerContrasenyaEditText);
        registerButton = findViewById(R.id.registerButton);
        pb = findViewById(R.id.progressbar);
        uNom=findViewById(R.id.nom);
        db = FirebaseFirestore.getInstance(); // Instanciamos la base de datos
        pb.setVisibility(View.INVISIBLE);


        //Instanciamos el FirebaseStorage para poder recuperar la foto de perfil de los nuevos usuarios
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://you-can-fit-412207.appspot.com"); // al instanciar procuramos poner el bucket donde se recuperara la foto
        StorageReference storageRef = storage.getReference();// recogemos la referencia
        StorageReference imageRef = storageRef.child("images/default_user_photo.jpg"); //recogemos la imagen

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // recogemos la url de la foto del nuevo usuario
            @Override
            public void onSuccess(Uri uri) {
                fotoPerfil=uri.toString(); // Pasamos de Uri a String
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                //instanciamos la FirebaseAuth para poder autentificarnos
                mAuth = FirebaseAuth.getInstance();
                //creamos un string de contraseña y email le asignamos los valores escritos por los usuarios
                String email, contrasenya, nom;
                email = String.valueOf(emailEditText.getText()).toLowerCase().trim();
                contrasenya = String.valueOf(contrasenyaEditText.getText());
                nom = String.valueOf(uNom.getText());

                //ponemos los mensajes de error en caso de que el usuario no ponga bien los datos
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(contrasenya) && TextUtils.isEmpty(nom)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir dades per crear el compte", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }else if (TextUtils.isEmpty(contrasenya)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir una contrasenya", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir un email", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }else if (TextUtils.isEmpty(nom)) {
                    Toast.makeText(RegisterActivity.this, "S'ha d'introduir el nom", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }else if(esGmail(email)){
                    Toast.makeText(RegisterActivity.this, "Per crear un compte de google s'ha de fer desde la pagina d'inici de sessio", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);

                }else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(contrasenya) && !TextUtils.isEmpty(nom) && !esGmail(email)){ // En caso de que todo el formulario este completo y el correo no es un Gmail, se puede crear la cuenta

                    mAuth.createUserWithEmailAndPassword(email, contrasenya) //Creamos un usuario con el correo y contraseña descritos por el usuario
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pb.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser(); // una ves creado el usuario lo recuperamos en la variable
                                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder() // Ponemos los datos a actualizar del nuevo usuario ya que cuando se crea uno nuevo, este esta completamente vacio
                                                .setDisplayName(nom)
                                                .setPhotoUri(Uri.parse(fotoPerfil))
                                                .build();
                                        user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() { //Actualizamos los datos del nuevo usuario
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //Si este usuario se ha actualizado y creado correctamente enviamos un correo de verificacion
                                                    user.sendEmailVerification();
                                                    //Creamos un hashmap para poder guardar los datos del usuario en la base de datos de Firebase Firestore
                                                    HashMap<String,Object> userData = new HashMap<>();
                                                    userData.put("Nom",user.getDisplayName());
                                                    userData.put("Foto",user.getPhotoUrl().toString());
                                                    userData.put("Email",user.getEmail());
                                                    userData.put("Sexo",String.valueOf(""));
                                                    userData.put("Edat",String.valueOf(""));
                                                    userData.put("Institut",String.valueOf(""));
                                                    userData.put("Data naixement",String.valueOf(""));

                                                    //Agregamos los datos en la base de datos de Firestore
                                                    String userDB = user.getDisplayName()+":"+user.getUid();
                                                    db.collection("Usuaris").document(userDB).set(userData);

                                                    //Creamos la base de datos de los puntos del usuario
                                                    LocalDate localDate = LocalDate.now();
                                                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                                                    int semanaActual = localDate.get(weekFields.weekOfYear());
                                                    HashMap<String,Object> pointsData = new HashMap<>();
                                                    pointsData.put("Semana "+String.valueOf(semanaActual), 0);

                                                    db.collection("Puntuaje Usuarios").document(userDB).set(pointsData);


                                                    Toast.makeText(RegisterActivity.this, "Compte creat correctament, verificar correu abants d'iniciar la sesió",
                                                            Toast.LENGTH_SHORT).show();

                                                    Intent i = new Intent(RegisterActivity.this,AuthActivity.class);
                                                    i.putExtra("email",email);
                                                    startActivity(i);
                                                    finish();

                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Hi ha hagut un error en crear el compte. Torna a intentar-ho.",
                                                Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

    }
    private boolean esGmail(String correo) { // Verificamos que el correo que se esta creando no sea un gmail ya que se si crea uno y luego se verifica con el usuario de google, la cuenta creada con este formulario se perderia
        Boolean esG = correo.matches(".*@gmail.*");
        return esG;
    }
}