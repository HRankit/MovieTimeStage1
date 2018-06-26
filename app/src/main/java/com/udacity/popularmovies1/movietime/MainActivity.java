package com.udacity.popularmovies1.movietime;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.udacity.popularmovies1.movietime.adapter.MoviePosterRecyclerViewAdapter;
import com.udacity.popularmovies1.movietime.model.GetDataService;
import com.udacity.popularmovies1.movietime.model.main.RetroTMDB;

import com.udacity.popularmovies1.movietime.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int POPULAR_MOVIES = 1;
    private static final int TOP_RATED_MOVIES = 2;
    private static int MOVIE_SHOWN = 1;
    private static String WHICH_CATEGORY_SHOWN = "isItPopularOrTop";
    private ProgressDialog progressDialog;
    private TextView no_network_tv;
    private ImageView no_network_image;
    private Button retry_button;
    private RecyclerView recyclerView;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(WHICH_CATEGORY_SHOWN, MOVIE_SHOWN);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MOVIE_SHOWN = savedInstanceState.getInt(WHICH_CATEGORY_SHOWN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        no_network_image = findViewById(R.id.no_network_image);
        no_network_tv = findViewById(R.id.no_network_tv);
        retry_button = findViewById(R.id.retry_button);

        checkNet();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                MOVIE_SHOWN = POPULAR_MOVIES;
                performRequestLoadUI(POPULAR_MOVIES);
                return true;
            case R.id.top_rated:
                MOVIE_SHOWN = TOP_RATED_MOVIES;
                performRequestLoadUI(TOP_RATED_MOVIES);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkNet() {
        if (isNetworkAvailable(this)) {
            manipulateViews(true);

            performRequestLoadUI(MOVIE_SHOWN);

        } else {
            manipulateViews(false);
        }
    }


    private void performRequestLoadUI(int whichRequest) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<RetroTMDB> call;
        if (whichRequest == POPULAR_MOVIES) {
            call = service.getPopularMovies();
        } else {
            call = service.getTopRatedMovies();
        }

        call.enqueue(new Callback<RetroTMDB>() {
            @Override
            public void onResponse(@NonNull Call<RetroTMDB> call, @NonNull Response<RetroTMDB> response) {
                progressDialog.dismiss();
                assert response.body() != null;
                generateDataList(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RetroTMDB> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_SHORT).show();
                manipulateViews(false);
            }


        });
    }

    private void generateDataList(RetroTMDB photoList) {
        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2);
        } else {
            mLayoutManager = new GridLayoutManager(this, 4);
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        MoviePosterRecyclerViewAdapter adapter = new MoviePosterRecyclerViewAdapter(MainActivity.this, photoList.getResults());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public void isNetAvailable(View view) {
        checkNet();
    }

    private void manipulateViews(Boolean isItNet) {
        if (isItNet) {
            recyclerView.setVisibility(View.VISIBLE);
            no_network_image.setVisibility(View.GONE);
            no_network_tv.setVisibility(View.GONE);
            retry_button.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_network_image.setVisibility(View.VISIBLE);
            no_network_tv.setVisibility(View.VISIBLE);
            retry_button.setVisibility(View.VISIBLE);
        }
    }
}
