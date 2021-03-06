package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tpdev.upmc.dcinephila.Beans.Movie;
import tpdev.upmc.dcinephila.R;

/**
 * Created by Wissou on 07/12/2017.
 */

public class SearchMoviesResultsAdapter extends RecyclerView.Adapter<SearchMoviesResultsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, release_date;
        public ImageView poster;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            release_date = (TextView) view.findViewById(R.id.release_date);
            poster = (ImageView) view.findViewById(R.id.poster);;
        }
    }


    public SearchMoviesResultsAdapter(Context mContext, List<Movie> moviesList) {
        this.mContext = mContext;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_movie_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(movie.getMovie_title());
        holder.release_date.setText(movie.getRelease_date());

        // loading album cover using Glide library
        Glide.with(mContext).load(movie.getMovie_poster()).into(holder.poster);


    }



    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
