package com.scj.youcanfit;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormulariUsuari extends AppCompatActivity {

    Spinner institut;
    Button boton;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulari_usuari);
        institut=findViewById(R.id.instituts);
        boton=findViewById(R.id.continuar);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists() && document.getString("Institut")!=null || document.getString("Sexo") != null || document.getString("Edat") != null){

                                    }
                                }
                            }
                        });
                Intent i = new Intent(FormulariUsuari.this, HomeActivity.class);
                startActivity(i);
                finish();
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
                db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                        .update(actualizarInstituto).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FormulariUsuari.this,"L'institut s'ha actualitzat",Toast.LENGTH_SHORT).show();

                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FormulariUsuari.this,"No s'ha seleccionat cap centre",Toast.LENGTH_SHORT).show();
            }
        });
    }//
}//