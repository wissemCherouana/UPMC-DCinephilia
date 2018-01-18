package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tpdev.upmc.dcinephila.Adapaters.SeanceAdapter;
import tpdev.upmc.dcinephila.Adapaters.SeasonAdapter;
import tpdev.upmc.dcinephila.Beans.Movie;
import tpdev.upmc.dcinephila.Beans.Seance;
import tpdev.upmc.dcinephila.DesignClasses.Calcoid.CaldroidFragment;
import tpdev.upmc.dcinephila.DesignClasses.Calcoid.CaldroidListener;
import tpdev.upmc.dcinephila.DesignClasses.RecyclerTouchListener;
import tpdev.upmc.dcinephila.R;

public class SeancesMoviesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView seancesRecyclerView;
    private SeanceAdapter seanceAdapter;
    private ArrayList<Seance> movies_seances;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    private DatabaseReference seancesMoviesReference;
    private FirebaseDatabase DCinephiliaInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seances_movies);

        DCinephiliaInstance = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        caldroidFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }

        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
        }

        //setCustomResourceForDates();

        try {
            GetMoviesSeances();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        seancesRecyclerView = (RecyclerView) findViewById(R.id.seances_recyclerview);

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                ArrayList<Seance> seances = new ArrayList<>();
                for(int i=0; i<movies_seances.size(); i++)
                {
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String[] date_parts = movies_seances.get(i).getSeance_date().split(" ");
                    Date date_seance=null;
                    try {
                        date_seance = df.parse(date_parts[0]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date.equals(date_seance)) seances.add(movies_seances.get(i));

                }

                seanceAdapter = new SeanceAdapter(getApplicationContext(), seances);
                RecyclerView.LayoutManager CastLayoutManager =
                        new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                seancesRecyclerView.setLayoutManager(CastLayoutManager);
                seancesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                seancesRecyclerView.setAdapter(seanceAdapter);

            }


            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);
        //AddSeancesMovies();

    }

    public void AddSeancesMovies()
    {
        seancesMoviesReference = DCinephiliaInstance.getReference("seances_movies");
        ArrayList<Seance> seancesList = new ArrayList<>();

        seancesList.add(new Seance (181808,"Star Wars, épisode VIII : Les Derniers Jedi","/ijAC17I7X4xCrKxtp1JUc2YBGuM.jpg","26-12-2017 13:55","UGC CINE CITE LES HALLES"));
        seancesList.add(new Seance (181808,"Star Wars, épisode VIII : Les Derniers Jedi","/ijAC17I7X4xCrKxtp1JUc2YBGuM.jpg","27-12-2017 17:30","GRAND REX"));
        seancesList.add(new Seance (181808,"Star Wars, épisode VIII : Les Derniers Jedi","/ijAC17I7X4xCrKxtp1JUc2YBGuM.jpg","30-12-2017 21:50","GAUMONT OPERA CAPUCINES"));
        seancesList.add(new Seance (181808,"Star Wars, épisode VIII : Les Derniers Jedi","/ijAC17I7X4xCrKxtp1JUc2YBGuM.jpg","29-12-2017 19:15","L ALCAZAR"));
        seancesList.add(new Seance (392044,"Le Crime de l'Orient-Express","/a8AwyQ6ueGL9YcddZ6nrZUlRzNt.jpg","27-12-2017 19:50","MK2 ODEON"));
        seancesList.add(new Seance (392044,"Le Crime de l'Orient-Express","/a8AwyQ6ueGL9YcddZ6nrZUlRzNt.jpg","26-12-2017 14:45","UGC LYON BASTILLE"));
        seancesList.add(new Seance (353616,"Pitch Perfect 3","/hQriQIpHUeh66I89gypFXtqEuVq.jpg","27-12-2017 15:40","GAUMONT PARNASSE"));
        seancesList.add(new Seance (353616,"Pitch Perfect 3","/hQriQIpHUeh66I89gypFXtqEuVq.jpg","29-12-2017 20:00","PATHE WEPLER"));
        seancesList.add(new Seance (398933,"Tout là-haut","/sEWJdTh3FF0egFX9KOkB7JK9igx.jpg","27-12-2017 18:00","MEGARAMA 1"));
        seancesList.add(new Seance (398933,"Tout là-haut","/sEWJdTh3FF0egFX9KOkB7JK9igx.jpg","29-12-2017 18:20","ARIEL HAUTS DE RUEIL"));

        ArrayList<String> date_seances = new ArrayList<>();

        for (int i=0; i<seancesList.size();i++)
        {
            String[] date_parts = seancesList.get(i).getSeance_date().split(" ");
            if (!ItContainsDate(date_parts[0],date_seances)) date_seances.add(date_parts[0]);
        }


        for (int i=0; i<seancesList.size();i++)
        {
            for (int j=0; j<date_seances.size(); j++)
            {
                String[] date_parts = seancesList.get(i).getSeance_date().split(" ");
                if (date_seances.get(j).equals(date_parts[0]))
                {
                    seancesMoviesReference.child(date_parts[0]).push().setValue(seancesList.get(i));
                    break;
                }
            }

        }
    }

    public void GetMoviesSeances() throws ParseException {
        seancesMoviesReference = DCinephiliaInstance.getReference("seances_movies");
        seancesMoviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movies_seances = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        for (DataSnapshot snapshot : singleSnapshot.getChildren())
                        {
                            Seance seance = snapshot.getValue(Seance.class);
                            movies_seances.add(seance);
                        }
                    }

                    Calendar cal = null ;
                    Date yellow_date = null;
                    for (int i=0; i<movies_seances.size(); i++)
                    {
                        cal = Calendar.getInstance();
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        String[] date_parts = movies_seances.get(i).getSeance_date().split(" ");

                        cal.add(Calendar.DATE, 7);
                        try {
                            yellow_date = dateFormatter.parse(date_parts[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (caldroidFragment != null) {
                            ColorDrawable green = new ColorDrawable(Color.rgb(137,81,156));
                            caldroidFragment.setBackgroundDrawableForDate(green, yellow_date);
                            caldroidFragment.setTextColorForDate(R.color.white, yellow_date);
                        }
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        t.replace(R.id.calendar, caldroidFragment);
                        t.commit();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }


    private boolean ItContainsDate(String date, ArrayList list)
    {
        boolean found=false;
        int i=0;
        while (!found && i<list.size())
        {
            if (list.get(i).equals(date))
            {
                found = true;
            }
            i++;
        }
        return found;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
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
            Intent intent = new Intent(SeancesMoviesActivity.this, CinemasActivity.class);
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
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}