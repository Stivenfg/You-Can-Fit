// FirstFragment.java
package com.scj.youcanfit.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scj.youcanfit.R;

import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private VideoView video;
    private int numExercicis;
    private String nomExercici;
    private ArrayList arrayexercicis;
    private RecyclerView recyclerView;
    MyAdapter adapter;
    FirebaseFirestore db;
    private Map<String,Object> exercicis;

    //CHRONOMETRO
    CountDownTimer timer;
    private ImageView btIniciEjercicios;
    private TextView chrono;



    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        db = FirebaseFirestore.getInstance();
        db.collection("Reptes").document("Exercicis").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                numExercicis = document.getData().size();
                                exercicis = document.getData();
                                System.out.println(exercicis);
                                for (int i = 0; i < exercicis.size()-1; i++) {
                                    System.out.println("_____"+exercicis.get(i));
                                }
                                System.out.println(exercicis);
                                //adapter.notifyItemChanged(numExercicis);
                                //Toast.makeText(getContext(),String.valueOf(numExercicis),Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(getContext(),"No existe el documento",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });


        //RECYCLERVIEW
        recyclerView = rootView.findViewById(R.id.recyclerView);
        // Create and set the adapter
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        // Set the layout manager (e.g., LinearLayoutManager or GridLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //ELEMENTOS CHRONOMETRO
        chrono = rootView.findViewById(R.id.chrono);
        btIniciEjercicios = rootView.findViewById(R.id.buttonInici);
        btIniciEjercicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTime();
                //btIniciEjercicios.setEnabled(false);
            }
        });

        return rootView;
    }

    //METODO DEL CHRONOMETRO
    private void startTime(){
        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished/ 1000)/3600;
                long minutes = ((millisUntilFinished / 1000) % 3600) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
                chrono.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                chrono.setText("00:00:00");
                Toast.makeText(getContext(), "Tiempo agotado", Toast.LENGTH_SHORT).show();

            }
        }.start();
    }


    //METODO PARA MOSTRAR EL FRAGMENT DEL VIDEO

    private void openFragment(){
        Ejercicis ejercicis = new Ejercicis();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainerView2,ejercicis);
        transaction.addToBackStack(null);
        transaction.commit();
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
            myButton = itemView.findViewById(R.id.textView);
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


            // Initialize other views as needed
        }

    }



    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        // Implement the necessary methods: onCreateViewHolder, onBindViewHolder, getItemCount

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            //Declaramos el holder que abrirá el Fragment que nos mostrará el video.
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView2, new Ejercicis());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            // Bind data to views based on position
        }


        @Override
        public int getItemCount() {

            return numExercicis;
        }
    }




}
