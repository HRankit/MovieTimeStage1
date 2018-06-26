package com.udacity.popularmovies1.movietime.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies1.movietime.DetailsActivity;
import com.udacity.popularmovies1.movietime.R;
import com.udacity.popularmovies1.movietime.model.main.Result;

import java.util.List;


import static com.udacity.popularmovies1.movietime.utils.staticConstants.MOVIE_ID_KEY;
import static com.udacity.popularmovies1.movietime.utils.staticConstants.POSTER_URL_KEY;

public class MoviePosterRecyclerViewAdapter extends RecyclerView.Adapter<MoviePosterRecyclerViewAdapter.MyViewHolder1> {
    private final Context mContext;
    private final List<Result> resultList;
    private int lastPosition = -1;

    public MoviePosterRecyclerViewAdapter(Context context, List<Result> resultList) {
        this.resultList = resultList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainact_poster_item, parent, false);

        return new MoviePosterRecyclerViewAdapter.MyViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder1 holder, @SuppressLint("RecyclerView") final int position) {

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });
        holder.movie_title.setText(resultList.get(position).getOriginalTitle());
        holder.movie_year.setText(resultList.get(position).getReleaseDate());
        holder.movie_rating.setText(resultList.get(position).getVoteAverage());

        holder.movie_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });


        Picasso.get()
                .load(resultList.get(position).getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.loader)
                .into(holder.movie_poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) holder.movie_poster.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@NonNull Palette palette) {
                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                        if (textSwatch == null) {
                                            return;
                                        }
                                        holder.movie_poster_background.setBackgroundColor(textSwatch.getRgb());
                                        holder.movie_title.setTextColor(textSwatch.getTitleTextColor());
                                        holder.movie_year.setTextColor(textSwatch.getBodyTextColor());
                                        holder.movie_rating.setTextColor(textSwatch.getBodyTextColor());
                                    }
                                });
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        setAnimation(holder.card_view, position);
    }

    private void navigate(int position) {

        Activity activity = (Activity) mContext;

        long i = resultList.get(position).getId();
        String posterURL = resultList.get(position).getPosterPath();

        Intent myIntent = new Intent(mContext, DetailsActivity.class);
        myIntent.putExtra(POSTER_URL_KEY, posterURL);
        myIntent.putExtra(MOVIE_ID_KEY, i);

        activity.startActivity(myIntent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView movie_title;
        final TextView movie_year;
        final TextView movie_rating;
        final RelativeLayout movie_poster_background;
        private final CardView card_view;

        MyViewHolder1(View itemView) {
            super(itemView);
            movie_title = itemView.findViewById(R.id.movie_title);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_year = itemView.findViewById(R.id.movie_year);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            movie_poster_background = itemView.findViewById(R.id.movie_poster_background);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }


}
