package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.Activities.DisplayElementsListOtherUserActivity;
import tpdev.upmc.dcinephila.Beans.ElementList;
import tpdev.upmc.dcinephila.R;

public class ElementListOtherUserAdapter extends ArrayAdapter<ElementList> {
    final int layoutResource;
    private final List<ElementList> myLists;
    private Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<String> a = new ArrayList<String>();
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
        TextView date;
        TextView genre;
        ImageView image;
    }


    public ElementListOtherUserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ElementList> objects) {
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

        ElementListOtherUserAdapter.ViewHolder holder = (ElementListOtherUserAdapter.ViewHolder) convertView.getTag();
        final ElementList element = getItem(position);
        holder.textView.setText(element.getName());
        if (element.getType().equals("movie")) {
            holder.date.setText("Sorti le : " + element.getDate());
            holder.genre.setText("Film");
        } else if (element.getType().equals("shows")) {
            holder.date.setText("1ère diffusion : " + element.getDate());
            holder.genre.setText("Série TV");
        }
        Glide.with(getContext()).load(element.getUrl()).into(holder.image);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = myLists.get(position).toString();
                Intent intent = new Intent(context.getApplicationContext(), DisplayElementsListOtherUserActivity.class);
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
        ElementListOtherUserAdapter.ViewHolder holder = new ElementListOtherUserAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.movie_title);
        holder.date = view.findViewById(R.id.date_of);
        holder.image = view.findViewById(R.id.poster);
        holder.genre = view.findViewById(R.id.genre);
        view.setTag(holder);
        return view;
    }


}
