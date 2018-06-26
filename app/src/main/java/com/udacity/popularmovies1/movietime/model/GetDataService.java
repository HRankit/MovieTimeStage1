package com.udacity.popularmovies1.movietime.model;

import com.udacity.popularmovies1.movietime.BuildConfig;
import com.udacity.popularmovies1.movietime.model.details.Details;
import com.udacity.popularmovies1.movietime.model.main.RetroTMDB;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDataService {

    String api_key = BuildConfig.API_KEY;

    @GET("popular?api_key=" + api_key)
    Call<RetroTMDB> getPopularMovies();

    @GET("top_rated?api_key=" + api_key)
    Call<RetroTMDB> getTopRatedMovies();

    @GET("{id}?api_key=" + api_key + "&language=en-US")
    Call<Details> getMovieWithID(@Path("id") String id);

}

