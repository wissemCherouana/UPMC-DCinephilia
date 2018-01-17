package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DisplayElementCommentsActivity extends AppCompatActivity {

    private DatabaseReference elementsCommentsReference, cinephilesReference;
    private TextView comments_text, comments_number, comment;
    private ImageButton add_comment_btn;
    private FirebaseDatabase DCinephiliaInstance;
    private ListView comments_listview ;
    private ArrayList<Comment> comments_list;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_element_comments);

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

    public void AddCommentAboutMovie(final String element_genre, final String comment_content, final int movie_id)
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
                        elementsCommentsReference = DCinephiliaInstance.getReference(element_genre);
                        Comment comment = new Comment(comment_content,new Date(), cinephile.getFirstname() +
                                " " + cinephile.getLastname().toUpperCase()  , movie_id);
                        elementsCommentsReference.child(String.valueOf(movie_id)).push().setValue(comment);
                    }
                }
                DisplayComments(element_genre, movie_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

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


}
