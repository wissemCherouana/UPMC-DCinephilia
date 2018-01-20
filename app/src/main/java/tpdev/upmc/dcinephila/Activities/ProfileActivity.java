package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.R;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView firtsname_profile_text, lastname_profile_text, email_profile_text, adress_value, description;
    private ImageView event_view,lists_view,edit_view, rates_view, likes_view;
    private TextView event_text, lists_text,edit_text,like_text,rate_text;
    private CircleImageView actor_avatar;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view now
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();

        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");
        firtsname_profile_text = (TextView) findViewById(R.id.firstname_profile);
        lastname_profile_text = (TextView) findViewById(R.id.lastname_profile);
        email_profile_text = (TextView) findViewById(R.id.email_profile);
        adress_value = (TextView) findViewById(R.id.adress_value);
        event_view=(ImageView)findViewById(R.id.event);
        lists_view=(ImageView)findViewById(R.id.lists);
        edit_view=(ImageView)findViewById(R.id.update);
        rates_view =(ImageView) findViewById(R.id.rates_profile);
        likes_view =(ImageView)findViewById(R.id.likes_profile);
        lists_text =(TextView) findViewById(R.id.create_lists);
        event_text=(TextView) findViewById(R.id.create_event);
        like_text=(TextView)findViewById(R.id.like_lists);
        rate_text=(TextView)findViewById(R.id.rate_lists);
        edit_text=(TextView)findViewById(R.id.update_lists) ;
        adress_value.setTypeface(face);
        lists_text.setTypeface(face_bold);
        rate_text.setTypeface(face_bold);
        like_text.setTypeface(face_bold);
        event_text.setTypeface(face_bold);
        edit_text.setTypeface(face_bold);
        firtsname_profile_text.setTypeface(face_bold);
        lastname_profile_text.setTypeface(face_bold);
        email_profile_text.setTypeface(face_bold);
        adress_value.setTypeface(face_bold);
        actor_avatar = (CircleImageView) findViewById(R.id.avatar);
        GetUserInformations();



        event_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, CreateEventActivity.class));
            }
        });

        event_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, CreateEventActivity.class));
            }
        });

        lists_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayListsActivity.class));
            }
        });

        lists_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayListsActivity.class));
            }
        });

        likes_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayLikesActivity.class));
            }
        });

        like_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayLikesActivity.class));
            }
        });

        rates_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayRatesActivity.class));
            }
        });

        rate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DisplayRatesActivity.class));
            }
        });

        edit_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                finish();
            }
        });

        edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                finish();
            }
        });

    }


    private void GetUserInformations() {
        adress_value.setText("");
        final String userRecord = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDatabase = mFirebaseInstance.getReference("cinephiles");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for(DataSnapshot c : child.getChildren()){
                        if(c.getValue().equals(userRecord)){
                            Cinephile cinp = child.getValue(Cinephile.class);
                            firtsname_profile_text.setText(cinp.getFirstname());
                            lastname_profile_text.setText(cinp.getLastname().toUpperCase());
                            email_profile_text.setText(cinp.getEmail());
                            adress_value.setText(cinp.getDescription());
                            String thumbnail = "";
                            if (cinp.getSexe().equals("Femme"))
                                thumbnail = "https://icon-icons.com/icons2/582/PNG/512/girl_icon-icons.com_55043.png";
                            else thumbnail = "https://cdn1.iconfinder.com/data/icons/user-pictures/100/boy-512.png";
                            Glide.with(getApplicationContext()).load(thumbnail).into(actor_avatar);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}