package com.udacity.popularmovies1.movietime.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies1.movietime.R;
import com.udacity.popularmovies1.movietime.model.details.Genre;

import java.util.List;

public class GenreChipsRecyclerViewAdapter extends RecyclerView.Adapter<GenreChipsRecyclerViewAdapter.MyViewHolder> {

    private final List<Genre> genreList;


    public GenreChipsRecyclerViewAdapter(List<Genre> genreList) {
        this.genreList = genreList;
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_chips_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText(genreList.get(position).getName());

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }


}
