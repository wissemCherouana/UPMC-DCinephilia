package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.transition.Visibility;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Adapaters.SearchMoviesResultsAdapter;
import tpdev.upmc.dcinephila.Adapaters.SearchShowsResultsAdapter;
import tpdev.upmc.dcinephila.Adapaters.SearchStarsResultsAdapter;
import tpdev.upmc.dcinephila.Beans.Actor;
import tpdev.upmc.dcinephila.Beans.Movie;
import tpdev.upmc.dcinephila.Beans.TVshow;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.RecyclerTouchListener;
import tpdev.upmc.dcinephila.R;

public class SearchResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView search_text, movies_text, shows_text, stars_text;
    private RecyclerView searchMoviesResultsRecyclerView, searchShowsResultsRecyclerView, searchStarsResultsRecyclerView;
    private LinearLayout movies_layout, shows_layout, stars_layout;
    private View divider_1, divider_2;
    private SearchMoviesResultsAdapter searchMoviesResultsAdapter;
    private SearchShowsResultsAdapter searchShowsResultsAdapter;
    private SearchStarsResultsAdapter searchStarsResultsAdapter;
    private ArrayList<Movie> moviesResultsList;
    private ArrayList<TVshow> showsResultsList;
    private ArrayList<Actor> starsResultsList;
    private static String TAG = SearchResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query").toString();

        Typeface face= Typeface.createFromAsset(this.getAssets(), "font/Comfortaa-Bold.ttf");

        search_text = (TextView) findViewById(R.id.search_text);
        shows_text = (TextView) findViewById(R.id.shows_text);
        movies_text = (TextView) findViewById(R.id.movies_text);
        stars_text = (TextView) findViewById(R.id.stars_text);

        searchMoviesResultsRecyclerView = (RecyclerView) findViewById(R.id.search_movies_results_recyclerview);
        searchShowsResultsRecyclerView = (RecyclerView) findViewById(R.id.search_shows_results_recyclerview);
        searchStarsResultsRecyclerView = (RecyclerView) findViewById(R.id.search_stars_results_recyclerview);

        movies_layout = (LinearLayout) findViewById(R.id.movies_layout);
        shows_layout = (LinearLayout) findViewById(R.id.shows_layout);
        stars_layout = (LinearLayout) findViewById(R.id.stars_layout);

        divider_1 = (View) findViewById(R.id.divider_1);
        divider_2 = (View) findViewById(R.id.divider_2);

        moviesResultsList = new ArrayList<Movie>();
        showsResultsList = new ArrayList<TVshow>();
        starsResultsList = new ArrayList<>();

        searchMoviesResultsAdapter = new SearchMoviesResultsAdapter(this, moviesResultsList);
        searchShowsResultsAdapter = new SearchShowsResultsAdapter(this, showsResultsList);
        searchStarsResultsAdapter = new SearchStarsResultsAdapter(this, starsResultsList);

        search_text.setText("Résultat de recherche pour : " + query);
        search_text.setTypeface(face);
        movies_text.setTypeface(face);
        shows_text.setTypeface(face);
        stars_text.setTypeface(face);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        searchMoviesResultsRecyclerView.setLayoutManager(mLayoutManager);
        searchMoviesResultsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        searchMoviesResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchMoviesResultsRecyclerView.setAdapter(searchMoviesResultsAdapter);
        searchMoviesResultsRecyclerView.setNestedScrollingEnabled(false);

        searchMoviesResultsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), searchMoviesResultsRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = moviesResultsList.get(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getMovie_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        RecyclerView.LayoutManager sLayoutManager = new GridLayoutManager(this, 2);
        searchShowsResultsRecyclerView.setLayoutManager(sLayoutManager);
        searchShowsResultsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        searchShowsResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchShowsResultsRecyclerView.setAdapter(searchShowsResultsAdapter);
        searchShowsResultsRecyclerView.setNestedScrollingEnabled(false);

        searchShowsResultsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), searchShowsResultsRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TVshow show = showsResultsList.get(position);
                Intent intent = new Intent(getApplicationContext(), TvShowsDetailsActivity.class);
                intent.putExtra("show_id", show.getShow_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        RecyclerView.LayoutManager aLayoutManager = new GridLayoutManager(this, 2);
        searchStarsResultsRecyclerView.setLayoutManager(aLayoutManager);
        searchStarsResultsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        searchStarsResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchStarsResultsRecyclerView.setAdapter(searchStarsResultsAdapter);
        searchStarsResultsRecyclerView.setNestedScrollingEnabled(false);

        searchStarsResultsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), searchStarsResultsRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Actor actor = starsResultsList.get(position);
                Intent intent = new Intent(getApplicationContext(), StarBiographyActivity.class);
                intent.putExtra("person_id", actor.getActor_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        SearchQuery(ThemoviedbApiAccess.MULTIPLE_SEARCH+query);
    }

    private void SearchQuery(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray results = response.getJSONArray("results");
                    for (int i=0; i<results.length();i++)
                    {
                        JSONObject result_json = (JSONObject) results.get(i);

                        String media_type = result_json.getString("media_type");

                        if (media_type.equals("movie"))
                        {
                            int movie_id = result_json.getInt("id");
                            String title = result_json.getString("title");
                            String poster = "https://image.tmdb.org/t/p/w500" + result_json.getString("poster_path");
                            String release_date = result_json.getString("release_date");
                            if (!(result_json.getString("poster_path").equals("null"))) {
                                moviesResultsList.add(new Movie(movie_id, title, release_date, poster));
                                searchMoviesResultsAdapter.notifyDataSetChanged();
                            }
                        }
                        else  if (media_type.equals("tv"))
                        {
                            int show_id = result_json.getInt("id");
                            String title = result_json.getString("name");
                            String poster = "https://image.tmdb.org/t/p/w500" + result_json.getString("poster_path");
                            String airing_date = result_json.getString("first_air_date");
                            Float vote_average = Float.parseFloat(result_json.getString("vote_average"));

                            if (!(result_json.getString("poster_path").equals("null")))
                            {
                                showsResultsList.add(new TVshow(show_id, title,airing_date,poster,vote_average));
                                searchShowsResultsAdapter.notifyDataSetChanged();
                            }
                        }
                        else if (media_type.equals("person")) {
                            int actor_id = result_json.getInt("id");
                            String name = result_json.getString("name");
                            String profile_picture = "https://image.tmdb.org/t/p/w500" + result_json.getString("profile_path");


                            if (!(result_json.getString("profile_path").equals("null"))) {
                                starsResultsList.add(new Actor(actor_id,name,"",profile_picture));
                                searchStarsResultsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    Collections.sort(moviesResultsList, new Comparator<Movie>() {
                        public int compare(Movie o1, Movie o2) {
                            if (o1.getDateTime() == null || o2.getDateTime() == null)
                                return 0;
                            return o2.getDateTime().compareTo(o1.getDateTime());
                        }
                    });
                    searchMoviesResultsAdapter.notifyDataSetChanged();

                    Collections.sort(showsResultsList, new Comparator<TVshow>() {
                        public int compare(TVshow o1, TVshow o2) {
                            if (o1.getAiring_date() == null || o2.getAiring_date() == null )
                                return 0;
                            return o2.getAiring_date().compareTo(o1.getAiring_date());
                        }
                    });
                    searchShowsResultsAdapter.notifyDataSetChanged();

                    if (moviesResultsList.size()==0)
                    {
                        movies_layout.setVisibility(LinearLayout.GONE);
                        divider_1.setVisibility(View.GONE);
                    }
                    if (showsResultsList.size()==0)
                    {
                        shows_layout.setVisibility(LinearLayout.GONE);
                        divider_2.setVisibility(View.GONE);
                    }
                    if (starsResultsList.size()==0) stars_layout.setVisibility((LinearLayout.GONE));

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


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // event_card position
            int column = position % spanCount; // event_card column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // event_card bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // event_card top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar event_card clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SearchResultsActivity.this, CinemasActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view event_card clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

                Intent intent = new Intent(SearchResultsActivity.this, SearchResultsActivity.class);
                intent.putExtra("query", searchViewAndroidActionBar.getQuery().toString());
                startActivity(intent);
                return true ;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
