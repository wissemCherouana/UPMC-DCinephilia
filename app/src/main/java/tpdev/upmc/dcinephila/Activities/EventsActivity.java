package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tpdev.upmc.dcinephila.Adapaters.EventAdapter;
import tpdev.upmc.dcinephila.Beans.Event;
import tpdev.upmc.dcinephila.DesignClasses.Utils;
import tpdev.upmc.dcinephila.R;

/**
 * This activity allows a cinephile to consult the different events cretaed by hi or other cinephiles
 * It also allows him to participate to an event that interests him
 */

public class EventsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HorizontalInfiniteCycleViewPager eventsRecyclerView;
    private ArrayList<Utils.EventItem> eventsList;

    private DatabaseReference eventsReference;
    private FirebaseDatabase DCinephiliaInstance;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        auth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        DCinephiliaInstance = FirebaseDatabase.getInstance();

        eventsRecyclerView =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        GetEvents();

    }

    /**
     * This method gets all the events created by cinephiles
     */
    public void GetEvents(){

        // get the list of all events
        eventsReference = DCinephiliaInstance.getReference("events");
        eventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsList = new ArrayList<Utils.EventItem>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    // get event's information
                    Event event = singleSnapshot.getValue(Event.class);
                    String event_id = event.getEvent_id();
                    String event_name = event.getEvent_name();
                    String event_movie = event.getEvent_seance().getMovie();
                    String event_description = event.getEvent_description();
                    String[] date_parts = event.getEvent_seance().getSeance_date().split(" ");
                    String event_date = date_parts[0];
                    String event_hour = date_parts[1];
                    String event_place = event.getEvent_seance().getSeance_place();
                    String event_poster = event.getEvent_seance().getMovie_poster();
                    int event_limit = event.getEvent_limit();
                    String event_created_by = event.getEvent_created_by().getFirstname() + " " +
                            event.getEvent_created_by().getLastname().toUpperCase();

                    // create a new event item
                    Utils.EventItem eventItem = new Utils.EventItem(event_id, event_name,event_movie,event_poster, event_description,event_date,
                            event_hour,event_place,event_limit,event_created_by);
                    // add it to the list of events
                    eventsList.add(eventItem);
                }
                // set the adapter of the recyclerview of events
                eventsRecyclerView.setAdapter(new EventAdapter(getApplicationContext(),eventsList));
                eventsRecyclerView.notifyDataSetChanged();
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
            Intent intent = new Intent(EventsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(EventsActivity.this, LoginActivity.class));
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
            Intent intent = new Intent(EventsActivity.this, CinemasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(EventsActivity.this, SeancesMoviesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(EventsActivity.this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(EventsActivity.this, StatisticsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(EventsActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(EventsActivity.this, SearchProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(EventsActivity.this, LoginActivity.class));
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
