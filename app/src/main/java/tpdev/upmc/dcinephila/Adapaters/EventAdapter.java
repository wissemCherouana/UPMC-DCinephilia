package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.Activities.CinemaEventActivity;
import tpdev.upmc.dcinephila.Beans.Event;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.DesignClasses.Utils;
import tpdev.upmc.dcinephila.R;

import static tpdev.upmc.dcinephila.DesignClasses.Utils.setupItem;

/**
 * Created by Wissou on 25/12/2017.
 */

public class EventAdapter extends PagerAdapter {

    private List<Utils.EventItem> eventsList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private DatabaseReference eventsReference, eventsParticipatedReference, eventParticipatedReference;
    private FirebaseDatabase DCinephiliaInstance;
    private boolean[] participated_before;
    public EventAdapter(final Context context, final ArrayList<Utils.EventItem> eventsList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.eventsList = eventsList;
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;

        view = mLayoutInflater.inflate(R.layout.event_card, container, false);
        setupItem(view, eventsList.get(position),mContext);

        participated_before = new boolean[eventsList.size()];
        DCinephiliaInstance = FirebaseDatabase.getInstance();

       final ImageButton participate_event_btn = view.findViewById(R.id.participate_event);
       final ImageButton see_location_btn = view.findViewById(R.id.see_location);

        Resources r = mContext.getResources();
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        for (int i=0; i<eventsList.size(); i++) {
            participated_before = CinephileParticipatedBeforeToTheEvent(eventsList.get(i).getEvent_id(), i);
            System.out.println("******" + participated_before[i]);
        }
        if (participated_before[position])
        {
            participate_event_btn.setBackgroundResource(R.drawable.participate_event_btn);
            participate_event_btn.setImageResource(R.drawable.thumbs_up_blue);
            participate_event_btn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
            participate_event_btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        participate_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] participated=null;
                for (int i=0; i<eventsList.size(); i++)
                {
                    participated = CinephileParticipatedBeforeToTheEvent(eventsList.get(i).getEvent_id(),i);

                    System.out.println("******" + participated_before[i]);
                }
                if (!participated[position])
                {
                    ParticipateEvent(eventsList.get(position).getEvent_id());
                    participate_event_btn.setBackgroundResource(R.drawable.participate_event_btn);
                    participate_event_btn.setImageResource(R.drawable.thumbs_up_blue);
                    participate_event_btn.setPadding(Math.round(px),Math.round(px),Math.round(px),Math.round(px));
                    participate_event_btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                else
                    Toast.makeText(mContext, "Vous participez déjà à cet événement !", Toast.LENGTH_SHORT).show();


            }
        });

        see_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CinemaEventActivity.class);
                intent.putExtra("place", eventsList.get(position).getEvent_place());
                mContext.startActivity(intent);

            }
        });

        container.addView(view);
        return view;
    }

    public void ParticipateEvent(final String event_id)
    {
            eventsParticipatedReference= DCinephiliaInstance.getReference("events_participated");
            eventsReference = DCinephiliaInstance.getReference("events");
            eventsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                    {
                        Event event = singleSnapshot.getValue(Event.class);
                        if (event.getEvent_id().equals(event_id))
                        {
                            eventsParticipatedReference.child(event_id).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    public boolean[] CinephileParticipatedBeforeToTheEvent(final String event_id, final int position)
    {
        eventParticipatedReference = DCinephiliaInstance.getReference("events_participated/"+event_id);
        eventParticipatedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (int i=0; i<eventsList.size();i++) participated_before[i] = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    {
                        String cinephile_email = singleSnapshot.getValue().toString();
                        if (cinephile_email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            participated_before[position] = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        return participated_before;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

}
