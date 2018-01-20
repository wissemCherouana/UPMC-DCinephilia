package tpdev.upmc.dcinephila.Activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.R;
import tpdev.upmc.dcinephila.databinding.ActivityUpdateProfileBinding;

public class UpdateProfileActivity extends AppCompatActivity {
    Cinephile user;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private TextView firstname_update, lastname_update, bio_update, password_update, confirmation_update;
    private Button update;
    private RadioGroup radioGroup;
    private String gender = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityUpdateProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        firstname_update = (EditText) findViewById(R.id.input_firstname);
        lastname_update = (EditText) findViewById(R.id.input_lastname);
        bio_update = (EditText) findViewById(R.id.input_bio);
        password_update = (EditText) findViewById(R.id.input_password);
        confirmation_update = (EditText) findViewById(R.id.input_confirmation);
        update = (Button) findViewById(R.id.input_update);
        radioGroup = (RadioGroup) findViewById(R.id.input_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        gender = "Homme";
                        break;
                    case R.id.female:
                        gender = "Femme";
                        break;
                }
            }
        });
        final String userRecord = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDatabase = mFirebaseInstance.getInstance().getReference("cinephiles");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot c : child.getChildren()) {
                        if (c.getValue().equals(userRecord)) {
                            Cinephile cinp = child.getValue(Cinephile.class);
                            user = new Cinephile(cinp.getFirstname(), cinp.getLastname(), cinp.getDescription());
                            binding.setUser(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();

            }
        });
    }

    private void updateUser() {
        final String userRecord = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDatabase = mFirebaseInstance.getInstance().getReference("cinephiles");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot c : child.getChildren()) {
                        if (c.getValue().equals(userRecord)) {
                            if (!firstname_update.getText().toString().equals("")) {
                                child.getRef().child("firstname").setValue(firstname_update.getText().toString().toLowerCase());
                            }
                            if (!lastname_update.getText().toString().equals("")) {
                                child.getRef().child("lastname").setValue(lastname_update.getText().toString().toLowerCase());
                            }

                            if (!bio_update.getText().toString().equals("")) {
                                child.getRef().child("description").setValue(bio_update.getText().toString());
                            }

                            if (!password_update.getText().toString().equals("") && verifypassword(password_update.getText().toString(), confirmation_update.getText().toString())) {
                                child.getRef().child("password").setValue(password_update.getText().toString());
                            }
                            child.getRef().child("gender").setValue(gender);
                            Toast.makeText(getApplicationContext(),
                                    "Modifications prises en compte !",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public boolean verifypassword(String password, String confirmation) {
        if (password.equals(confirmation)) {
            return true;
        }
        return false;
    }
}
