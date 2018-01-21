package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Beans.Rate;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.R;

import static android.content.ContentValues.TAG;

public class ElementRateAdapter extends ArrayAdapter<Rate> {
    final int layoutResource;
    private final List<Rate> myLists;
    private Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String movie,movie_id;
    private String release_date,url_movie;
    ElementRateAdapter.ViewHolder holder;
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
        TextView rate;
        ImageView image;
    }


    public ElementRateAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Rate> objects) {
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

        holder = (ElementRateAdapter.ViewHolder) convertView.getTag();
        holder.textView.setTypeface(face_bold);
        holder.rate.setTypeface(face);
        final Rate rate = getItem(position);
        holder.textView.setText(rate.getTitle());
        holder.rate.setText(String.valueOf(rate.getRating_value())+"/10");
        Glide.with(getContext()).load(rate.getUrl()).into(holder.image);

        return convertView;
    }


    private View createView() {
        // Create item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);
        ElementRateAdapter.ViewHolder holder = new ElementRateAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.movie_title);
        holder.rate=view.findViewById(R.id.rate_of);
        holder.image=view.findViewById(R.id.poster);
        view.setTag(holder);
        return view;
    }

}
