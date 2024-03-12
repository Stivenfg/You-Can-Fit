package com.scj.youcanfit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scj.youcanfit.R;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    // RecyclerView para mostrar la lista de clasificación
    private RecyclerView recyclerView;
    // Adaptador para poblar datos en el RecyclerView
    private Ranking_Adapter adapter;
    // Lista para almacenar los datos de clasificación
    private List<Ranking_Item> rankingList;

    public SecondFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el diseño para este fragmento
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
        List<Ranking_Item> dummyData = new ArrayList<>();
        dummyData.add(new Ranking_Item("Alumne 1", 74 ));
        dummyData.add(new Ranking_Item("Alumne 2",37));
        dummyData.add(new Ranking_Item("Alumne 3", 73 ));
        dummyData.add(new Ranking_Item("Alumne 4",54));
        dummyData.add(new Ranking_Item("Alumne 5",12));
        dummyData.add(new Ranking_Item("Alumne 6",49));
        dummyData.add(new Ranking_Item("Alumne 7",80));


        // Agrega más datos según sea necesario
        return dummyData;
    }
}