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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.Activities.ConsultOtherProfileActivity;
import tpdev.upmc.dcinephila.R;

public class SearchProfileAdapter extends ArrayAdapter<String> {

    final int layoutResource;
    private final List<String> myLists;
    private Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<String> a = new ArrayList<String>();
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
    }


    public SearchProfileAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
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
        }
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Light.ttf");

        SearchProfileAdapter.ViewHolder holder = (SearchProfileAdapter.ViewHolder) convertView.getTag();
        final String todo = getItem(position);
        holder.textView.setText(todo);
        holder.textView.setTypeface(face);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = myLists.get(position).toString();
                Intent intent = new Intent(context.getApplicationContext(), ConsultOtherProfileActivity.class).putExtra("consultProfile", mail);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
        return convertView;
    }


    private View createView() {
        // Create item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);

        SearchProfileAdapter.ViewHolder holder = new SearchProfileAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.title);

        view.setTag(holder);
        return view;
    }

}