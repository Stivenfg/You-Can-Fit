package com.scj.youcanfit.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.scj.youcanfit.R;

import java.util.List;

public class Ranking_Adapter extends RecyclerView.Adapter<Ranking_Adapter.ViewHolder> {

    // Lista que contiene datos de clasificación
    private List<Ranking_Item> rankingList;

    /*
        Clase interna ViewHolder para representar la vista de cada elemento en el RecyclerView.
    */
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Elementos de la vista para mostrar información del Ranking_Item
        public TextView playerNameTextView;
        public TextView scoreTextView;
        public TextView posicionTextView;

        /*
            Constructor ViewHolder para inicializar las vistas.
        */
        public ViewHolder(View view) {
            super(view);
            playerNameTextView = view.findViewById(R.id.text_view_player_name);
            scoreTextView = view.findViewById(R.id.text_view_score);
            posicionTextView = view.findViewById(R.id.text_view_posicion);
        }
    }

    /*
        Constructor de la clase Ranking_Adapter que recibe la lista de clasificación.
    */
    public Ranking_Adapter(List<Ranking_Item> rankingList) {
        this.rankingList = rankingList;
    }

    /*
        Sobrescribe el método onCreateViewHolder para inflar la vista de cada elemento en el RecyclerView.
    */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_ranking, parent, false);

        return new ViewHolder(itemView);
    }

    /*
        Sobrescribe el método onBindViewHolder para establecer los datos en cada elemento del RecyclerView.
    */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ranking_Item item = rankingList.get(position);
        holder.playerNameTextView.setText(item.getPlayerName());
        holder.scoreTextView.setText(String.valueOf(item.getScore()));
        holder.posicionTextView.setText(String.valueOf(item.getPosition()));
    }

    /*
        Sobrescribe el método getItemCount para devolver la cantidad de elementos en la lista de clasificación.
    */
    @Override
    public int getItemCount() {
        return rankingList.size();
    }
}
