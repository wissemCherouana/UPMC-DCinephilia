package tpdev.upmc.dcinephila.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Beans.GeographicalPosition;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.R;

/**
 * This activity allows a cinephile who wants to attend an event to get the itinerary to the cinema
 * where it will be organized
 *
 * Use of Google Maps API
 */

public class CinemaEventActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = CinemasActivity.class.getSimpleName();
    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    private GoogleMap mMap;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_event);
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String cinema_name = intent.getStringExtra("place");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GetCinemaCordinates(ThemoviedbApiAccess.ILE_DE_FRANCE_CINEMAS, cinema_name);

    }

    /**
     * This application retrieves cordinates of a specific cinema of Ile-de-France
     * in order to display it on the map when the cinephile clicks on the place of an event
     * @param urlJsonObj the URI of the API of Open Data Ile de France
     * @param cinema the name of the cinema
     */
    public void GetCinemaCordinates(final String urlJsonObj, final String cinema) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray cinemas_json = (JSONArray) response.getJSONArray("records");
                    for (int i=0; i<cinemas_json.length(); i++)
                    {
                        JSONObject cinema_json = (JSONObject) cinemas_json.get(i);
                        JSONObject cinema_infos = (JSONObject) cinema_json.getJSONObject("fields");

                        // get the cinema's information
                        String cinema_name = cinema_infos.getString("enseigne");
                        String cinema_address = cinema_infos.getString("adrnumvoie");
                        String town = cinema_infos.getString("ville");

                        // retrieve latitude and longitude of the cinema
                        double cinema_longitude = cinema_infos.getDouble("lng");
                        double cinema_latitude = cinema_infos.getDouble("lat");

                        if (cinema_name.equals(cinema))
                        {
                            GeographicalPosition position = new GeographicalPosition(cinema_name,town, cinema_address,
                                    cinema_longitude,cinema_latitude);
                            // add marker on the map
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(position.getCinema_latitude(), position.getCinema_longitude()))
                                    .title(position.getCinama_name())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                    .snippet(position.getCinema_town() + ", "  +position.getCinema_address()));
                        }

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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // setting up the map with the marker of the chosen cinema
        mMap = googleMap;
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_CODE_PERMISSION_LOCATION);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(48.8311155,2.344223);
            // Showing the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            mMap.getUiSettings().setZoomControlsEnabled(true);


        } else {
            Toast.makeText(this, "pb", Toast.LENGTH_LONG).show();

        }
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
            Intent intent = new Intent(CinemaEventActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(CinemaEventActivity.this, LoginActivity.class));
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
            Intent intent = new Intent(CinemaEventActivity.this, CinemasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(CinemaEventActivity.this, SeancesMoviesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(CinemaEventActivity.this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(CinemaEventActivity.this, StatisticsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(CinemaEventActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(CinemaEventActivity.this, SearchProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(CinemaEventActivity.this, LoginActivity.class));
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
