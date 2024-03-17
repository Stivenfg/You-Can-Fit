package com.scj.youcanfit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scj.youcanfit.R;
import com.scj.youcanfit.clasesextra.PuntosAlumne;


import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SecondFragment extends Fragment {

    // RecyclerView para mostrar la lista de clasificación
    private RecyclerView recyclerView;
    // Adaptador para poblar datos en el RecyclerView
    private Ranking_Adapter adapter;
    // Lista para almacenar los datos de clasificación
    private List<Ranking_Item> rankingList;
    public List<Map<String, Object>> mapaAlumne;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;


    public SecondFragment() {
    }

    public SecondFragment(List<PuntosAlumne> puntosAlumnes) {
        System.out.println("PUNTOS ALUMNES RANKING: "+puntosAlumnes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el diseño para este fragmento
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_second, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_ranking);

        rankingList = generateDummyData();
        // Crea un adaptador y pasa la lista de clasificación a él
        adapter = new Ranking_Adapter(rankingList);
        // Configura el RecyclerView con un LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // Establece el adaptador para el RecyclerView
        recyclerView.setAdapter(adapter);
        adapter.ordenarPorPuntuacion();

    }
    private List<Ranking_Item> generateDummyData() {
        //Recuperar semana actual
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semanaActual = localDate.get(weekFields.weekOfYear());


        List<Ranking_Item> dummyData = new ArrayList<>();
        String userDB = user.getDisplayName()+":"+user.getUid();
        db.collection("Usuaris").document(userDB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    List <String> alumnes= new ArrayList<String>();


                    DocumentSnapshot documentAlumne = task.getResult();
                    if (documentAlumne.exists()){
                        db.collection("Puntuaje Usuarios").document(userDB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentPunt = task.getResult();
                                    if (documentPunt.exists()){
                                        db.collection("Usuaris").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot documentNom : task.getResult()){
                                                        alumnes.add(documentNom.getString("Nom"));
                                                    }
                                                }
                                            }
                                        });

                                        db.collection("Puntuaje Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    int i =0;
                                                    for (QueryDocumentSnapshot documentPunt : task.getResult()){
                                                        String semanaAct = "Semana "+semanaActual;
                                                        int punts = Integer.parseInt(documentPunt.get("Semana 11").toString());
                                                        dummyData.add(new Ranking_Item(alumnes.get(i), punts));//---------------------------------------------
                                                        adapter.notifyItemChanged(documentPunt.getData().size());
                                                        i++;
                                                    }
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        });

                    }
                }
            }
        });

        return dummyData;
    }
}