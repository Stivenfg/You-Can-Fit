package com.scj.youcanfit;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FormulariUsuari extends AppCompatActivity {

    Spinner institut;
    RadioGroup radioGroup;
    ImageButton btn_calendario;
    EditText editTextFechaNacimiento;
    String sexo;
    Button boton;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    String userDB;
    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulari_usuari);
        institut=findViewById(R.id.instituts);
        boton=findViewById(R.id.continuar);
        radioGroup=findViewById(R.id.radioGroup);
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento);
        btn_calendario = findViewById(R.id.btn_calendario);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(FormulariUsuari.this, gso);
        //Creamos la base de datos de los puntos del usuario
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semanaActual = localDate.get(weekFields.weekOfYear());
        userDB= user.getDisplayName()+":"+user.getUid();



        //Actualizacion del sexo del usuario
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId); //Aqui obtenemos el radio seleciconado
                if (radioButton != null){
                    sexo = radioButton.getText().toString(); //Guardamos el sexo del usuario
                    HashMap<String,Object> actualizarSexo = new HashMap<>();
                    actualizarSexo.put("Sexo",sexo);
                    db.collection("Usuaris").document(userDB).update(actualizarSexo);

                    HashMap<String,Object> pointsData = new HashMap<>();
                    pointsData.put("Sexo",sexo);
                    db.collection("Puntuaje Usuarios").document(userDB).update(pointsData);

                }
            }
        });





        //Recuperamos la coleccion de institutos, y luego dentro de estos institutos recumperamos el nombre y los datos de esta coleccion
        List<String> nameINS = new ArrayList<>();
        List<String> collectionINS = new ArrayList<>();
        db.collection("Instituts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String documentId = documentSnapshot.getId();
                    collectionINS.add(documentId);
                }

                // Lista de tareas para almacenar las tareas de obtención de cada documento
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                // Crear tareas de obtención de cada documento
                for (String docId : collectionINS) {
                    Task<DocumentSnapshot> task = db.collection("Instituts").document(docId).get();
                    tasks.add(task);
                }

                // Esperar a que todas las tareas se completen
                Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

                allTasks.addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots) {
                            String nom = document.getString("Nom");
                            nameINS.add(nom);
                        }
                        //GUARDAMOS EN UN ARRAYLIST EL NOMBRE DENTRO DE LOS INSTITUTOS
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(FormulariUsuari.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, nameINS);
                        institut.setAdapter(adapter);

                    }
                });

            }
        });

        institut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //GUARDAMOS EN LA BASE DE DATOS DEL USUARIO LA SELECCION DEL INSTITUTO Y LO INSERTAMOS
                HashMap<String, Object> actualizarInstituto = new HashMap<>();
                actualizarInstituto.put("Institut",institut.getSelectedItem());
                db.collection("Usuaris").document(userDB).update(actualizarInstituto);

                HashMap<String,Object> pointsData = new HashMap<>();
                pointsData.put("Institut",institut.getSelectedItem());
                db.collection("Puntuaje Usuarios").document(userDB).update(pointsData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FormulariUsuari.this,"No s'ha seleccionat cap centre",Toast.LENGTH_SHORT).show();
            }
        });
        //El boton de continuar solo funcionara si todos los campo estan completos
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(FormulariUsuari.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void mostrarCalendario (View v){
        String userDB = user.getDisplayName()+":"+user.getUid();

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dataNaix = dayOfMonth+"/"+(month+1)+"/"+year;
                LocalDate fechaActual = LocalDate.now();
                LocalDate fechaUsuario = LocalDate.of(year,month+1,dayOfMonth);
                Period period = Period.between(fechaUsuario,fechaActual);
                String edat = String.valueOf(period.getYears());

                if (Integer.parseInt(edat)>=12){
                    editTextFechaNacimiento.setText(dataNaix);
                    HashMap<String,Object> actualizarEdat = new HashMap<>();
                    actualizarEdat.put("Edat",edat);
                    actualizarEdat.put("Data naixement",dataNaix);
                    db.collection("Usuaris").document(userDB).update(actualizarEdat);

                    HashMap<String,Object> pointsData = new HashMap<>();
                    pointsData.put("Edat",edat);
                    db.collection("Puntuaje Usuarios").document(userDB).update(pointsData);
                }else{
                    Toast.makeText(FormulariUsuari.this,"No es pot tindre una edat inferior a 12 anys",Toast.LENGTH_SHORT).show();
                }


            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        d.show();
    }

    //Cuando se cierre la app o se le de al boton de atras se cierra la sesion del formulario
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();

        googleSignInClient.signOut().addOnCompleteListener(FormulariUsuari.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                CerrarSesion();

            }
        });

    }


    public void CerrarSesion(){
        HashMap<String, Object> eliminarDatos = new HashMap<>();
        eliminarDatos.put("Institut","");
        eliminarDatos.put("Edat","");
        eliminarDatos.put("Data naixement","");
        eliminarDatos.put("Sexo","");

        HashMap<String, Object> eliminarDatosP = new HashMap<>();
        eliminarDatosP.put("Institut","");
        eliminarDatosP.put("Edat","");
        eliminarDatosP.put("Sexo","");

        db.collection("Usuaris").document(userDB).update(eliminarDatos);
        db.collection("Puntuaje Usuarios").document(userDB).update(eliminarDatosP);

        Intent intent = new Intent(FormulariUsuari.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}