package tpdev.upmc.dcinephila.DAO;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import tpdev.upmc.dcinephila.Beans.Cinephile;

/**
 * Created by Wissou on 21/12/2017.
 */

public class CinephileDAO {

    private DatabaseReference cinephilesReference;
    private FirebaseDatabase DCinephiliaInstance;

    public CinephileDAO()
    {
        DCinephiliaInstance = FirebaseDatabase.getInstance();
    }

    public Cinephile GetCinephileByEmail(final String email)
    {
        Cinephile cinephile_found = null;
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

                    if (cinephile.getEmail().equals(email))
                    {

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return cinephile_found;
    }
}
