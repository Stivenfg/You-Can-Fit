// FirstFragment.java
package com.scj.youcanfit.fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
                                //adapter.notifyItemChanged(numExercicis);
                                //Toast.makeText(getContext(),String.valueOf(numExercicis),Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(getContext(),"No existe el documento",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });



        // Find the TextView in the layout
        //TextView btnSwitchFragment = rootView.findViewById(R.id.btnSwitchFragment);
        // Create FragmentSwitcher instance and attach the switcher to the TextView
       // btnSwitchFragment.setOnClickListener(new View.OnClickListener() {
          //  @Override
           // public void onClick(View v) {
                // Replace the current fragment with the "ejercicis" fragment
              //  getFragmentManager().beginTransaction()
                //        .replace(R.id.frameMain, new Ejercicis()) // Use the appropriate ID for your fragment container
                 //       .addToBackStack(null)
               //         .commit();
         //   }
      //  });

//        ImageView videoEjercicios= rootView.findViewById(R.id.video);
//        videoEjercicios.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFragment();
//            }
//        });


        //RECYCLERVIEW
        recyclerView = rootView.findViewById(R.id.recyclerView);
        // Create and set the adapter
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        // Set the layout manager (e.g., LinearLayoutManager or GridLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //CHRONOMETRO
        chrono = rootView.findViewById(R.id.chrono);
        btIniciEjercicios = rootView.findViewById(R.id.buttonInici);
        btIniciEjercicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime();
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
    /*
    public class VideoDialog extends Dialog {

        private VideoView videoView;

        public VideoDialog(@NonNull Context context) {
            super(context);

            setContentView(R.layout.dialog_video);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Obtener referencia del VideoView
            videoView = findViewById(R.id.videoView);

            // Configura el video a reproducir
            String videoPath = "https://www.pexels.com/es-es/video/un-nino-usando-un-lapiz-escribiendo-en-un-papel-dentro-de-un-aula-3209663/"; // Esto puede ser la ruta local o una URL
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoURI(uri);


            // Inicia la reproducción del video
            videoView.start();
        }
    }*/

    //Método para mostrar el fragment del video

    private void openFragment(){
        Ejercicis ejercicis = new Ejercicis();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainerView2,ejercicis);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myButton;
        public ImageView video;

        MyViewHolder(View itemView) {
            super(itemView);
            myButton = itemView.findViewById(R.id.textView);
            video = itemView.findViewById(R.id.videoEj1);
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
