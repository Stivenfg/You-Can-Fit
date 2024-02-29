package com.scj.youcanfit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scj.youcanfit.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends Fragment {

    // RecyclerView para mostrar la lista de clasificación
    private RecyclerView recyclerView;

    // Adaptador para poblar datos en el RecyclerView
    private Ranking_Adapter adapter;

    // Lista para almacenar los datos de clasificación
    private List<Ranking_Item> rankingList;

    /*
        Constructor predeterminado para SecondFragment.
        Es un constructor vacío como se requiere para Fragment.
    */
    public SecondFragment() {
        // Constructor público vacío requerido
    }

    /*
        Sobrescribe el método onCreateView para inflar el diseño de este fragmento.
        @param inflater: LayoutInflater para inflar el diseño
        @param container: ViewGroup contenedor para el diseño
        @param savedInstanceState: Bundle para guardar el estado de la instancia
        @return: Vista inflada para el fragmento
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el diseño para este fragmento
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    /*
        Sobrescribe el método onViewCreated para realizar acciones después de crear la vista.
        En este caso, inicializa el RecyclerView, genera datos de ejemplo y configura el adaptador.
        @param view: La vista creada para el fragmento
        @param savedInstanceState: Bundle para guardar el estado de la instancia
    */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Encuentra el RecyclerView desde la vista inflada
        recyclerView = view.findViewById(R.id.recycler_view_ranking);

        // Genera datos de ejemplo para la lista de clasificación
        rankingList = generateDummyData();

        // Crea un adaptador y pasa la lista de clasificación a él
        adapter = new Ranking_Adapter(rankingList);

        // Configura el RecyclerView con un LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Establece el adaptador para el RecyclerView
        recyclerView.setAdapter(adapter);
    }
    private List<Ranking_Item> generateDummyData() {
        List<Ranking_Item> dummyData = new ArrayList<>();
        dummyData.add(new Ranking_Item("Jugador 1", 1000, 1));
        dummyData.add(new Ranking_Item("Jugador 2", 900, 2));
        dummyData.add(new Ranking_Item("Jugador 3", 800, 3));
        // Agrega más datos según sea necesario
        return dummyData;
    }
}