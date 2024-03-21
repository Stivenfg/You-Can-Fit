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

import java.time.LocalDate;
import java.time.temporal.WeekFields;

import com.scj.youcanfit.clasesextra.Exercici;
import com.scj.youcanfit.clasesextra.PuntosAlumne;
import com.scj.youcanfit.clasesextra.VideoDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment {

    private TextView tituloSemana;
    private RecyclerView recyclerView;
    MyAdapter adapter;
    static FirebaseFirestore db;
    private static List<Exercici> exercici;
    //private static List<PuntosAlumne> puntosalumne;
    private static PuntosAlumne puntosalumne;
    private static List<Integer> valoring = new ArrayList<>();
    private static List<Integer> repesexer = new ArrayList<>();

    //CHRONOMETRO
    CountDownTimer timer;
    private ImageView btIniciEjercicios;
    private TextView chrono;
    static String chronoActivo;
    Spinner sp_lugar;
    FirebaseAuth auth;
    static FirebaseUser user;
    static int semanaActual;
    int puntos=0;
    static int valorexer;

    static boolean chronoEstaActivo;


    //CONSTRUCTOR VACIO DEL FIRST FRAGMENT
    public FirstFragment() {
        // Required empty public constructor

    }

    public FirstFragment(List<Exercici> exercici) {
        this.exercici = exercici;
    }


    public void setExercicis(List<Exercici> exercici) {
        this.exercici = exercici;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        if (valoring.size() < exercici.size()) {
            // If repess is smaller, add elements to repess until it matches exercici's size
            while (valoring.size() < exercici.size()) {
                valoring.add(0); // Or any default value you want
                repesexer.add(0);
            }
        } else if (valoring.size() > exercici.size()) {
            // If repess is larger, remove elements from repess until it matches exercici's size
            while (valoring.size() > exercici.size()) {
                valoring.remove(valoring.size() - 1); // Remove the last element
            }
        }
        System.out.println("MEC   VALOREXER___"+valoring.size());
        System.out.println("MEC  REPESEXER__"+repesexer.size());

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
        LinearLayout cajaEjercicio;
        ImageView barra;
        private boolean isMoving = false;
        private Animation animation;

        String cuentaRep;
        TextView contador;
        Integer contadorRepeticiones = 0;
        int position = 0;

        public MyViewHolder(View itemView, int position) {
            super(itemView);
            myButton = itemView.findViewById(R.id.textView);
            video = itemView.findViewById(R.id.videoEj1);
            contador = itemView.findViewById(R.id.contador);

            // Inicializamos las vistas necesarias
            cajaEjercicio = itemView.findViewById(R.id.cajaNomEjercicio);
            barra = itemView.findViewById(R.id.barra);
            animation = AnimationUtils.loadAnimation(barra.getContext(), R.anim.anima_barra);

            // Agregamos el OnClickListener para la caja de ejercicio
            cajaEjercicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getAdapterPosition();
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        if (isMoving == false && chronoEstaActivo == true) {
                            barra.startAnimation(animation);
                            isMoving = true;
                        } else if (isMoving == false && chronoEstaActivo == false) {
                            barra.clearAnimation();
                            isMoving = false;
                        } else {
                            barra.clearAnimation();
                            isMoving = false;
                            contadorRepeticiones++;
                            PuntosAlumne puntos = puntosalumne;
                            int puntsactuals = Integer.parseInt(puntos.getPunts());
                            int valorfinal = contadorRepeticiones * valoring.get(itemPosition);

                            int valorsumado = puntsactuals + valorfinal ;
                            valorsumado = valorsumado + valorfinal;
                            db.collection("Puntuaje Usuarios").document(user.getDisplayName() + ":" + user.getUid()).update("Semana " + semanaActual, String.valueOf(valorsumado));
                            System.out.println("VALORRRRRR" + valorfinal);
                            cuentaRep = contadorRepeticiones.toString();
                            contador.setText(cuentaRep);
                        }
                    }
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
            Exercici exer = exercici.get(position);
            valorexer = Integer.parseInt(exer.getValor());
            valorexer = valorexer;
            valoring.add(position, valorexer);
        }

        public int getItemPosition() {
            return position;
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(itemView, viewType); // Llama al constructor que recibe dos parámetros
            int position = parent.indexOfChild(itemView);
            position = position + 1;
            viewHolder.setPosition(position);
            nomExercici = itemView.findViewById(R.id.nomExercici);
            numRepet = itemView.findViewById(R.id.numRepet);
            numSeries = itemView.findViewById(R.id.numSeries);
            return viewHolder;
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

            // Obtiene el valor del ejercicio
            int valorexer = Integer.parseInt(exer.getValor());
            // Guarda el valor en la lista valoring en la misma posición
            valoring.add(position, valorexer);

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
            System.out.println("MEC MEC MEC___"+position);

            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {

            return exercici.size();
        }

    }
}