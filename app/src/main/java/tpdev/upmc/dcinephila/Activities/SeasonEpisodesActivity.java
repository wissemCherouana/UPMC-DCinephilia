package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Adapaters.TimeLineAdapter;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.R;
import tpdev.upmc.dcinephila.DesignClasses.Timeline.OrderStatus;
import tpdev.upmc.dcinephila.DesignClasses.Timeline.Orientation;
import tpdev.upmc.dcinephila.DesignClasses.Timeline.TimeLineModel;

public class SeasonEpisodesActivity extends AppCompatActivity {

    private TextView show_title;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> episodesList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private static String TAG = SeasonEpisodesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_episodes);

        Typeface face_bold= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");

        Intent intent = getIntent();
        int show_id = intent.getIntExtra("show_id",0);
        int season_number = intent.getIntExtra("season_number",0);
        String season_name = intent.getStringExtra("season_name");

        show_title = (TextView) findViewById(R.id.show_name);
        show_title.setTypeface(face_bold);
        show_title.setText(season_name);

        mOrientation = Orientation.VERTICAL;
        mWithLinePadding = true;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        GetSeasonEpisodes(ThemoviedbApiAccess.GetSeasonEpisodes(show_id,season_number));
        mTimeLineAdapter = new TimeLineAdapter(episodesList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

    }


    private void GetSeasonEpisodes(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray season_episodes = (JSONArray) response.getJSONArray("episodes");
                    for (int i=0; i<season_episodes.length(); i++)
                    {
                        JSONObject episode_json = (JSONObject) season_episodes.get(i);
                        String episode_title = episode_json.getString("episode_number") +". " +episode_json.getString("name");
                        String episode_date = episode_json.getString("air_date");
                        String episode_resume = episode_json.getString("overview");
                        String episode_picture = "https://image.tmdb.org/t/p/w780" + episode_json.getString("still_path");

                        if (i==0 || i== (season_episodes.length()-1))
                            episodesList.add(new TimeLineModel(episode_title,episode_date, episode_picture, episode_resume, OrderStatus.ACTIVE));
                        else
                            episodesList.add(new TimeLineModel(episode_title,episode_date, episode_picture, episode_resume, OrderStatus.COMPLETED));
                        mTimeLineAdapter.notifyDataSetChanged();
                    }

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