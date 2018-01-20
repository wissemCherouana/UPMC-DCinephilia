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

/**
 * Created by Sourour Bnll on 19/01/2018.
 */

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
        // on se charge ici de get tt les trucs de name date et ect...
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

    private void GetMovieDetails(final String urlJsonObj) {
        System.out.println("iciii");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {

                    movie=response.getString("title");
                    holder.textView.setText(movie);
                    System.out.println("le titre"+response.getString("title"));
                    try {
                        if (response.getString("release_date")!=null)
                            release_date = response.getString("release_date");
                        System.out.println("la data"+response.getString("release_date"));
                    }
                    catch (Exception e){
                        release_date="";
                    }

                    // Getting movie runtime
                    System.out.println("la url  "+response.getString("poster_path"));
                    url_movie="https://image.tmdb.org/t/p/w500" + response.getString("poster_path");

                    Glide.with(getContext()).load("https://image.tmdb.org/t/p/w500" + response.getString("poster_path")).into(holder.image);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
