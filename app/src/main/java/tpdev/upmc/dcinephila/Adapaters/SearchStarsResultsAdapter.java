package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tpdev.upmc.dcinephila.Beans.Actor;
import tpdev.upmc.dcinephila.R;

/**
 * Created by Wissou on 24/12/2017.
 */

public class SearchStarsResultsAdapter extends RecyclerView.Adapter<SearchStarsResultsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Actor> actorsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView profile_picture;
        public CardView cardview;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            profile_picture = (ImageView) view.findViewById(R.id.poster);
            cardview = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public SearchStarsResultsAdapter(Context mContext, List<Actor> actorsList) {
        this.mContext = mContext;
        this.actorsList = actorsList;
    }

    @Override
    public SearchStarsResultsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_star_card, parent, false);

        return new SearchStarsResultsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchStarsResultsAdapter.MyViewHolder holder, final int position) {
        Actor actor = actorsList.get(position);
        holder.name.setText(actor.getActor_name());
        Glide.with(mContext).load(actor.getProfile_picture()).into(holder.profile_picture);
    }

    @Override
    public int getItemCount() {
        return actorsList.size();
    }

}
