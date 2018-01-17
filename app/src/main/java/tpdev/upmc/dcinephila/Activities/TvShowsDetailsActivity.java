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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import me.gujun.android.taggroup.TagGroup;
import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Adapaters.ActorAdapter;
import tpdev.upmc.dcinephila.Adapaters.CommentAdapter;
import tpdev.upmc.dcinephila.Adapaters.ImageAdapter;
import tpdev.upmc.dcinephila.Adapaters.SeasonAdapter;
import tpdev.upmc.dcinephila.Adapaters.TvShowsAdapter;
import tpdev.upmc.dcinephila.Beans.Actor;
import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Comment;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.Beans.Rate;
import tpdev.upmc.dcinephila.Beans.Season;
import tpdev.upmc.dcinephila.Beans.TVshow;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.RecyclerTouchListener;
import tpdev.upmc.dcinephila.DesignClasses.Utility;
import tpdev.upmc.dcinephila.R;

public class TvShowsDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private TextView show_title, show_overview, show_first_air_date, show_created_by, show_number_of_seasons,
            show_number_of_episodes, images_text, casting_text, actors_number, images_number, networks,
            seasons_text, seasons_number, similar_shows_text, similar_shows_number, rating_value, rate_text,
            like_text, add_list_text, comments_text, comments_number, see_comments, seeImageBtn, comment;
    private int comments_value = 0;
    private boolean show_liked_before = false, show_rated_before = false;
    private float my_rate=0;
    private ImageButton likeBtn, rateBtn, addListBtn, add_comment_btn;
    private RatingBar ratingBar;
    private ImageView show_poster;
    private RecyclerView castingRecyclerView, seasonsRecycleView, imageRecyclerView, similarShowsRecyclerViewer;
    private ActorAdapter actorAdapter;
    private ImageAdapter imageShowAdapter;
    private TvShowsAdapter tvshowsAdapter;
    private SeasonAdapter seasonAdapter;
    private ArrayList<Actor> actorsList;
    private ArrayList<String> imagesList, videosList;
    private ArrayList<TVshow> similarShowsList;
    private ArrayList<Season> seasonsList;
    private TagGroup show_genres_tags;
    private static String TAG = TvShowsDetailsActivity.class.getSimpleName();
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private DatabaseReference showsCommentsReference, cinephilesReference,likedElementsReference, ratedTvShowsReference;;
    private FirebaseDatabase DCinephiliaInstance;
    private ListView comments_listview ;
    private ArrayList<Comment> comments_list;
    private CommentAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_shows_details);

        Intent intent = getIntent();
        final int show_id = intent.getIntExtra("show_id",0);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");

        DCinephiliaInstance = FirebaseDatabase.getInstance();

        show_title = (TextView) findViewById(R.id.show_title);
        show_overview = (TextView) findViewById(R.id.show_overview);
        show_first_air_date = (TextView) findViewById(R.id.first_air_date);
        show_created_by = (TextView) findViewById(R.id.created_by);
        show_number_of_episodes = (TextView) findViewById(R.id.number_of_episodes);
        show_number_of_seasons = (TextView) findViewById(R.id.number_of_seasons);
        casting_text = (TextView) findViewById(R.id.casting_text);
        images_text = (TextView) findViewById(R.id.images_text);
        similar_shows_text = (TextView) findViewById(R.id.similar_shows_text);
        actors_number = (TextView) findViewById(R.id.actors_number);
        images_number = (TextView) findViewById(R.id.images_number);
        similar_shows_number = (TextView) findViewById(R.id.similar_shows_number);
        rating_value = (TextView) findViewById(R.id.rating_value);
        rate_text = (TextView) findViewById(R.id.rate_text);
        like_text = (TextView) findViewById(R.id.like_text);
        add_list_text = (TextView) findViewById(R.id.add_list_text);
        comments_text = (TextView) findViewById(R.id.comments_text);
        comments_number = (TextView) findViewById(R.id.comments_number);
        comment = (TextView) findViewById(R.id.comment);
        see_comments = (TextView) findViewById(R.id.seeComments);
        castingRecyclerView = (RecyclerView) findViewById(R.id.casting_recyclerview);
        imageRecyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        seasonsRecycleView = (RecyclerView) findViewById(R.id.seasons_recyclerview);
        similarShowsRecyclerViewer = (RecyclerView) findViewById(R.id.similar_shows_recyclerview);
        ratingBar = (RatingBar) findViewById(R.id.RatingBar);
        seeImageBtn = (TextView) findViewById(R.id.seeImages);
        likeBtn = (ImageButton) findViewById(R.id.like_show);
        rateBtn = (ImageButton) findViewById(R.id.rate_show);
        addListBtn = (ImageButton) findViewById(R.id.add_list);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        show_genres_tags = (TagGroup) findViewById(R.id.tag_group);
        show_poster = (ImageView) findViewById(R.id.poster);
        seasons_number = (TextView) findViewById(R.id.seasons_number);
        seasons_text = (TextView) findViewById(R.id.seasons_text);
        networks = (TextView) findViewById(R.id.networks);
        add_comment_btn = (ImageButton) findViewById(R.id.btn_add_comment);
        comments_listview = (ListView) findViewById(R.id.comments_list);

        show_title.setTypeface(face_bold);
        show_overview.setTypeface(face);
        show_first_air_date.setTypeface(face);
        show_created_by.setTypeface(face);
        show_number_of_episodes.setTypeface(face);
        show_number_of_seasons.setTypeface(face);
        networks.setTypeface(face);
        actors_number.setTypeface(face);
        images_number.setTypeface(face);
        similar_shows_number.setTypeface(face);
        seasons_text.setTypeface(face_bold);
        seasons_number.setTypeface(face);
        rating_value.setTypeface(face);
        like_text.setTypeface(face);
        rate_text.setTypeface(face);
        add_list_text.setTypeface(face);
        comments_number.setTypeface(face);
        comments_text.setTypeface(face_bold);
        comment.setTypeface(face);
        see_comments.setTypeface(face_bold);
        seeImageBtn.setTypeface(face_bold);
        casting_text.setTypeface(face_bold);
        images_text.setTypeface(face_bold);
        similar_shows_text.setTypeface(face_bold);

        actorsList = new ArrayList<>();
        seasonsList = new ArrayList<>();
        imagesList = new ArrayList<>();
        videosList = new ArrayList<>();
        similarShowsList = new ArrayList<>();
        actorAdapter = new ActorAdapter(this, actorsList);
        imageShowAdapter = new ImageAdapter(this, imagesList);
        tvshowsAdapter = new TvShowsAdapter(this, similarShowsList);
        seasonAdapter = new SeasonAdapter(this,seasonsList);

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
                intent.putExtra("person_name",actor.getActor_name());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        RecyclerView.LayoutManager SeasonsLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        seasonsRecycleView.setLayoutManager(SeasonsLayoutManager);
        seasonsRecycleView.setItemAnimator(new DefaultItemAnimator());
        seasonsRecycleView.setAdapter(seasonAdapter);
        seasonsRecycleView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), seasonsRecycleView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), SeasonEpisodesActivity.class);
                intent.putExtra("show_id",show_id);
                intent.putExtra("season_number", seasonsList.get(position).getSeason_number());
                intent.putExtra("season_name", show_title.getText() + " - Saison " + seasonsList.get(position).getSeason_number());
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
        imageRecyclerView.setAdapter(imageShowAdapter);

        RecyclerView.LayoutManager SimilarShowsLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        similarShowsRecyclerViewer.setLayoutManager(SimilarShowsLayoutManager);
        similarShowsRecyclerViewer.setItemAnimator(new DefaultItemAnimator());
        similarShowsRecyclerViewer.setAdapter(tvshowsAdapter);
        similarShowsRecyclerViewer.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), similarShowsRecyclerViewer, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TVshow tvshow = similarShowsList.get(position);
                Intent intent = new Intent(getApplicationContext(), TvShowsDetailsActivity.class);
                intent.putExtra("show_id", tvshow.getShow_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        seeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TvShowsDetailsActivity.this, ImagesSliderActivity.class);
                intent.putExtra("element_id", show_id);
                intent.putExtra("element_title",show_title.getText().toString());
                intent.putExtra("element_genre","show");
                startActivity(intent);
            }
        });

        Resources r = getResources();
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        if (!TvShowIsLikedBeforeByTheCinephile(show_id)) {
            likeBtn.setBackgroundResource(R.drawable.button_shape);
            likeBtn.setImageResource(R.drawable.thumbs_up);
            likeBtn.setPadding(Math.round(px), Math.round(px), Math.round(px), Math.round(px));
            likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TvShowIsLikedBeforeByTheCinephile(show_id)) {
                    LikeTvShow("cinephiles_shows_likes", show_id);
                } else

                {
                    likeBtn.setBackgroundResource(R.drawable.button_shape);
                    likeBtn.setImageResource(R.drawable.thumbs_up);
                    likeBtn.setPadding(Math.round(px), Math.round(px), Math.round(px), Math.round(px));
                    likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    removeLikeFromShow(show_id);
                }

            }
        });

        if (!TvShowIsRatedBeforeByTheCinephile(show_id)) {
            rateBtn.setBackgroundResource(R.drawable.button_shape);
            rateBtn.setImageResource(R.drawable.star2);
            rateBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
            rateBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            rate_text.setText("Je note");
        }

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(TvShowsDetailsActivity.this);
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
                        TvShowsDetailsActivity.this);

                alertDialogBuilder.setView(DialogView);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        my_rate = ratingBar.getRating();
                                        RateShow("cinephiles_shows_rates", show_id);
                                        rateBtn.setBackgroundResource(R.drawable.button_shape_blue);
                                        rateBtn.setImageResource(R.drawable.star_blue);
                                        rateBtn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                                        rateBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        rate_text.setText(String.valueOf(my_rate));
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
            }
        });

        add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment.getText().equals(""))
                {
                    AddCommentAboutTvShow(comment.getText().toString(), show_id);
                    comment.setText("");
                }
            }
        });

        see_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TvShowsDetailsActivity.this, DisplayElementCommentsActivity.class);
                intent.putExtra("element_id", show_id);
                intent.putExtra("element_genre", "show");
                startActivity(intent);
            }
        });

        GetTvShowDetails(ThemoviedbApiAccess.AllTvShowDetailsURL(show_id));
        GetTvShowVideos(ThemoviedbApiAccess.GetTvShowTrailer(show_id));
        GetTvShowImages(ThemoviedbApiAccess.TvShowsBackdrops(show_id));
        GetSimilarTvShows(ThemoviedbApiAccess.TvShowSimilarURl(show_id));
        DisplayComments(show_id);

    }

    private void GetTvShowDetails(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    show_title.setText(response.getString("name"));
                    String first_air_date="";
                    try {
                        if (response.getString("first_air_date")!=null)
                            first_air_date = response.getString("first_air_date");
                    }
                    catch (Exception e){
                        first_air_date="";
                    }

                    show_first_air_date.setText("1ère diffisuion : "+ first_air_date);
                    show_number_of_episodes.setText("Nombre d'épisodes : " +response.getString("number_of_episodes"));
                    show_number_of_seasons.setText("Nombre de saisons : " + response.getString("number_of_seasons"));
                    rating_value.setText(response.getString("vote_average"));
                    ratingBar.setRating(Float.valueOf(response.getString("vote_average")));

                    show_overview.setText(response.getString("overview"));
                    Glide.with(getApplicationContext()).load("https://image.tmdb.org/t/p/w500" + response.getString("poster_path")).into(show_poster);

                    // Getting show genres
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
                    show_genres_tags.setTags(tags);

                    TvShowsDetailsActivity.MyTagGroupOnClickListener tgClickListener = new TvShowsDetailsActivity.MyTagGroupOnClickListener();
                    show_genres_tags.setOnClickListener(tgClickListener);
                    show_genres_tags.setOnTagClickListener(mTagClickListener);

                    JSONArray show_createdby = response.getJSONArray("created_by");

                    JSONArray show_networks = response.getJSONArray("networks");

                    JSONObject show_credits = response.getJSONObject("credits");
                    JSONArray show_cast = show_credits.getJSONArray("cast");

                    show_created_by.setText("Créée par : " + show_createdby.getJSONObject(0).getString("name"));
                    networks.setText("Diffusé sur : " + show_networks.getJSONObject(0).getString("name"));
                    for (int i=0; i<show_cast.length(); i++)
                    {
                        JSONObject actor_json = (JSONObject) show_cast.get(i);

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

                    actors_number.setText("•  " +String.valueOf(actorsList.size()));

                    JSONArray show_seasons = response.getJSONArray("seasons");

                    for (int i=0; i<show_seasons.length(); i++)
                    {
                        JSONObject season_json = (JSONObject) show_seasons.get(i);

                        int season_id = season_json.getInt("id");
                        int season_number = season_json.getInt("season_number");
                        String season_date = season_json.getString("air_date");
                        String season_poster = "https://image.tmdb.org/t/p/w500" + season_json.getString("poster_path");
                        int season_episodes_number = season_json.getInt("episode_count");

                        if (!season_json.getString("poster_path").equals("null") && season_number!=0)
                        {
                            seasonsList.add(new Season(season_id,season_number,season_episodes_number,season_date,season_poster));
                            seasonAdapter.notifyDataSetChanged();
                        }
                    }
                    seasons_number.setText("•  " +String.valueOf(seasonsList.size()));


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

    private void GetTvShowVideos(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray show_videos = (JSONArray) response.getJSONArray("results");
                    for (int i=0; i<show_videos.length(); i++)
                    {
                        JSONObject video_json = (JSONObject) show_videos.get(i);
                        String video_url = video_json.getString("key");
                        videosList.add(video_url);
                    }
                    youTubeView.initialize("AIzaSyB8BGDlSvQQpezJ2dneya5JP7Qxogt7Fb4", TvShowsDetailsActivity.this);
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

    private void GetTvShowImages(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray show_backdrops = (JSONArray) response.getJSONArray("backdrops");
                    for (int i=0; i<show_backdrops.length(); i++)
                    {
                        JSONObject image_json = (JSONObject) show_backdrops.get(i);
                        String image_url = "https://image.tmdb.org/t/p/w780" + image_json.getString("file_path");

                        imagesList.add(image_url);
                        imageShowAdapter.notifyDataSetChanged();
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

    private void GetSimilarTvShows(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray results = response.getJSONArray("results");
                    for (int i=0; i<results.length();i++)
                    {
                        JSONObject show_json = (JSONObject) results.get(i);

                        int show_id = show_json.getInt("id");
                        String title = show_json.getString("name");
                        String poster = "https://image.tmdb.org/t/p/w500" + show_json.getString("poster_path");
                        String airing_date = show_json.getString("first_air_date");
                        Float vote_average = Float.parseFloat(show_json.getString("vote_average"));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date datetime = sdf.parse(airing_date);
                        Calendar c = Calendar.getInstance();
                        c.setTime(datetime);
                        if(!show_json.getString("poster_path").equals("null") )
                        {
                            if (show_json.getString("original_language").equals("en") && c.get(Calendar.YEAR)>=2005)
                                    {
                                        similarShowsList.add(new TVshow(show_id, title,airing_date,poster,vote_average));
                                        tvshowsAdapter.notifyDataSetChanged();
                                    }
                        }

                    }
                    similar_shows_number.setText("•  " +String.valueOf(similarShowsList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
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


    public void AddCommentAboutTvShow(final String comment_content, final int show_id)
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
                        showsCommentsReference = DCinephiliaInstance.getReference("shows_comments");
                        Comment comment = new Comment(comment_content,new Date(), cinephile.getFirstname() +
                                " " + cinephile.getLastname().toUpperCase()  , show_id);
                        showsCommentsReference.child(String.valueOf(show_id)).push().setValue(comment);
                    }
                }
                DisplayComments(show_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void DisplayComments(int show_id)
    {
        final ArrayList<Comment> comments = new ArrayList<Comment>();
        showsCommentsReference = DCinephiliaInstance.getReference("shows_comments/"+String.valueOf(show_id));
        showsCommentsReference.addValueEventListener(new ValueEventListener() {
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




    public void LikeTvShow(final String element_genre, final int element_id)
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
                        Like like = new Like(cinephile.getEmail(), element_id);
                        likedElementsReference.child(String.valueOf(element_id)).push().setValue(like);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void RateShow(final String element_genre, final int element_id)
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
                        ratedTvShowsReference = DCinephiliaInstance.getReference(element_genre);
                        Rate rating  = new Rate(cinephile.getEmail(), element_id, my_rate);
                        ratedTvShowsReference.child(String.valueOf(element_id)).push().setValue(rating);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public boolean TvShowIsLikedBeforeByTheCinephile(int show_id)
    {
        likedElementsReference = DCinephiliaInstance.getReference("cinephiles_shows_likes/"+String.valueOf(show_id));
        likedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                show_liked_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Like like = singleSnapshot.getValue(Like.class);

                        if (like.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            show_liked_before = true;

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
        return show_liked_before;
    }

    public boolean TvShowIsRatedBeforeByTheCinephile(int show_id)
    {
        ratedTvShowsReference = DCinephiliaInstance.getReference("cinephiles_shows_rates/"+String.valueOf(show_id));
        ratedTvShowsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                show_rated_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Rate rate = singleSnapshot.getValue(Rate.class);

                        if (rate.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            show_rated_before = true;

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
        return show_rated_before;
    }

    public void removeLikeFromShow(final int show_id)
    {

        likedElementsReference = DCinephiliaInstance.getReference("cinephiles_shows_likes/"+String.valueOf(show_id));
        likedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                show_liked_before = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Like like = singleSnapshot.getValue(Like.class);

                        if (like.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            show_liked_before = false;

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

    class MyTagGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //
        }
    }

    private TagGroup.OnTagClickListener mTagClickListener = new TagGroup.OnTagClickListener() {
        @Override
        public void onTagClick(String tag) {
            Toast.makeText(TvShowsDetailsActivity.this, tag, Toast.LENGTH_SHORT).show();
        }
    };

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
}
