package com.udacity.popularmovies1.movietime;

import android.content.Intent;

import android.graphics.Typeface;
import android.net.Uri;

import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.view.MenuItem;
import android.view.View;

import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.udacity.popularmovies1.movietime.adapter.GenreChipsRecyclerViewAdapter;
import com.udacity.popularmovies1.movietime.model.GetDataService;
import com.udacity.popularmovies1.movietime.model.details.Details;
import com.udacity.popularmovies1.movietime.model.details.Genre;
import com.udacity.popularmovies1.movietime.network.RetrofitClientInstance;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.udacity.popularmovies1.movietime.utils.staticConstants.MOVIE_ID_KEY;
import static com.udacity.popularmovies1.movietime.utils.staticConstants.POSTER_URL_KEY;

public class DetailsActivity extends AppCompatActivity {


    private ImageView coverImage;
    private TextView userRating, budget_tv, imdb_id_tv, revenue_tv, runtime_tv, tagline_tv, vote_count_tv;
    private TextView releaseDate;
    private ImageView movie_poster;
    private TextView movie_overview_tv;
    private RecyclerView genre_chips_layout;
    private TextView movie_title_tv;
    private Transition transition;
    private ConstraintSet constrainSet;
    private ConstraintLayout constraintLayout;
    private Group constraingroup;
    private ProgressBar progressBar;
    private Call<Details> call;
    private TextView homepage_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_act);


        supportPostponeEnterTransition();

        String movie_id = "";
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey("movie_id")) {
            movie_id = extras.get(MOVIE_ID_KEY).toString();
            String url = extras.get(POSTER_URL_KEY).toString();

            movie_poster = findViewById(R.id.movie_poster);
            constraintLayout = findViewById(R.id.constrainLayout);

            constraingroup = findViewById(R.id.constraingroup);


            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.loader)
                    .into(movie_poster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {
                            supportStartPostponedEnterTransition();

                        }

                    });


        } else {
            onBackPressed();
        }

        constrainSet = new ConstraintSet();
        constrainSet.clone(this, R.layout.activity_details_act2);

        transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateInterpolator(1.0f));
        transition.setDuration(1200);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("MovieTime");
        }


        progressBar.setVisibility(View.VISIBLE);

        networkAndPopulateUI(movie_id);
    }

    private void runAnimation() {
        TransitionManager.beginDelayedTransition(constraintLayout, transition);
        constrainSet.applyTo(constraintLayout);
        constraingroup.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void networkAndPopulateUI(String movie_id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        call = service.getMovieWithID(movie_id);
        call.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(@NonNull Call<Details> call, @NonNull Response<Details> response) {
                progressBar.setVisibility(View.GONE);
                generateDataList(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Details> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

                if (call.isCanceled()) {
                    Toast.makeText(DetailsActivity.this, "Request Cancelled by User." + t.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailsActivity.this, "other larger issue, i.e. no network connection?" + t.toString(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(DetailsActivity.this, "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

        });
    }


    private void generateDataList(Details photoList) {
        progressBar.setVisibility(View.GONE);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }

        movie_title_tv.setText(photoList.getOriginalTitle());

        Picasso.get()
                .load(photoList.getBackdropPath())
                .fit()
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_launcher_background)
                .into(coverImage);


        Picasso.get()
                .load(photoList.getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_launcher_background)
                .into(movie_poster);


        releaseDate.setText(photoList.getReleaseDate());
        userRating.setText(photoList.getVoteAverage());
        movie_overview_tv.setText(photoList.getOverview());


        budget_tv.setText(photoList.getBudget());


        imdb_id_tv.setText(getString(R.string.click_here));
        final String imdburl = photoList.getImdbId();
        imdb_id_tv.setTypeface(null, Typeface.BOLD_ITALIC);

        imdb_id_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdburl));
                startActivity(browserIntent);
            }
        });


        final String homepage = photoList.getHomepage();
        homepage_tv.setText(homepage);
        homepage_tv.setTypeface(null, Typeface.BOLD_ITALIC);
        homepage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
                startActivity(browserIntent);
            }
        });


        revenue_tv.setText(photoList.getRevenue());
        runtime_tv.setText(photoList.getRuntime());
        tagline_tv.setText(photoList.getTagline());
        vote_count_tv.setText(photoList.getVoteCount());

        List<Genre> s = photoList.getGenres();

        genre_chips_layout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genre_chips_layout.setAdapter(new GenreChipsRecyclerViewAdapter(s));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runAnimation();

            }
        }, 1300);

    }


    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        coverImage = findViewById(R.id.coverImage);
        movie_title_tv = findViewById(R.id.movie_title_tv);
        releaseDate = findViewById(R.id.release_date_tv);
        userRating = findViewById(R.id.user_rating_tv);
        movie_overview_tv = findViewById(R.id.movie_overview_tv);
        budget_tv = findViewById(R.id.budget_tv);
        imdb_id_tv = findViewById(R.id.imdb_id_tv);
        revenue_tv = findViewById(R.id.revenue_tv);
        runtime_tv = findViewById(R.id.runtime_tv);
        tagline_tv = findViewById(R.id.tagline_tv);
        vote_count_tv = findViewById(R.id.vote_count_tv);
        genre_chips_layout = findViewById(R.id.genre_chips_layout);
        homepage_tv = findViewById(R.id.homepage_tv);

    }


    @Override
    public void onBackPressed() {
        call.cancel();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }
}
