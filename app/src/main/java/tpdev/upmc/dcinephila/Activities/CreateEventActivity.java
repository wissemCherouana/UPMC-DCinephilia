package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Event;
import tpdev.upmc.dcinephila.Beans.Rate;
import tpdev.upmc.dcinephila.Beans.Seance;
import tpdev.upmc.dcinephila.R;

public class CreateEventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView event_text;
    private EditText event_name, event_description, event_limit;
    private Spinner event_movie, event_seance, event_place;
    private Seance selected_seance;
    private Cinephile event_created_by;
    private ArrayList<Seance> seances;
    private Button create_event_btn;
    private DatabaseReference seancesMoviesReference, eventsReference, cinephilesReference;
    private FirebaseDatabase DCinephiliaInstance;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        auth = FirebaseAuth.getInstance();
        Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");

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

        event_text = (TextView) findViewById(R.id.dcinephilia);
        event_name= (EditText) findViewById(R.id.event_name);
        event_description = (EditText) findViewById(R.id.event_description);
        event_limit = (EditText) findViewById(R.id.event_limit);
        event_movie = (Spinner) findViewById(R.id.event_movie);
        event_seance = (Spinner) findViewById(R.id.event_seance);
        event_place = (Spinner) findViewById(R.id.event_place);
        create_event_btn = (Button) findViewById(R.id.create_event_button);

        event_text.setTypeface(face_bold);
        event_name.setTypeface(face);
        event_description.setTypeface(face);
        event_limit.setTypeface(face);

        GetSeances();
        create_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEvent();
                Toast.makeText(getApplicationContext(), "Evénement créé avec succès!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateEventActivity.this, EventsActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public void GetSeances()
    {
        seancesMoviesReference = DCinephiliaInstance.getReference("seances_movies");
        seancesMoviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seances = new ArrayList<Seance>();
                final ArrayList<String> movies_list = new ArrayList<String>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        for (DataSnapshot snapshot : singleSnapshot.getChildren()) {
                            Seance seance = snapshot.getValue(Seance.class);
                            seances.add(seance);
                            if (!ItContainsMovie(seance.getMovie(), movies_list))
                            {
                                movies_list.add(seance.getMovie());
                            }

                        }
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, movies_list);
                dataAdapter.setDropDownViewResource(R.layout.drowdown_spinner);
                event_movie.setAdapter(dataAdapter);
                event_movie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String chosen_movie = movies_list.get(position);
                        final ArrayList<String> seances_list = new ArrayList<String>();
                        for(int i=0; i<seances.size();i++)
                        {
                            if (seances.get(i).getMovie().equals(chosen_movie))
                            {
                                seances_list.add(seances.get(i).getSeance_date());
                            }
                        }

                        ArrayAdapter<String> seancesAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, seances_list);
                        seancesAdapter.setDropDownViewResource(R.layout.drowdown_spinner);
                        event_seance.setAdapter(seancesAdapter);
                        event_seance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final String chosen_seance = seances_list.get(position);
                                final ArrayList<String> places_list = new ArrayList<String>();
                                for(int i=0; i<seances.size();i++)
                                {
                                    if (seances.get(i).getMovie().equals(chosen_movie) &&
                                            seances.get(i).getSeance_date().equals(chosen_seance))
                                    {
                                        places_list.add(seances.get(i).getSeance_place());
                                    }
                                }
                                ArrayAdapter<String> placesAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, places_list);
                                placesAdapter.setDropDownViewResource(R.layout.drowdown_spinner);
                                event_place.setAdapter(placesAdapter);
                                event_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                        String chosen_place = places_list.get(position);
                                        for(int i=0; i<seances.size();i++)
                                        {
                                            if (seances.get(i).getMovie().equals(chosen_movie) &&
                                                    seances.get(i).getSeance_date().equals(chosen_seance) &&
                                                    seances.get(i).getSeance_place().equals(chosen_place))
                                            {
                                                selected_seance = seances.get(i);
                                                break;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }

                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    private boolean ItContainsMovie(String movie, ArrayList list)
    {
        boolean found=false;
        int i=0;
        while (!found && i<list.size())
        {
            if (list.get(i).equals(movie))
            {
                found = true;
            }
            i++;
        }
        return found;
    }

    public void CreateEvent()
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
                        event_created_by = cinephile;
                    }
                }
                eventsReference = DCinephiliaInstance.getReference("events");
                String event_id =  eventsReference.push().getKey();
                Event event = new Event(event_id, event_name.getText().toString(), event_description.getText().toString(),
                        selected_seance, event_created_by, Integer.valueOf(event_limit.getText().toString()));
                eventsReference.push().setValue(event);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
            Intent intent = new Intent(CreateEventActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(CreateEventActivity.this, LoginActivity.class));
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
            Intent intent = new Intent(CreateEventActivity.this, CinemasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(CreateEventActivity.this, SeancesMoviesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(CreateEventActivity.this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(CreateEventActivity.this, StatisticsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(CreateEventActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(CreateEventActivity.this, SearchProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(CreateEventActivity.this, LoginActivity.class));
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
