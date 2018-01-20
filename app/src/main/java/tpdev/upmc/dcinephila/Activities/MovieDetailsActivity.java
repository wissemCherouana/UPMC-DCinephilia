package tpdev.upmc.dcinephila.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Adapaters.ActorAdapter;
import tpdev.upmc.dcinephila.Adapaters.CommentAdapter;
import tpdev.upmc.dcinephila.Adapaters.ImageAdapter;
import tpdev.upmc.dcinephila.Adapaters.MoviesAdapter;
import tpdev.upmc.dcinephila.Beans.Actor;
import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Comment;
import tpdev.upmc.dcinephila.Beans.ElementList;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.Beans.ListCinephile;
import tpdev.upmc.dcinephila.Beans.Movie;
import tpdev.upmc.dcinephila.Beans.Rate;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.RecyclerTouchListener;
import tpdev.upmc.dcinephila.DesignClasses.Utility;
import tpdev.upmc.dcinephila.R;

public class MovieDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private TextView movie_title, movie_overview, movie_release_date, movie_director, movie_genres,
            movie_runtime, images_text, casting_text, actors_number, images_number, rating_value, seeImageBtn,
            similar_movies_text, similar_movies_number, rate_text, like_text, add_list_text, comments_text,
            comments_number, see_comments, comment;
    private int comments_value = 0;
    private boolean movie_liked_before = false, movie_rated_before = false;
    private float my_rate=0;
    private Spinner my_spinner;
    private ImageButton add_comment_btn;
    private ImageButton likeBtn, rateBtn, addListBtn;
    private RatingBar ratingBar;
    private ImageView movie_poster;
    private RecyclerView castingRecyclerView, imageRecyclerView, similarMoviesRecyclerViewer;
    private ActorAdapter actorAdapter;
    private ImageAdapter imageMovieAdapter;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Actor> actorsList;
    private ArrayList<String> imagesList, videosList;
    private ArrayList<Movie> similarMoviesList;
    private TagGroup mSmallTagGroup;
    private static String TAG = MovieDetailsActivity.class.getSimpleName();
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private DatabaseReference moviesCommentsReference, cinephilesReference, likedElementsReference, ratedMoviesReference;
    private FirebaseDatabase DCinephiliaInstance;
    private ListView comments_listview;
    private ArrayList<Comment> comments_list;
    private CommentAdapter commentAdapter;
    private View DialogView;
    private ArrayAdapter<String> dataAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    final String userRecord = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    final String userUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<String> list;
    private String spinner_text;
    private String url_movie;
    private String release_date="";
    private String movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        final int movie_id = intent.getIntExtra("movie_id", 0);
        Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");

        DCinephiliaInstance = FirebaseDatabase.getInstance();

        movie_title = (TextView) findViewById(R.id.movie_title);
        movie_overview = (TextView) findViewById(R.id.movie_overview);
        movie_release_date = (TextView) findViewById(R.id.date_release);
        movie_director = (TextView) findViewById(R.id.director);
        movie_genres = (TextView) findViewById(R.id.movie_genres);
        movie_runtime = (TextView) findViewById(R.id.movie_runtime);
        movie_poster = (ImageView) findViewById(R.id.poster);
        casting_text = (TextView) findViewById(R.id.casting_text);
        images_text = (TextView) findViewById(R.id.images_text);
        similar_movies_text = (TextView) findViewById(R.id.similar_movies_text);
        actors_number = (TextView) findViewById(R.id.actors_number);
        images_number = (TextView) findViewById(R.id.images_number);
        similar_movies_number = (TextView) findViewById(R.id.similar_movies_number);
        rating_value = (TextView) findViewById(R.id.rating_value);
        rate_text = (TextView) findViewById(R.id.rate_text);
        like_text = (TextView) findViewById(R.id.like_text);
        add_list_text = (TextView) findViewById(R.id.add_list_text);
        comments_text = (TextView) findViewById(R.id.comments_text);
        comments_number = (TextView) findViewById(R.id.comments_number);
        see_comments = (TextView) findViewById(R.id.seeComments);
        comment = (TextView) findViewById(R.id.comment);
        castingRecyclerView = (RecyclerView) findViewById(R.id.casting_recyclerview);
        imageRecyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        similarMoviesRecyclerViewer = (RecyclerView) findViewById(R.id.similar_movies_recyclerview);
        ratingBar = (RatingBar) findViewById(R.id.RatingBar);
        seeImageBtn = (TextView) findViewById(R.id.seeImages);
        likeBtn = (ImageButton) findViewById(R.id.like_movie);
        rateBtn = (ImageButton) findViewById(R.id.rate_movie);
        addListBtn = (ImageButton) findViewById(R.id.add_list);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        mSmallTagGroup = (TagGroup) findViewById(R.id.tag_group);
        add_comment_btn = (ImageButton) findViewById(R.id.btn_add_comment);
        comments_listview = (ListView) findViewById(R.id.comments_list);


        movie_title.setTypeface(face_bold);
        movie_overview.setTypeface(face);
        movie_release_date.setTypeface(face);
        movie_release_date.setTypeface(face);
        movie_director.setTypeface(face);
        movie_genres.setTypeface(face);
        movie_runtime.setTypeface(face);
        actors_number.setTypeface(face);
        images_number.setTypeface(face);
        similar_movies_number.setTypeface(face);
        rating_value.setTypeface(face);
        like_text.setTypeface(face);
        rate_text.setTypeface(face);
        add_list_text.setTypeface(face);
        comments_number.setTypeface(face);
        comments_text.setTypeface(face_bold);
        see_comments.setTypeface(face_bold);
        comment.setTypeface(face);
        seeImageBtn.setTypeface(face_bold);
        casting_text.setTypeface(face_bold);
        images_text.setTypeface(face_bold);
        similar_movies_text.setTypeface(face_bold);

        actorsList = new ArrayList<>();
        imagesList = new ArrayList<>();
        videosList = new ArrayList<>();
        similarMoviesList = new ArrayList<>();
        actorAdapter = new ActorAdapter(this, actorsList);
        imageMovieAdapter = new ImageAdapter(this, imagesList);
        moviesAdapter = new MoviesAdapter(this, similarMoviesList);

        RecyclerView.LayoutManager CastLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        castingRecyclerView.setLayoutManager(CastLayoutManager);
        castingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castingRecyclerView.setAdapter(actorAdapter);
        castingRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), castingRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Actor actor = actorsList.get(position);
                Intent intent = new Intent(getApplicationContext(), StarBiographyActivity.class);
                intent.putExtra("person_id", actor.getActor_id());
                intent.putExtra("person_name", actor.getActor_name());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        RecyclerView.LayoutManager ImagesLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(ImagesLayoutManager);
        imageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerView.setAdapter(imageMovieAdapter);

        RecyclerView.LayoutManager SimilarMoviesLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        similarMoviesRecyclerViewer.setLayoutManager(SimilarMoviesLayoutManager);
        similarMoviesRecyclerViewer.setItemAnimator(new DefaultItemAnimator());
        similarMoviesRecyclerViewer.setAdapter(moviesAdapter);
        similarMoviesRecyclerViewer.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), similarMoviesRecyclerViewer, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = similarMoviesList.get(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getMovie_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        seeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailsActivity.this, ImagesSliderActivity.class);
                intent.putExtra("element_id", movie_id);
                intent.putExtra("element_title", movie_title.getText().toString());
                intent.putExtra("element_genre", "movie");
                startActivity(intent);
            }
        });

        Resources r = getResources();
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        if (!MovieIsLikedBeforeByTheCinephile(movie_id)) {
            likeBtn.setBackgroundResource(R.drawable.button_shape);
            likeBtn.setImageResource(R.drawable.thumbs_up);
            likeBtn.setPadding(Math.round(px), Math.round(px), Math.round(px), Math.round(px));
            likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MovieIsLikedBeforeByTheCinephile(movie_id)) {
                    LikeMovie("cinephiles_movies_likes", movie_id);
                } else
                    removeLikeFromMovie(movie_id);

            }
        });

        if (!MovieIsRatedBeforeByTheCinephile(movie_id)) {
            rateBtn.setBackgroundResource(R.drawable.button_shape);
            rateBtn.setImageResource(R.drawable.star2);
            rateBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
            rateBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            rate_text.setText("");
        }

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(MovieDetailsActivity.this);
                View DialogView = li.inflate(R.layout.rate_dialog, null);

                final RatingBar ratingBar = (RatingBar) DialogView.findViewById(R.id.RatingBar);
                final TextView rate_value = (TextView) DialogView.findViewById(R.id.rate);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        rate_value.setText(String.valueOf(ratingBar.getRating()));
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MovieDetailsActivity.this);

                alertDialogBuilder.setView(DialogView);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        my_rate = ratingBar.getRating();
                                        RateMovie("cinephiles_movies_rates", movie_id);
                                        rateBtn.setBackgroundResource(R.drawable.button_shape_blue);
                                        rateBtn.setImageResource(R.drawable.star_blue);
                                        rateBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                                        rateBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        rate_text.setText(String.valueOf(my_rate)+"/10");
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        addListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addListBtn.setBackgroundResource(R.drawable.button_shape_blue);
                addListBtn.setImageResource(R.drawable.like_blue);
                addListBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                addListBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LayoutInflater li = LayoutInflater.from(MovieDetailsActivity.this);
                DialogView = li.inflate(R.layout.addlist_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MovieDetailsActivity.this);
                alertDialogBuilder.setView(DialogView);
                addNewSpinnerItem();
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        spinner_text= my_spinner.getSelectedItem().toString();
                                        addElementList(movie_id,spinner_text);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment.getText().equals(""))
                {
                    AddCommentAboutMovie(comment.getText().toString(), movie_id);
                    comment.setText("");
                }
            }
        });

        see_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailsActivity.this, DisplayElementCommentsActivity.class);
                intent.putExtra("element_id", movie_id);
                intent.putExtra("element_genre", "movie");
                startActivity(intent);
            }
        });

        GetMovieDetails(ThemoviedbApiAccess.AllMovieDetailsURL(movie_id));
        GetMovieImages(ThemoviedbApiAccess.MovieBackdrops(movie_id));
        GetSimilarMovies(ThemoviedbApiAccess.MovieRecommendationsURl(movie_id));
        GetMovieVideos(ThemoviedbApiAccess.GetMovieTrailer(movie_id));
        DisplayComments(movie_id);

    }

    private void GetMovieDetails(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    movie=response.getString("title");
                    movie_title.setText(response.getString("title"));

                    try {
                        if (response.getString("release_date")!=null)
                            release_date = response.getString("release_date");
                    }
                    catch (Exception e){
                        release_date="";
                    }

                    movie_release_date.setText("Sortie : "+ release_date);
                    rating_value.setText(response.getString("vote_average"));
                    ratingBar.setRating(Float.valueOf(response.getString("vote_average")));
                    // Getting movie runtime
                    if (response.getString("runtime").equals("0") || response.getString("runtime").equals("null")  )
                    {
                        movie_runtime.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        int runtime = Integer.valueOf(response.getString("runtime"));
                        int hours = runtime / 60;
                        int minutes = runtime % 60;
                        String minutes_text = String.valueOf(minutes);
                        if (minutes<10) minutes_text = "0"+ String.valueOf(minutes);
                        String runtime_text = String.valueOf(hours)+"h"+minutes_text;
                        movie_runtime.setText("Durée : " + runtime_text);
                    }
                    movie_overview.setText(response.getString("overview"));
                    url_movie="https://image.tmdb.org/t/p/w500" + response.getString("poster_path");
                    Glide.with(getApplicationContext()).load("https://image.tmdb.org/t/p/w500" + response.getString("poster_path")).into(movie_poster);

                    // Getting movie genres
                    ArrayList<String> genres_array = new ArrayList<>();
                    String[] tags;
                    JSONArray genres = response.getJSONArray("genres");
                    for (int i=0; i<genres.length();i++) {
                        JSONObject result_json = (JSONObject) genres.get(i);
                        String genre_name = result_json.getString("name");
                        genres_array.add(genre_name);
                    }
                    tags = new String[genres_array.size()];
                    genres_array.toArray(tags);
                    mSmallTagGroup.setTags(tags);
                    MyTagGroupOnClickListener tgClickListener = new MyTagGroupOnClickListener();
                    mSmallTagGroup.setOnClickListener(tgClickListener);
                    mSmallTagGroup.setOnTagClickListener(mTagClickListener);

                    JSONObject movie_credits = response.getJSONObject("credits");
                    JSONArray movie_cast = movie_credits.getJSONArray("cast");
                    JSONArray movie_crew = movie_credits.getJSONArray("crew");

                    movie_director.setText("De : " + movie_crew.getJSONObject(0).getString("name"));

                    for (int i=0; i<movie_cast.length(); i++)
                    {
                        JSONObject actor_json = (JSONObject) movie_cast.get(i);

                        int actor_id = actor_json.getInt("id");
                        String name = actor_json.getString("name");
                        String profile_picture = "https://image.tmdb.org/t/p/w500" + actor_json.getString("profile_path");
                        String character = actor_json.getString("character");

                        if (!actor_json.getString("profile_path").equals("null"))
                        {
                            actorsList.add(new Actor(actor_id,name,character,profile_picture));
                            actorAdapter.notifyDataSetChanged();
                        }
                    }
                    movie_genres.setText("Avec : " + actorsList.get(0).getActor_name() + " , " + actorsList.get(1).getActor_name());
                    actors_number.setText("•  " +String.valueOf(actorsList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    class MyTagGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //
        }
    }

    private TagGroup.OnTagClickListener mTagClickListener = new TagGroup.OnTagClickListener() {
        @Override
        public void onTagClick(String tag) {
            int genre_id=0;
            if (tag.equals("Action")) genre_id = 28;
            if (tag.equals("Aventure")) genre_id = 12;
            if (tag.equals("Animation")) genre_id = 16;
            if (tag.equals("Comédie")) genre_id = 35;
            if (tag.equals("Crime")) genre_id = 80;
            if (tag.equals("Documentaire")) genre_id = 99;
            if (tag.equals("Drame")) genre_id = 18;
            if (tag.equals("Familial")) genre_id = 10751;
            if (tag.equals("Fantastique")) genre_id = 14;
            if (tag.equals("Histoire")) genre_id = 36;
            if (tag.equals("Horreur")) genre_id = 27;
            if (tag.equals("Musique")) genre_id = 10402;
            if (tag.equals("Mystère")) genre_id = 9648;
            if (tag.equals("Romance")) genre_id = 10749;
            if (tag.equals("Science-Fiction")) genre_id = 878;
            if (tag.equals("Téléfilm")) genre_id = 10770;
            if (tag.equals("Thriller")) genre_id = 53;
            if (tag.equals("Guerre")) genre_id = 10752;
            if (tag.equals("Western")) genre_id = 37;


            Intent intent = new Intent(MovieDetailsActivity.this, MoviesByGenreActivity.class);
            intent.putExtra("genre_id",genre_id);
            intent.putExtra("genre_name", tag);
            startActivity(intent);

        }
    };


    private void GetMovieVideos(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray movie_videos = (JSONArray) response.getJSONArray("results");
                    for (int i=0; i<movie_videos.length(); i++)
                    {
                        JSONObject video_json = (JSONObject) movie_videos.get(i);
                        String video_url = video_json.getString("key");
                        videosList.add(video_url);
                    }
                    youTubeView.initialize("AIzaSyB8BGDlSvQQpezJ2dneya5JP7Qxogt7Fb4", MovieDetailsActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void GetMovieImages(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray movie_backdrops = (JSONArray) response.getJSONArray("backdrops");
                    for (int i=0; i<movie_backdrops.length(); i++)
                    {
                        JSONObject image_json = (JSONObject) movie_backdrops.get(i);
                        String image_url = "https://image.tmdb.org/t/p/w780" + image_json.getString("file_path");

                        imagesList.add(image_url);
                        imageMovieAdapter.notifyDataSetChanged();
                    }
                    images_number.setText("•  " +String.valueOf(imagesList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void GetSimilarMovies(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray results = response.getJSONArray("results");
                    for (int i=0; i<results.length();i++)
                    {
                        JSONObject movie_json = (JSONObject) results.get(i);

                        int movie_id = movie_json.getInt("id");
                        String title = movie_json.getString("title");
                        String poster = "https://image.tmdb.org/t/p/w500" + movie_json.getString("poster_path");
                        String release_date="";
                        try {
                            if (movie_json.getString("release_date")!=null)
                                release_date = movie_json.getString("release_date");
                        }
                        catch (Exception e){
                            release_date="";
                        }
                        if(!movie_json.getString("poster_path").equals("null"))
                        {
                            similarMoviesList.add(new Movie(movie_id,title,release_date,poster));
                            moviesAdapter.notifyDataSetChanged();
                        }

                    }
                    Collections.sort(similarMoviesList, new Comparator<Movie>() {
                        public int compare(Movie o1, Movie o2) {
                            if (o1.getDateTime() == null || o2.getDateTime() == null)
                                return 0;
                            return o2.getDateTime().compareTo(o1.getDateTime());
                        }
                    });
                    moviesAdapter.notifyDataSetChanged();
                    similar_movies_number.setText("•  " +String.valueOf(similarMoviesList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
                if (videosList.size()!=0) player.cueVideo(videosList.get(0));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyB8BGDlSvQQpezJ2dneya5JP7Qxogt7Fb4", this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    public void AddCommentAboutMovie(final String comment_content, final int movie_id)
    {
        cinephilesReference = DCinephiliaInstance.getReference("cinephiles");
        cinephilesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot snapshot_next = iterator.next();
                    Cinephile cinephile = snapshot_next.getValue(Cinephile.class);

                    if (cinephile.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        String thumbnail = "";
                        if (cinephile.getSexe().equals("Femme"))
                            thumbnail = "https://icon-icons.com/icons2/582/PNG/512/girl_icon-icons.com_55043.png";
                        else thumbnail = "https://cdn1.iconfinder.com/data/icons/user-pictures/100/boy-512.png";
                        moviesCommentsReference = DCinephiliaInstance.getReference("movies_comments");
                        Comment comment = new Comment(comment_content,new Date(), cinephile.getFirstname() +
                                " " + cinephile.getLastname().toUpperCase(), movie_id, thumbnail);
                        moviesCommentsReference.child(String.valueOf(movie_id)).push().setValue(comment);
                    }
                }
                DisplayComments(movie_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void DisplayComments(int movie_id)
    {
        final ArrayList<Comment> comments = new ArrayList<Comment>();
        moviesCommentsReference = DCinephiliaInstance.getReference("movies_comments/"+String.valueOf(movie_id));
        moviesCommentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments_value = 0;
                comments_list = new ArrayList<>();
                commentAdapter = new CommentAdapter(getApplicationContext(), R.layout.comment_row, comments_list);
                comments_listview.setAdapter(commentAdapter);
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        comments_value ++;
                        Comment comment = singleSnapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                    comments_number.setText("•  " +String.valueOf(comments_value));
                }
                Collections.sort(comments, new Comparator<Comment>() {
                    public int compare(Comment o1, Comment o2) {
                        if (o1.getComment_date() == null || o2.getComment_date() == null)
                            return 0;
                        return o2.getComment_date().compareTo(o1.getComment_date());
                    }
                });
                for (int i=0; i<comments.size();i++)
                {
                    if (comments_list.size() <3)
                        {
                            comments_list.add(comments.get(i));
                            commentAdapter.notifyDataSetChanged();
                        }
                }

                Utility.setListViewHeightBasedOnChildren(comments_listview);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void LikeMovie(final String element_genre, final int element_id)
    {
        cinephilesReference = DCinephiliaInstance.getReference("cinephiles");
        cinephilesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot snapshot_next = iterator.next();
                    Cinephile cinephile = snapshot_next.getValue(Cinephile.class);

                    if (cinephile.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        likedElementsReference = DCinephiliaInstance.getReference(element_genre);
                        Like like = new Like(cinephile.getEmail(), element_id,
                                movie_title.getText().toString(), url_movie, release_date);
                        likedElementsReference.child(String.valueOf(element_id)).push().setValue(like);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void RateMovie(final String element_genre, final int element_id)
    {
        cinephilesReference = DCinephiliaInstance.getReference("cinephiles");
        cinephilesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot snapshot_next = iterator.next();
                    Cinephile cinephile = snapshot_next.getValue(Cinephile.class);

                    if (cinephile.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        ratedMoviesReference = DCinephiliaInstance.getReference(element_genre);
                        Rate rating  = new Rate(cinephile.getEmail(), element_id, my_rate, movie, url_movie);
                        ratedMoviesReference.child(String.valueOf(element_id)).push().setValue(rating);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public boolean MovieIsLikedBeforeByTheCinephile(int movie_id)
    {
        likedElementsReference = DCinephiliaInstance.getReference("cinephiles_movies_likes/"+String.valueOf(movie_id));
        likedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movie_liked_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Like like = singleSnapshot.getValue(Like.class);

                        if (like.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            movie_liked_before = true;

                            Resources r = getResources();
                            final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

                            likeBtn.setBackgroundResource(R.drawable.button_shape_blue);
                            likeBtn.setImageResource(R.drawable.thumbs_up_blue);
                            likeBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                            likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        return movie_liked_before;
    }

    public void removeLikeFromMovie(final int show_id)
    {

        likedElementsReference = DCinephiliaInstance.getReference("cinephiles_movies_likes/"+String.valueOf(show_id));
        likedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movie_liked_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Like like = singleSnapshot.getValue(Like.class);

                        if (like.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            movie_liked_before = false;

                            Resources r = getResources();
                            final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

                            likeBtn.setBackgroundResource(R.drawable.button_shape);
                            likeBtn.setImageResource(R.drawable.thumbs_up);
                            likeBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                            likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            likedElementsReference.child(singleSnapshot.getKey()).removeValue();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public boolean MovieIsRatedBeforeByTheCinephile(int movie_id)
    {
        ratedMoviesReference = DCinephiliaInstance.getReference("cinephiles_movies_rates/"+String.valueOf(movie_id));
        ratedMoviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movie_rated_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Rate rate = singleSnapshot.getValue(Rate.class);

                        if (rate.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            movie_rated_before = true;

                            Resources r = getResources();
                            final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

                            rateBtn.setBackgroundResource(R.drawable.button_shape_blue);
                            rateBtn.setImageResource(R.drawable.star_blue);
                            rateBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                            rateBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            rate_text.setText(String.valueOf(rate.getRating_value()));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        return movie_rated_before;
    }

    protected void addNewSpinnerItem(){
        list = new ArrayList<String>();
        mFirebaseDatabase = mFirebaseInstance.getInstance().getReference("lists_cinephile");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for(DataSnapshot c : child.getChildren()){
                        if(c.getValue().equals(userRecord)){
                            ListCinephile cinp = child.getValue(ListCinephile.class);
                            list.add(cinp.getTitle());
                            dataAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        my_spinner = (Spinner) DialogView.findViewById(R.id.spinner2);
        dataAdapter = new ArrayAdapter<String>(MovieDetailsActivity.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spinner.setAdapter(dataAdapter);
        my_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {

                String item = String.valueOf(adapterView.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub)

            }
        });
      /*  my_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

               spinner_text=my_spinner.getSelectedItem().toString();
               System.out.println(" the spinner "+ spinner_text);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });*/

    }

    public void addElementList(final int movie_id, final String spinner_text){
        // gerer les doublons ici
        mFirebaseDatabase = mFirebaseInstance.getInstance().getReference("elements_lists");
        ElementList element_list = new ElementList(String.valueOf(movie_id),"movie",url_movie,movie,release_date,spinner_text,userRecord);

        // pushing cinephile to 'cinephiles' node using the cinephileId
        // mFirebaseDatabase.child(email ).setValue(cinephile);
        mFirebaseDatabase.child(userUid).push().setValue(element_list);

    }

}
