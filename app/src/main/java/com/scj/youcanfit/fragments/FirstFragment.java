// FirstFragment.java
package com.scj.youcanfit.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scj.youcanfit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

import com.scj.youcanfit.clasesextra.Exercici;
import com.scj.youcanfit.clasesextra.VideoDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment {

    private TextView tituloSemana;
    private RecyclerView recyclerView;
    MyAdapter adapter;
    FirebaseFirestore db;
    private List<Exercici> exercici;

    //CHRONOMETRO
    CountDownTimer timer;
    private ImageView btIniciEjercicios;
    private TextView chrono;
    private String chronoActivo;
    Spinner sp_lugar;
    FirebaseAuth auth;
    FirebaseUser user;
    int semanaActual;
    int puntos=0;

    static boolean chronoEstaActivo;


    //CONSTRUCTOR VACIO DEL FIRST FRAGMENT
    public FirstFragment() {
        // Required empty public constructor

    }

    public FirstFragment(List<Exercici> exercici) {
        this.exercici = exercici;
        int size = exercici.size();
        // ActualizarAdapter(size);
    }


    public void setExercicis(List<Exercici> exercici) {
        this.exercici = exercici;
        System.out.println("pepita " + exercici);
    }

    private void ActualizarAdapter(int size) {
        adapter.notifyItemChanged(size);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        db = FirebaseFirestore.getInstance();
        sp_lugar = rootView.findViewById(R.id.sp_lugar);
        tituloSemana = rootView.findViewById(R.id.tituloSemana);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        semanaActual = localDate.get(weekFields.weekOfYear());

        tituloSemana.setText(tituloSemana.getText().toString() + " " + String.valueOf(semanaActual));


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

                startTime(btIniciEjercicios, 10);

            }
        });
        return rootView;
    }


    //METODO DEL CHRONOMETRO
    private void startTime(ImageView buttonChrono, long tiempoSegundos) {
        timer = new CountDownTimer(TimeUnit.SECONDS.toMillis(tiempoSegundos), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Formulas de pasar de tiempo a milisegundos
                long hours = (millisUntilFinished / 1000) / 3600;
                long minutes = ((millisUntilFinished / 1000) % 3600) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds); // Formato de como queremos que se muestre en el textView
                chrono.setText(timeFormatted);//
                chronoEstaActivo = true;
            }

            @Override
            public void onFinish() {
                chrono.setText("00:00:00");
                Toast.makeText(getContext(), "Tiempo agotado", Toast.LENGTH_SHORT).show();
                buttonChrono.setEnabled(true);
                chronoEstaActivo = false;

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

        String cuentaRep;
        TextView contador;
        Integer contadorRepeticiones = 0;


        MyViewHolder(View itemView) {
            super(itemView);
            myButton = itemView.findViewById(R.id.textView);

            video = itemView.findViewById(R.id.videoEj1);

            //CONTADOR DE REPETICIONES
            contador = itemView.findViewById(R.id.contador);


            //ANIMACION BARRA
            barra = itemView.findViewById(R.id.barra);
            animation = AnimationUtils.loadAnimation(barra.getContext(), R.anim.anima_barra);
            cajaEjercicio = itemView.findViewById(R.id.cajaNomEjercicio);
            cajaEjercicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isMoving == false && chronoEstaActivo == true) {
                        barra.startAnimation(animation);
                        isMoving = true;


                    } else if (isMoving == false && chronoEstaActivo == false){
                        barra.clearAnimation();
                        isMoving = false;

                    }else{
                        barra.clearAnimation();
                        isMoving = false;
                        //Funcionalidad para que se cuente y se muestre el numero de repeticiones de cada ejercicio finalizado.
                        contadorRepeticiones++;
                        cuentaRep = contadorRepeticiones.toString();
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
            // Obtiene el mapa de ejercicio correspondiente a la posición del ítem
            Exercici exer = exercici.get(position);
            String urlVideo;

            String descripcio;
            String nombrerepeticions;
            String nombreseries;
            //Verificamos que el  URL tiene el formato de youtube y que el URL de Exercici no este vacio para mostrar un link falso de error
            String url = exer.getUrlVideo();
            if (!url.isEmpty() && url.contains("https://www.youtube.com/watch?v=")){
                urlVideo = exer.getUrlVideo();
            }else{
                urlVideo = "https://www.youtube.com/watch?v=videoNotFound";
            }

            descripcio = exer.getTipusExercici() + " - " + exer.getNomExercici();
            nombrerepeticions = exer.getRepeticions();
            nombreseries = exer.getSeries();
            nomExercici.setText(descripcio);
            numRepet.setText(nombrerepeticions);
            numSeries.setText(nombreseries);


            //Declaramos el holder que abrirá el Fragment que nos mostrará el video.
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoDialogFragment dialogFragment = new VideoDialogFragment(urlVideo);
                    dialogFragment.show(getParentFragmentManager(), "fragment_video_dialog");
                }
            });
        }

        @Override
        public int getItemCount() {

            return exercici.size();
        }

    }
}