// FirstFragment.java
package com.scj.youcanfit.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scj.youcanfit.R;

import java.sql.Time;
import java.util.Date;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import com.scj.youcanfit.clasesextra.VideoDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment {

    private TextView tituloSemana;
    private int numExercicis;
    private int numtodate;
    private RecyclerView recyclerView;
    MyAdapter adapter;
    FirebaseFirestore db;
    private List<Map<String, Object>> exercicisList;
    private Map<String,Object> exerciciss;

    //CHRONOMETRO
    CountDownTimer timer;
    private ImageView btIniciEjercicios;
    private TextView chrono;
    private String chronoActivo;
    Spinner sp_lugar;
    FirebaseAuth auth;
    FirebaseUser user;

    //CONSTRUCTOR VACIO DEL FIRST FRAGMENT
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        db = FirebaseFirestore.getInstance();
        sp_lugar= rootView.findViewById(R.id.sp_lugar);
        tituloSemana = rootView.findViewById(R.id.tituloSemana);
        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semanaActual = localDate.get(weekFields.weekOfYear());
        String nomUsuari = user.getDisplayName()+":"+user.getUid();
        System.out.println("Nombre: "+ nomUsuari);
        db.collection("Puntuaje Usuarios").document(nomUsuari).get()
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
                                    db.collection("Puntuaje Usuarios").document(nomUsuari).update(crearNuevaSemana);
                                    System.out.println("DB PUNTUAJE CREADA");
                                }
                            }
                        }
                    }
                });

        tituloSemana.setText(tituloSemana.getText().toString()+" "+String.valueOf(semanaActual));


        db.collection("Reptes").document("Exercicis").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                numExercicis = document.getData().size();
                                exerciciss = document.getData();
                                System.out.println(exerciciss);
                                numtodate = numExercicis;

                                // Aquí recuperamos los campos Data Inici y Data Fi como Timestamps


                                adapter.notifyItemChanged(numExercicis);


                                Map<String, Object> allExercicis = document.getData();
                                exercicisList = new ArrayList<>();


                                // Suponiendo que cada clave es un ID de ejercicio y su valor es un mapa de los datos del ejercicio
                                for (Map.Entry<String, Object> entry : allExercicis.entrySet()) {
                                    Map<String, Object> exerciciMap = (Map<String, Object>) entry.getValue();
                                    exercicisList.add(exerciciMap);
                                    System.out.println("Ejercicio ID: " + entry.getKey());
                                    System.out.println(exerciciMap);
                                }

                                for (int i = 0; i < numExercicis ; i++) {
                                    Map<String, Object> exer = exercicisList.get(i);
                                    System.out.println("TIPO DE EJERCICIO  " + String.valueOf(i) + " " + exer.get("Tipus d'exercici"));
                                }

                                // En este punto, exercicisList contiene todos los ejercicios como mapas
                                // Puedes actualizar la interfaz de usuario o realizar otras operaciones con esta lista

                            } else {
                                Toast.makeText(getContext(), "No existe el documento", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error al obtener el documento", Toast.LENGTH_LONG).show();
                        }
                    }


                });



        //IMPLEMENTACION DE LOS LUGARES DE EJERCICIOS

        //Recuperamos la coleccion de Los lugares de donde se hace los ejercicios, y luego dentro de estos institutos recumperamos el nombre y los datos de esta coleccion
        List<String> collectionLloc = new ArrayList<>();
        List<String> nameLloc = new ArrayList<>();
        db.collection("Zonas Deportivas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String documentId = documentSnapshot.getId();
                    collectionLloc.add(documentId);
                }

                // Lista de tareas para almacenar las tareas de obtención de cada documento
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                // Crear tareas de obtención de cada documento
                for (String docId : collectionLloc) {
                    Task<DocumentSnapshot> task = db.collection("Zonas Deportivas").document(docId).get();
                    tasks.add(task);
                }

                // Esperar a que todas las tareas se completen
                Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

                allTasks.addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots) {
                            String nom = document.getString("Nom");
                            nameLloc.add(nom);
                        }
                        //GUARDAMOS EN UN ARRAYLIST EL NOMBRE DENTRO DE LOS INSTITUTOS
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, nameLloc);
                        sp_lugar.setAdapter(adapter);

                    }
                });

            }
        });


        //RECYCLERVIEW
        recyclerView = rootView.findViewById(R.id.recyclerView);
        // Create and set the adapter
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        // Set the layout manager (e.g., LinearLayoutManager or GridLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //CAJA EJERCICIO BARRA


        //ELEMENTOS CHRONOMETRO
        chrono = rootView.findViewById(R.id.chrono);
        btIniciEjercicios = rootView.findViewById(R.id.buttonInici);
        chronoActivo = chrono.toString();
        btIniciEjercicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startTime(btIniciEjercicios,10);

            }
        });
        return rootView;
    }



    //METODO DEL CHRONOMETRO
    private void startTime(ImageView buttonChrono, long tiempoSegundos){
        timer = new CountDownTimer(TimeUnit.SECONDS.toMillis(tiempoSegundos), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Formulas de pasar de tiempo a milisegundos
                long hours = (millisUntilFinished/ 1000)/3600;
                long minutes = ((millisUntilFinished / 1000) % 3600) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds); // Formato de como queremos que se muestre en el textView
                chrono.setText(timeFormatted);//
            }

            @Override
            public void onFinish() {
                chrono.setText("00:00:00");
                Toast.makeText(getContext(), "Tiempo agotado", Toast.LENGTH_SHORT).show();
                buttonChrono.setEnabled(true);

            }
        }.start();
        buttonChrono.setEnabled(false);
    }


    //RECYCLER VIEW
    private static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myButton;
        public ImageView video;

        //BARRA DE EJERCICIO ACTIVO
        LinearLayout cajaEjercicio;
        ImageView barra;
        private boolean isMoving = false;
        private Animation animation;


        //CONTADOR DE REPETICIONES

        TextView contador;
        Integer contadorRepeticiones = 0;



        MyViewHolder(View itemView) {
            super(itemView);
            myButton = itemView.findViewById(R.id.nomExercici);
            video = itemView.findViewById(R.id.videoEj1);

            //CONTADOR DE REPETICIONES
            contador = itemView.findViewById(R.id.contador);



            //ANIMACION BARRA
            barra = itemView.findViewById(R.id.barra);
            animation = AnimationUtils.loadAnimation(barra.getContext(), R.anim.anima_barra);
            cajaEjercicio=itemView.findViewById(R.id.cajaNomEjercicio);
            cajaEjercicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isMoving == false){
                        barra.startAnimation(animation);
                        isMoving = true;

                    } else {
                        barra.clearAnimation();
                        isMoving = false;
                        //Funcionalidad para que se cuente y se muestre el numero de repeticiones de cada ejercicio finalizado.
                        contadorRepeticiones++;
                        String cuentaRep = contadorRepeticiones.toString();
                        contador.setText(cuentaRep);

                    }

                }
            });

        }

    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        // Implement the necessary methods: onCreateViewHolder, onBindViewHolder, getItemCount
        TextView nomExercici;
        TextView numRepet;
        TextView numSeries;

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);
            nomExercici = itemView.findViewById(R.id.nomExercici);
            numRepet = itemView.findViewById(R.id.numRepet);
            numSeries = itemView.findViewById(R.id.numSeries);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String urlVideo;
            // Obtiene el mapa de ejercicio correspondiente a la posición del ítem
            Map<String, Object> exer = exercicisList.get(position);

            Timestamp dataIniciTimestamp = (Timestamp) exer.get("Data Inici");
            Timestamp dataFiTimestamp = (Timestamp) exer.get("Data Fi");
            System.out.println("!!TIMESTAMPS!! "+dataIniciTimestamp+"___"+dataFiTimestamp);

            Date currentDate = new Date(); // Fecha actual
            Date dataInici = null;
            Date dataFi = null;
            String descripcio;
            String nombrerepeticions;
            String nombreseries;

                dataInici = dataIniciTimestamp.toDate();
                dataFi = dataFiTimestamp.toDate();
                // Continúa con la lógica de tu aplicación aquí
                if (currentDate.after(dataInici) && currentDate.before(dataFi)) {
                    // La fecha actual está entre Data Inici y Data Fi
                    // Mostrar los ejercicios o realizar la lógica necesaria
                    descripcio = exer.get("Tipus d'exercici") + " - "+exer.get("Nom de l'exercici").toString();
                    nombrerepeticions = exer.get("Repeticions").toString();
                    nombreseries = exer.get("Número de series").toString();
                    nomExercici.setText(descripcio+"SIIIIII");
                    numRepet.setText(nombrerepeticions);
                    numSeries.setText(nombreseries);

                }else {
                    numtodate = numtodate -1;
                }
                System.out.println("EEEEEEEE"+dataInici+"hellooo"+dataFi);
            // En tu método onBindViewHolder, cuando necesites notificar cambios



            // Establece el texto del TextView nomExercici con la descripción del ejercicio
//            descripcio = exer.get("Tipus d'exercici") + " - "+exer.get("Nom de l'exercici").toString();
//            nombrerepeticions = exer.get("Repeticions").toString();
//            nombreseries = exer.get("Número de series").toString();
            urlVideo = exer.get("URL Vídeo explicatiu").toString();
//            nomExercici.setText(descripcio+"NOOOOOO");
//            numRepet.setText(nombrerepeticions);
//            numSeries.setText(nombreseries);

            //Declaramos el holder que abrirá el Fragment que nos mostrará el video.
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoDialogFragment dialogFragment = new VideoDialogFragment(urlVideo);
                    dialogFragment.show(getParentFragmentManager(),"fragment_video_dialog");
                }
            });
        }
        @Override
        public int getItemCount() {
            numExercicis = numtodate;
            return numExercicis;
        }
    }
}