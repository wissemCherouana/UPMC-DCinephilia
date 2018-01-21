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

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.Beans.Like;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.R;

import static android.content.ContentValues.TAG;

public class ElementLikeAdapter extends ArrayAdapter<Like> {
    final int layoutResource;
    private final List<Like> myLists;
    private Context context;
    ElementLikeAdapter.ViewHolder holder;
    // Get Firebase database instance

    private static class ViewHolder {
        TextView textView;
        TextView date;
        ImageView image;
    }


    public ElementLikeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Like> objects) {
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

        holder = (ElementLikeAdapter.ViewHolder) convertView.getTag();
        final Like like = getItem(position);
        holder.textView.setText(like.getTitle());
        holder.textView.setTypeface(face_bold);
        holder.date.setText("Sorti le : " + like.getDate());
        holder.date.setTypeface(face);
        Glide.with(getContext()).load(like.getUrl()).into(holder.image);


        return convertView;
    }


    private View createView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);
        ElementLikeAdapter.ViewHolder holder = new ElementLikeAdapter.ViewHolder();
        holder.textView = view.findViewById(R.id.movie_title);
        holder.date=view.findViewById(R.id.date_of);
        holder.image=view.findViewById(R.id.poster);
        view.setTag(holder);
        return view;
    }

}
