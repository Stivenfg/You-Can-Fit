package com.scj.youcanfit.fragments;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scj.youcanfit.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ranking_Adapter extends RecyclerView.Adapter<Ranking_Adapter.ViewHolder> {

    // Lista que contiene datos de clasificación
    private List<Ranking_Item> rankingList;

    FirebaseFirestore db;
    FirebaseUser user;


    public void ordenarPorPuntuacion(){
        Collections.sort(rankingList, new Comparator<Ranking_Item>() {
            @Override
            public int compare(Ranking_Item o1, Ranking_Item o2) {
                return Integer.compare(o2.getScore(),o1.getScore());
            }
        });
    }
    //Clase interna ViewHolder para representar la vista de cada elemento en el RecyclerView.
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos de la vista para mostrar información del Ranking_Item
        public TextView playerNameTextView;
        public TextView scoreTextView;
        public TextView posicion;
        public ImageView imagen;

        //Constructor ViewHolder para inicializar las vistas.
        public ViewHolder(View view) {
            super(view);
            playerNameTextView = view.findViewById(R.id.text_view_player_name);
            scoreTextView = view.findViewById(R.id.text_view_score);
            posicion = view.findViewById(R.id.text_view_posicion);
            imagen = view.findViewById(R.id.estrella);
        }
    }
    //Constructor de la clase Ranking_Adapter que recibe la lista de clasificación.
    public Ranking_Adapter(List<Ranking_Item> rankingList) {
        this.rankingList = rankingList;
    }
    //Sobrescribe el método onCreateViewHolder para inflar la vista de cada elemento en el RecyclerView.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_ranking, parent, false);

        return new ViewHolder(itemView);
    }
    //Sobrescribe el método onBindViewHolder para establecer los datos en cada elemento del RecyclerView.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ranking_Item item = rankingList.get(position);
        holder.posicion.setText(String.valueOf(position+1));;
        holder.playerNameTextView.setText(item.getPlayerName());
        holder.scoreTextView.setText(String.valueOf(item.getScore()));
        switch (position){
            case 0:
                holder.imagen.setBackgroundColor(Color.parseColor("#FFD700"));
                break;
            case 1:
                holder.imagen.setBackgroundColor(Color.parseColor("#BEBEBE"));
                break;
            case 2:
                holder.imagen.setBackgroundColor(Color.parseColor("#CD7F32"));
                break;

            default:
                holder.imagen.setBackgroundColor(Color.parseColor("#959595"));
                break;
        }
    }
    //Sobrescribe el método getItemCount para devolver la cantidad de elementos en la lista de clasificación.
    @Override
    public int getItemCount() {
        return rankingList.size();
    }
}
