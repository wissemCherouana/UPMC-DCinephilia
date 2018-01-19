package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.Activities.DisplayElementsListsActivity;
import tpdev.upmc.dcinephila.Beans.ElementList;
import tpdev.upmc.dcinephila.R;

/**
 * Created by Sourour Bnll on 03/01/2018.
 */

public class ElementListAdapter extends ArrayAdapter<ElementList> {
    final int layoutResource;
    private final List<ElementList> myLists;
    private Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<String> a = new ArrayList<String>();
    final String userRecord = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
        TextView date;
        ImageView trash;
        ImageView image;
    }


    public ElementListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ElementList> objects) {
        super(context, resource, objects);
        this.context = context;
        layoutResource = resource;
        myLists = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = createView();
            notifyDataSetChanged();
        }
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Bold.ttf");
        ElementListAdapter.ViewHolder holder = (ElementListAdapter.ViewHolder) convertView.getTag();
        holder.textView.setTypeface(face_bold);
        holder.date.setTypeface(face);
        final ElementList element = getItem(position);
        holder.textView.setText(element.getName());
        if(element.getType().equals("movie")){
            holder.date.setText("Sorti le : "+ element.getDate());
        }
        Glide.with(getContext()).load(element.getUrl()).into(holder.image);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = myLists.get(position).toString();
                Intent intent = new Intent(context.getApplicationContext(), DisplayElementsListsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });

       /* holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElementList element = myLists.get(position);
                myLists.remove(position);
                myLists.clear();
                removeList(element.getName());
                notifyDataSetChanged();
            }
        });*/

        return convertView;
    }


    private View createView() {
        // Create item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);
        ElementListAdapter.ViewHolder holder = new ElementListAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.movie_title);
        holder.date=view.findViewById(R.id.date_of);
       // holder.trash=view.findViewById(R.id.deleteBtn);
        holder.image=view.findViewById(R.id.poster);
        view.setTag(holder);
        return view;
    }

    private void removeList(final String t) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("elements_lists/"+userRecord);
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    ElementList element = child.getValue(ElementList.class);
                    if(element.getName().equals(t)){
                        child.getRef().removeValue();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}