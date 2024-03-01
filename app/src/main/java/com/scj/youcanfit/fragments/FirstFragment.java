// FirstFragment.java
package com.scj.youcanfit.fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scj.youcanfit.R;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private VideoView video;
    private int numExercicis;
    private RecyclerView recyclerView;
    MyAdapter adapter;
    FirebaseFirestore db;

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
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                numExercicis = document.getData().size();
                                adapter.notifyItemChanged(numExercicis);
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

//        ImageView videoEjercicios= rootView.findViewById(R.id.videoEj1);
//        videoEjercicios.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFragment();
//            }
//        });

        recyclerView = rootView.findViewById(R.id.recyclerView);

        // Create and set the adapter
         adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        // Set the layout manager (e.g., LinearLayoutManager or GridLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

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
    }

    //Método para mostrar el video

    private void openFragment(){
        Ejercicis ejercicis = new Ejercicis();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainerView2,ejercicis);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myButton;

        MyViewHolder(View itemView) {
            super(itemView);
            myButton = itemView.findViewById(R.id.textView);
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
            // Bind data to views based on position
        }


        @Override
        public int getItemCount() {

            return numExercicis;
        }
    }
}
