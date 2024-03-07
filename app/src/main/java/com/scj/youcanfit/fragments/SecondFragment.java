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
        dummyData.add(new Ranking_Item("Jugador 1", 8 ));
        dummyData.add(new Ranking_Item("Jugador 2", 1 ));
        dummyData.add(new Ranking_Item("Jugador 3", 9 ));
        dummyData.add(new Ranking_Item("Jugador 4",3));
        // Agrega más datos según sea necesario
        return dummyData;
    }
}