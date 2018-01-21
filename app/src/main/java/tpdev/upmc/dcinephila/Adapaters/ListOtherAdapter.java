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

import tpdev.upmc.dcinephila.Activities.DisplayElementsListOtherUserActivity;
import tpdev.upmc.dcinephila.Beans.ElementList;
import tpdev.upmc.dcinephila.R;

public class ListOtherAdapter extends ArrayAdapter<ElementList> {
    final int layoutResource;
    private final List<ElementList> myLists;
    private Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<String> a = new ArrayList<String>();
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
        ImageView button;
    }


    public ListOtherAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ElementList> objects) {
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
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Bold.ttf");

        ListOtherAdapter.ViewHolder holder = (ListOtherAdapter.ViewHolder) convertView.getTag();
        final ElementList elementList = getItem(position);
        holder.textView.setText(elementList.getMovie());
        holder.textView.setTypeface(face);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ElementList title = myLists.get(position);
                Intent intent = new Intent(context.getApplicationContext(), DisplayElementsListOtherUserActivity.class).putExtra("title", title.getMovie());
                intent.putExtra("emailOtherUser", elementList.getEmail().toString());
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
        ListOtherAdapter.ViewHolder holder = new ListOtherAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.title);
        holder.button = view.findViewById(R.id.deleteBtn);
        view.setTag(holder);
        return view;
    }


}