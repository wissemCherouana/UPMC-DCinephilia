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

public class ConsultOtherProfileActivity extends AppCompatActivity{
    private TextView firstname_other_profile, lastname_other_profile, email_other_profile, adress_value;
    private ImageView lists_other,like_other,rate_other ;
    private TextView lists_other_profile, like_other_profile,rate_other_profile;
    private String emailOtherUser;
    private CircleImageView actor_avatar;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        Bundle i = getIntent().getExtras();
        emailOtherUser=i.getString("consultProfile");
        mFirebaseInstance = FirebaseDatabase.getInstance();

        Typeface face_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Bold.ttf");
        firstname_other_profile = (TextView) findViewById(R.id.firstname_other_profile);
        lastname_other_profile = (TextView) findViewById(R.id.lastname_other_profile);
        email_other_profile = (TextView) findViewById(R.id.email_other_profile);
        lists_other=(ImageView)findViewById(R.id.lists_other);
        like_other=(ImageView)findViewById(R.id.like_other);
        rate_other=(ImageView)findViewById(R.id.rate_other) ;
        lists_other_profile =(TextView) findViewById(R.id.lists_other_profile);
        like_other_profile=(TextView) findViewById(R.id.like_other_profile);
        rate_other_profile=(TextView)findViewById(R.id.rate_other_profile);
        adress_value = (TextView) findViewById(R.id.adress_value);
        lists_other_profile.setTypeface(face_bold);
        like_other_profile.setTypeface(face_bold);
        rate_other_profile.setTypeface(face_bold);
        firstname_other_profile.setTypeface(face_bold);
        lastname_other_profile.setTypeface(face_bold);
        email_other_profile.setTypeface(face_bold);
        adress_value.setTypeface(face_bold);
        actor_avatar = (CircleImageView) findViewById(R.id.avatar);
        GetUserInformations();



        lists_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayListsOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });

        lists_other_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayListsOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });

        rate_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayRatesOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });

        rate_other_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayRatesOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });

        like_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayLikeOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });

        like_other_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConsultOtherProfileActivity.this, DisplayLikeOtherUserActivity.class).putExtra("emailOtherUser",emailOtherUser));
            }
        });


    }


    private void GetUserInformations() {
        mFirebaseDatabase = mFirebaseInstance.getReference("cinephiles");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for(DataSnapshot c : child.getChildren()){
                        if(c.getValue().equals(emailOtherUser)){
                            Cinephile cinp = child.getValue(Cinephile.class);
                            firstname_other_profile.setText(cinp.getFirstname());
                            lastname_other_profile.setText(cinp.getLastname().toUpperCase());
                            email_other_profile.setText(cinp.getEmail());
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
