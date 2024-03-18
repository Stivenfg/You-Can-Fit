package com.scj.youcanfit;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.components.Qualified;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scj.youcanfit.clasesextra.Alumne;
import com.scj.youcanfit.clasesextra.Exercici;
import com.scj.youcanfit.clasesextra.PuntosAlumne;
import com.scj.youcanfit.fragments.FirstFragment;
import com.scj.youcanfit.fragments.SecondFragment;
import com.scj.youcanfit.fragments.ThirdFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;
    BottomNavigationView bottomNavigationView;
    String userDB;
    int semanaActual;
    FirstFragment primerFragment = new FirstFragment();
    SecondFragment segundoFragment = new SecondFragment();
    ThirdFragment tercerFragment= new ThirdFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        userDB= user.getDisplayName()+":"+user.getUid();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        //Recuperar semana actual
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        semanaActual = localDate.get(weekFields.weekOfYear());
        String semanaAct = "Semana "+semanaActual;

        //Crear nueva DB de semana cada vez que se inicie una semana nueva
        db.collection("Puntuaje Usuarios").document(userDB).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                String nombreSemanaDB = "Semana "+semanaActual;
                                if (document.get(nombreSemanaDB)!=null){
                                    System.out.println("Existe");
                                }else{
                                    HashMap<String,Object> crearNuevaSemana = new HashMap<>();
                                    crearNuevaSemana.put(nombreSemanaDB,String.valueOf(0));
                                    db.collection("Puntuaje Usuarios").document(userDB).update(crearNuevaSemana);
                                    System.out.println("DB PUNTUAJE CREADA");
                                }
                            }
                        }
                    }
                });

        // ACTUALIZAR LA EDAD DEL ALUMNO EN CASO DE QUE CUMPLA AÑOS
        db.collection("Usuaris").document(userDB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot docu = task.getResult();
                    if (docu.exists()){
                       String fechaNalumne = docu.get("Data naixement").toString();
                       String[]fechaNalumneSplit = fechaNalumne.split("/");
                       int dia = Integer.parseInt(fechaNalumneSplit[0]);
                       int mes = Integer.parseInt(fechaNalumneSplit[1]);
                       int any = Integer.parseInt(fechaNalumneSplit[2]);

                       LocalDate fechaNalumneFormat = LocalDate.of(any,mes,dia);
                       Period period = Period.between(fechaNalumneFormat,localDate);
                       String edadActual = String.valueOf(period.getYears());
                       if (!edadActual.equals(docu.get("Edat").toString())){
                           db.collection("Usuaris").document(userDB).update("Edat",edadActual);
                           db.collection("Puntuaje Usuarios").document(userDB).update("Edat",edadActual);
                       }
                    }
                }
            }
        });



        //Lista de puntos de alumnos
        List <PuntosAlumne> puntosAlumnes = new ArrayList<PuntosAlumne>();
        List <PuntosAlumne> puntosAlumnesSexo = new ArrayList<PuntosAlumne>();
        List <PuntosAlumne> puntosAlumnesEdad = new ArrayList<PuntosAlumne>();

        //Lista de ejercicios
        List <Exercici> exercici = new ArrayList<Exercici>();
        List <Exercici> exerciciSemana = new ArrayList<Exercici>();


        //CREACION DE HILOS + CONSULTAS PARA PODER CONTROLAR LA ASINCRONIA CON FIREBASE
        Thread perfilThread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("Usuaris").document(userDB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Alumne alumne = new Alumne();
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                alumne = new Alumne(String.valueOf(document.get("Nom")),
                                        String.valueOf(document.get("Email")),
                                        String.valueOf(document.get("Sexo")),
                                        String.valueOf(document.get("Edat")),
                                        String.valueOf(document.get("Data naixement")),
                                        String.valueOf(document.get("Foto")),
                                        String.valueOf(document.get("Institut")));
                            }
                            tercerFragment.setAlumne(alumne);
                        }
                    }
                });
            }
        });
        Thread rankingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("Puntuaje Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot puntosDoc : task.getResult()){
                                PuntosAlumne puntos = new PuntosAlumne(String.valueOf(puntosDoc.get("Nom")),String.valueOf(puntosDoc.get("Edat")),String.valueOf(puntosDoc.get("Sexo")),String.valueOf(puntosDoc.get(semanaAct)));
                                puntosAlumnes.add(puntos);
                            }
                            db.collection("Usuaris").document(userDB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()){
                                            for (int i = 0; i < puntosAlumnes.size(); i++) {
                                                if (puntosAlumnes.get(i).getSexe().equals(doc.get("Sexo").toString())){
                                                    puntosAlumnesSexo.add(puntosAlumnes.get(i));
                                                }
                                            }
                                            //Falta crear el filtro de rango por edades
                                            segundoFragment.setPuntosAlumnos(puntosAlumnesSexo);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        Thread exercicisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("Reptes").document("Exercicis").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            List <Map<String,Object>> exercicis = new ArrayList<>();

                            DocumentSnapshot document = task.getResult();
                            for (int i = 0; i < document.getData().size(); i++) {

                                exercicis.add((Map<String, Object>) document.get(String.valueOf(i)));

                                Exercici exer = new Exercici((String) exercicis.get(i).get("Data Fi"),
                                        (String) exercicis.get(i).get("Data Inici"),
                                        (String) exercicis.get(i).get("Marca de temps"),
                                        (String) exercicis.get(i).get("Nom de l'exercici"),
                                        (String) exercicis.get(i).get("Número de series"),
                                        (String) exercicis.get(i).get("Repeticions"),
                                        (String) exercicis.get(i).get("Tipus d'exercici"),
                                        (String) exercicis.get(i).get("URL Vídeo explicatiu"),
                                        (String) exercicis.get(i).get("Valor de l'exercici"));

                                exercici.add(exer);
                            }
                            for (int i = 0; i < exercici.size(); i++) {
                                if (EstaEnSemana(exercici.get(i).getDataInici()) || EstaEnSemana(exercici.get(i).getDataFi())){
                                    exerciciSemana.add(exercici.get(i));
                                }
                            }

                            primerFragment.setExercicis(exerciciSemana);
                        }
                    }
                });
            }
        });

        perfilThread.start();
        rankingThread.start();
        exercicisThread.start();

        try {
            perfilThread.join();
            rankingThread.join();
            exercicisThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }



        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.thirdFragment);

    }



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
    public boolean EstaEnSemana(String dateString){
        boolean estaEntreSemana;
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z '('z')'", Locale.ENGLISH);

        Date date = null;
        try {
            // Parsea la cadena a un objeto Date
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Obtiene el número de semana del año actual
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int semanaExercici = cal.get(Calendar.WEEK_OF_YEAR);

        // Compara si los números de semana son iguales
        if (semanaActual == semanaExercici) {
            System.out.println("La fecha pertenece a la misma semana que la fecha actual.");
            estaEntreSemana =true;
        } else {
            System.out.println("La fecha no pertenece a la misma semana que la fecha actual.");
            estaEntreSemana = false;
        }
        return estaEntreSemana;
    }

}