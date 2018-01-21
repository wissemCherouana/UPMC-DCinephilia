package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import tpdev.upmc.dcinephila.Adapaters.CommentAdapter;
import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Comment;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.DesignClasses.Utility;
import tpdev.upmc.dcinephila.R;

/**
 * This activity allows the cinephile to display all the comments that where posted on a movie or a tv show
 */

public class DisplayElementCommentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference elementsCommentsReference, cinephilesReference;
    private TextView comments_text, comments_number, comment;
    private ImageButton add_comment_btn;
    private FirebaseDatabase DCinephiliaInstance;
    private ListView comments_listview ;
    private ArrayList<Comment> comments_list;
    private CommentAdapter commentAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_element_comments);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        final int element_id = intent.getIntExtra("element_id", 0);
        final String element_genre = intent.getStringExtra("element_genre");

        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");


        DCinephiliaInstance = FirebaseDatabase.getInstance();

        comments_text = (TextView) findViewById(R.id.comments_text);
        comments_number = (TextView) findViewById(R.id.comments_number);
        add_comment_btn = (ImageButton) findViewById(R.id.btn_add_comment);
        comments_listview = (ListView) findViewById(R.id.comments_list);
        comment = (TextView) findViewById(R.id.comment);

        comment.setTypeface(face);
        comments_number.setTypeface(face);
        comments_text.setTypeface(face_bold);

        add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment.getText().equals(""))
                {
                    if (element_genre.equals("movie")) AddCommentAboutMovie("movies_comments", comment.getText().toString(), element_id);
                    else if (element_genre.equals("show")) AddCommentAboutMovie("shows_comments", comment.getText().toString(), element_id);
                    comment.setText("");
                }
            }
        });

        if (element_genre.equals("movie")) DisplayComments("movies_comments", element_id);
        else if (element_genre.equals("show")) DisplayComments("shows_comments", element_id);
    }

    /**
     * This method adds a comment about a movie or a show
     * @param element_genre comment about a movie or a show
     * @param comment_content the content of the comment
     * @param movie_id the id of the movie or the show
     */
    public void AddCommentAboutMovie(final String element_genre, final String comment_content, final int movie_id)
    {
        // get the list of cinephiles in order to get who posts the comment
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
                        // display the thumbnail of the cinephile who posted the comment,it depends on gender
                        String thumbnail = "";
                        if (cinephile.getSexe().equals("Femme"))
                            thumbnail = "https://icon-icons.com/icons2/582/PNG/512/girl_icon-icons.com_55043.png";
                        else thumbnail = "https://cdn1.iconfinder.com/data/icons/user-pictures/100/boy-512.png";

                        elementsCommentsReference = DCinephiliaInstance.getReference(element_genre);

                        // create a new comment
                        Comment comment = new Comment(comment_content,new Date(), cinephile.getFirstname() +
                                " " + cinephile.getLastname().toUpperCase()  , movie_id, thumbnail);

                        // push the comment to the reference of comments on firebase
                        elementsCommentsReference.child(String.valueOf(movie_id)).push().setValue(comment);
                    }
                }
                // diplay the comment on the listview
                DisplayComments(element_genre, movie_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Display a comment on the listview
     * @param element_genre
     * @param element_id
     */
    public void DisplayComments(String element_genre, int element_id)
    {
        elementsCommentsReference = DCinephiliaInstance.getReference(element_genre+"/"+String.valueOf(element_id));
        elementsCommentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments_list = new ArrayList<>();
                commentAdapter = new CommentAdapter(getApplicationContext(), R.layout.comment_row, comments_list);
                comments_listview.setAdapter(commentAdapter);
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        Comment comment = singleSnapshot.getValue(Comment.class);

                        comments_list.add(comment);
                        commentAdapter.notifyDataSetChanged();
                    }
                    comments_number.setText("•  " +String.valueOf(comments_list.size()));
                }

                Utility.setListViewHeightBasedOnChildren(comments_listview);
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
            Intent intent = new Intent(DisplayElementCommentsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(DisplayElementCommentsActivity.this, LoginActivity.class));
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
            Intent intent = new Intent(DisplayElementCommentsActivity.this, CinemasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(DisplayElementCommentsActivity.this, SeancesMoviesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(DisplayElementCommentsActivity.this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(DisplayElementCommentsActivity.this, StatisticsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(DisplayElementCommentsActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(DisplayElementCommentsActivity.this, SearchProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.disconnect) {
            auth.signOut();
            startActivity(new Intent(DisplayElementCommentsActivity.this, LoginActivity.class));
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
