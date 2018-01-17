package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Adapaters.ImageActorAdapter;
import tpdev.upmc.dcinephila.Adapaters.MoviesAdapter;
import tpdev.upmc.dcinephila.Adapaters.TvShowsAdapter;
import tpdev.upmc.dcinephila.Beans.Movie;
import tpdev.upmc.dcinephila.Beans.TVshow;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.MySpannable;
import tpdev.upmc.dcinephila.DesignClasses.RecyclerTouchListener;
import tpdev.upmc.dcinephila.R;

public class StarBiographyActivity extends AppCompatActivity {

    private TextView actor_name, birthday_text, birthday_place_text, biopgraphy, movies_text, movies_number,
            shows_text, shows_number, images_text, images_number, seeImageBtn;
    private RecyclerView moviesRecycerView, showsRecyclerViewer, imageRecyclerView;
    private CircleImageView actor_avatar;
    private KenBurnsView background;
    private ArrayList<Movie> moviesList;
    private ArrayList<TVshow> showsList;
    private ArrayList<String> imagesList;
    private ImageActorAdapter imageMovieAdapter;
    private MoviesAdapter moviesAdapter;
    private TvShowsAdapter showsAdapter;
    private static String TAG = StarBiographyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_biography);

        Intent intent = getIntent();
        final int person_id = intent.getIntExtra("person_id",0);
        final String person_name = intent.getStringExtra("person_name");
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");

        actor_name = (TextView) findViewById(R.id.actor_name);
        birthday_text = (TextView) findViewById(R.id.birthday_text);
        birthday_place_text = (TextView) findViewById(R.id.birthday_place_text);
        biopgraphy = (TextView) findViewById(R.id.biography);
        actor_avatar = (CircleImageView) findViewById(R.id.avatar);
        background = (KenBurnsView) findViewById(R.id.background);
        movies_text = (TextView) findViewById(R.id.movies_text);
        movies_number = (TextView) findViewById(R.id.movies_number);
        shows_text = (TextView) findViewById(R.id.shows_text);
        shows_number = (TextView) findViewById(R.id.shows_number);
        images_text = (TextView) findViewById(R.id.images_text);
        images_number = (TextView) findViewById(R.id.images_number);
        imageRecyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        moviesRecycerView = (RecyclerView) findViewById(R.id.movies_recyclerview);
        showsRecyclerViewer = (RecyclerView) findViewById(R.id.shows_recyclerview);
        seeImageBtn = (TextView) findViewById(R.id.seeImages);

        actor_name.setTypeface(face_bold);
        birthday_text.setTypeface(face_bold);
        birthday_place_text.setTypeface(face_bold);
        movies_text.setTypeface(face_bold);
        images_text.setTypeface(face_bold);
        shows_text.setTypeface(face_bold);
        seeImageBtn.setTypeface(face_bold);
        biopgraphy.setTypeface(face);
        movies_number.setTypeface(face);
        images_number.setTypeface(face);
        shows_number.setTypeface(face);

        moviesList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesList);
        RecyclerView.LayoutManager MoviesLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        moviesRecycerView.setLayoutManager(MoviesLayoutManager);
        moviesRecycerView.setItemAnimator(new DefaultItemAnimator());
        moviesRecycerView.setAdapter(moviesAdapter);
        moviesRecycerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), moviesRecycerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = moviesList.get(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getMovie_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        showsList = new ArrayList<>();
        showsAdapter = new TvShowsAdapter(this, showsList);
        RecyclerView.LayoutManager ShowsLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        showsRecyclerViewer.setLayoutManager(ShowsLayoutManager);
        showsRecyclerViewer.setItemAnimator(new DefaultItemAnimator());
        showsRecyclerViewer.setAdapter(showsAdapter);
        showsRecyclerViewer.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), showsRecyclerViewer, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TVshow show = showsList.get(position);
                Intent intent = new Intent(getApplicationContext(), TvShowsDetailsActivity.class);
                intent.putExtra("show_id", show.getShow_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        imagesList = new ArrayList<>();
        imageMovieAdapter = new ImageActorAdapter(this, imagesList);
        RecyclerView.LayoutManager ImagesLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(ImagesLayoutManager);
        imageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerView.setAdapter(imageMovieAdapter);

        seeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StarBiographyActivity.this, ImagesSliderActivity.class);
                intent.putExtra("element_id", person_id);
                intent.putExtra("element_title",actor_name.getText().toString());
                intent.putExtra("element_genre","person");
                startActivity(intent);
            }
        });

        GetStarDetails(ThemoviedbApiAccess.AllPersonDetailsURL(person_id));
        GetActorShows(ThemoviedbApiAccess.PersonShows(person_id));
        GetActorMovies(ThemoviedbApiAccess.PersonMovies(person_id));
        GetActorImages(ThemoviedbApiAccess.PersonImages(person_id));

    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final String strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(true){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Voir moins", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 6, "Voir plus", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    private void GetStarDetails(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    actor_name.setText(response.getString("name"));
                    String birthday_date="";
                    try {
                        if (response.getString("birthday")!=null)
                            birthday_date = response.getString("birthday");
                    }
                    catch (Exception e){
                        birthday_date="/";
                    }

                    String birthday_place="";
                    try {
                        if (response.getString("place_of_birth")!=null)
                            birthday_place = response.getString("place_of_birth");
                    }
                    catch (Exception e){
                        birthday_place="/";
                    }

                    birthday_text.setText("\uD83C\uDF82" + " " + birthday_date);
                    birthday_place_text.setText("\uD83D\uDCCC" + " " + birthday_place);
                    biopgraphy.setText(response.getString("biography"));
                    Glide.with(getApplicationContext()).load("https://image.tmdb.org/t/p/w500" + response.getString("profile_path")).into(actor_avatar);
                    makeTextViewResizable(biopgraphy, 6, "Voir plus", true);

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

    private void GetActorMovies(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray movies_array = (JSONArray) response.getJSONArray("cast");
                    for (int i=0; i<movies_array.length(); i++)
                    {
                        JSONObject movie_json = (JSONObject) movies_array.get(i);
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
                            moviesList.add(new Movie(movie_id,title,release_date,poster));
                        }

                    }
                    Collections.sort(moviesList, new Comparator<Movie>() {
                        public int compare(Movie o1, Movie o2) {
                            if (o1.getDateTime() == null || o2.getDateTime() == null)
                                return 0;
                            return o2.getDateTime().compareTo(o1.getDateTime());
                        }
                    });
                    moviesAdapter.notifyDataSetChanged();
                    movies_number.setText("•  " +String.valueOf(moviesList.size()));

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

    private void GetActorShows(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray shows_array = (JSONArray) response.getJSONArray("cast");
                    for (int i=0; i<shows_array.length(); i++)
                    {
                        JSONObject show_json = (JSONObject) shows_array.get(i);
                        int show_id = show_json.getInt("id");
                        String title = show_json.getString("name");
                        String poster = "https://image.tmdb.org/t/p/w500" + show_json.getString("poster_path");
                        Float vote_average = Float.parseFloat(show_json.getString("vote_average"));
                        String first_air_date="";
                        try {
                            if (show_json.getString("first_air_date")!=null)
                                first_air_date = show_json.getString("first_air_date");
                        }
                        catch (Exception e){
                            first_air_date="";
                        }

                        if(!show_json.getString("poster_path").equals("null"))
                        {
                            showsList.add(new TVshow(show_id, title,first_air_date,poster,vote_average));
                            showsAdapter.notifyDataSetChanged();
                        }

                    }
                    Collections.sort(showsList, new Comparator<TVshow>() {
                        public int compare(TVshow o1, TVshow o2) {
                            if (o1.getAiring_date() == null || o2.getAiring_date() == null)
                                return 0;
                            return o2.getAiring_date().compareTo(o1.getAiring_date());
                        }
                    });
                    showsAdapter.notifyDataSetChanged();
                    shows_number.setText("•  " +String.valueOf(showsList.size()));

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

    private void GetActorImages(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray actor_images = (JSONArray) response.getJSONArray("profiles");
                    for (int i=0; i<actor_images.length(); i++)
                    {
                        JSONObject image_json = (JSONObject) actor_images.get(i);
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
}
