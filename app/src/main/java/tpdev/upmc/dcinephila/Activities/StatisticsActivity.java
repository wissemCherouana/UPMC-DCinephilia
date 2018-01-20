package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Comment;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.Beans.Rate;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.R;

public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference likedElementsReference, ratedElementsReference, commentsReference, cinephilesReference;
    private Button likes, rates, commentaires;
    private FirebaseDatabase DCinephiliaInstance;
    private ArrayList<Integer> moviesLikedByCinephile;
    private ArrayList<Comment> comments;
    private ArrayList<Rate> rates_cinephile;
    private static String TAG = StatisticsActivity.class.getSimpleName();
    private Map<String, Integer> likes_map ;
    private Map<Integer, Integer> comments_map ;
    private int count=0,count_comments=0;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;
    private TextView text1, text2;
    private PieChart pieChart;
    private LineChart lineChart;
    private Cinephile cinephile_id;
    public static final int[] JOYFUL_COLORS = {
            Color.rgb(210, 121, 166), Color.rgb(172, 57, 115), Color.rgb(255, 204, 0),
            Color.rgb(204, 204, 0), Color.rgb(128, 128, 0), Color.rgb(0, 179, 134),
            Color.rgb(41, 163, 163), Color.rgb(148, 184, 184), Color.rgb(204, 153, 102),
            Color.rgb(209, 97, 37)
    };
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DCinephilia");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Typeface face= Typeface.createFromAsset(this.getAssets(), "font/Comfortaa-Bold.ttf");
        auth = FirebaseAuth.getInstance();
        moviesLikedByCinephile = new ArrayList<>();
        comments = new ArrayList<>();
        rates_cinephile = new ArrayList<>();
        DCinephiliaInstance = FirebaseDatabase.getInstance();

        likes_map = new HashMap<String, Integer>();
        comments_map = new TreeMap<Integer, Integer>();

        likes_map.put("Action", 0);
        likes_map.put("Aventure", 0);
        likes_map.put("Animation", 0);
        likes_map.put("Comédie", 0);
        likes_map.put("Crime", 0);
        likes_map.put("Documentaire", 0);
        likes_map.put("Drame", 0);
        likes_map.put("Familial", 0);
        likes_map.put("Fantastique", 0);
        likes_map.put("Histoire", 0);
        likes_map.put("Horreur", 0);
        likes_map.put("Musique", 0);
        likes_map.put("Mystère", 0);
        likes_map.put("Romance", 0);
        likes_map.put("Science-Fiction", 0);
        likes_map.put("Téléfilm", 0);
        likes_map.put("Thriller", 0);
        likes_map.put("Guerre", 0);
        likes_map.put("Western", 0);

        comments_map.put(1,0);
        comments_map.put(2,0);
        comments_map.put(3,0);
        comments_map.put(4,0);
        comments_map.put(5,0);
        comments_map.put(6,0);
        comments_map.put(7,0);
        comments_map.put(8,0);
        comments_map.put(9,0);
        comments_map.put(10,0);
        comments_map.put(11,0);
        comments_map.put(12,0);

        text1 = (TextView) findViewById(R.id.chat1_text);
        text2 = (TextView) findViewById(R.id.chat2_text);

        text1.setTypeface(face);
        text2.setTypeface(face);

        likes = (Button) findViewById(R.id.likes);
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, DisplayLikesActivity.class);
                startActivity(intent);
            }
        });
        rates = (Button) findViewById(R.id.rates);
        rates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, DisplayRatesActivity.class);
                startActivity(intent);
            }
        });
        commentaires = (Button) findViewById(R.id.comments);

        entries = new ArrayList<>();
        labels = new ArrayList<String>();
        pieChart = (PieChart) findViewById(R.id.chart1);
        lineChart = (LineChart) findViewById(R.id.chart2);

        GetStatsAboutFilmsLikedByCinephile();
        GetStatsAboutFilmsRatedByCinephile();
        GetStatsAboutCinephileComments();
    }


    public void GetStatsAboutFilmsLikedByCinephile()
    {
        likedElementsReference = DCinephiliaInstance.getReference("cinephiles_movies_likes");
        likedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        for (DataSnapshot snapshot : singleSnapshot.getChildren())
                        {
                            Like like = snapshot.getValue(Like.class);

                            if (like.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                            {
                                moviesLikedByCinephile.add(like.getElement_id());
                            }
                        }
                    }
                }

                likes.setText(moviesLikedByCinephile.size() + " likes");

                for (int i=0; i<moviesLikedByCinephile.size(); i++)
                {

                    String urlJsonObj= ThemoviedbApiAccess.AllMovieDetailsURL(moviesLikedByCinephile.get(i));
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            urlJsonObj, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                count++;
                                String title = response.getString("title");
                                JSONArray genres = response.getJSONArray("genres");
                                for (int i=0; i<genres.length();i++) {
                                    JSONObject result_json = (JSONObject) genres.get(i);
                                    String genre_id = result_json.getString("name");
                                    boolean found =false;
                                    Iterator it = likes_map.entrySet().iterator();
                                    while (!found && it.hasNext()) {
                                        Map.Entry pair = (Map.Entry)it.next();
                                        int val = (int) pair.getValue();
                                        if (pair.getKey().equals(genre_id))
                                        {
                                            val++;
                                            likes_map.put(genre_id, val);
                                            found = true;
                                        }
                                    }

                                }
                                if (count==moviesLikedByCinephile.size())
                                {
                                    int i=0;
                                    Iterator it = likes_map.entrySet().iterator();
                                    while (it.hasNext()) {
                                        Map.Entry pair = (Map.Entry)it.next();
                                        int val = (int) pair.getValue();
                                        if (val!=0)
                                        {
                                            entries.add(new Entry((float)val,i));
                                            labels.add(pair.getKey().toString());
                                        }
                                        i++;
                                    }
                                    PieDataSet dataset = new PieDataSet(entries, "");
                                    dataset.setColors(JOYFUL_COLORS);
                                    PieData data = new PieData(labels, dataset); // initialize Piedata
                                    pieChart.setNoDataText("");
                                    pieChart.setDescription("");
                                    pieChart.getLegend().setEnabled(false);
                                    pieChart.setData(data);
                                    pieChart.invalidate();
                                }

                            } catch (Exception e) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void GetStatsAboutFilmsRatedByCinephile(){
        ratedElementsReference = DCinephiliaInstance.getReference("cinephiles_movies_rates");
        ratedElementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        for (DataSnapshot snapshot : singleSnapshot.getChildren())
                        {
                            Rate rate = snapshot.getValue(Rate.class);

                            if (rate.getCinephile().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                            {
                                rates_cinephile.add(rate);
                            }
                        }
                    }
                }

                rates.setText(rates_cinephile.size() + " rates");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void GetStatsAboutCinephileComments()
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
                        cinephile_id = cinephile;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        commentsReference = DCinephiliaInstance.getReference("movies_comments");
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    for (DataSnapshot snapshot : singleSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                }
                commentaires.setText(comments.size() + " comments");

                for (int i=0; i<comments.size();i++)
                {
                    count_comments++;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(comments.get(i).getComment_date());
                    int month = cal.get(Calendar.MONTH);
                    month++;

                    if (comments.get(i).getCinephile_id().equals(cinephile_id.getFirstname() +
                            " " + cinephile_id.getLastname().toUpperCase()))
                    {
                        boolean found = false;
                        Iterator it = comments_map.entrySet().iterator();
                        while (!found && it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            int val = (int) pair.getValue();
                            if (pair.getKey().equals(month))
                            {
                                val++;
                                comments_map.put(month, val);
                                found = true;
                            }
                        }
                    }

                    if (count_comments == comments.size())
                    {
                        ArrayList<String> xVals = new ArrayList<String>();
                        xVals.add("Janvier");
                        xVals.add("Février");
                        xVals.add("Mars");
                        xVals.add("Avril");
                        xVals.add("Mai");
                        xVals.add("Juin");
                        xVals.add("Juillet");
                        xVals.add("Août");
                        xVals.add("Septembre");
                        xVals.add("Octobre");
                        xVals.add("Novembre");
                        xVals.add("Decembre");
                        ArrayList<Entry> vals1 = new ArrayList<Entry>();
                        List sortedKeys=new ArrayList(comments_map.keySet());
                        Collections.sort(sortedKeys);
                        Iterator it = comments_map.entrySet().iterator();
                        int j=0;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            int key = (int) pair.getKey();
                            key--;
                            int val = (int) pair.getValue();
                            vals1.add(new Entry((int)val, key));
                        }
                        LineDataSet set1 = new LineDataSet(vals1, "#Commentaires par mois");
                        set1.setLineWidth(1.8f);
                        set1.setCircleRadius(4f);
                        set1.setCircleColor(Color.rgb(198, 83, 198));
                        set1.setColor(Color.rgb(198, 83, 198));
                        set1.setFillColor(Color.rgb(198, 83, 198));

                        LineData data = new LineData(xVals, set1);
                        lineChart.setDescription("");
                        lineChart.setData(data);
                        lineChart.invalidate();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            Intent intent = new Intent(StatisticsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(StatisticsActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view event_card clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(StatisticsActivity.this, CinemasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(StatisticsActivity.this, SeancesMoviesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(StatisticsActivity.this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(StatisticsActivity.this, StatisticsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(StatisticsActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(StatisticsActivity.this, SearchProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(StatisticsActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
