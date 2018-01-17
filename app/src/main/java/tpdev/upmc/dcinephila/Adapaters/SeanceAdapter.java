package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tpdev.upmc.dcinephila.Beans.Seance;
import tpdev.upmc.dcinephila.R;

/**
 * Created by Wissou on 24/12/2017.
 */

public class SeanceAdapter extends RecyclerView.Adapter<SeanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<Seance> seancesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, place;
        public ImageView poster;
        public CardView cardview;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            place = (TextView) view.findViewById(R.id.place);
            poster = (ImageView) view.findViewById(R.id.poster);
            cardview = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public SeanceAdapter(Context mContext, List<Seance> seancesList) {
        this.mContext = mContext;
        this.seancesList = seancesList;
    }

    @Override
    public SeanceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seance_card, parent, false);

        return new SeanceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SeanceAdapter.MyViewHolder holder, final int position) {
        Seance seance = seancesList.get(position);
        Typeface face= Typeface.createFromAsset(mContext.getAssets(), "font/Comfortaa-Light.ttf");

        holder.date.setTypeface(face);

        holder.title.setText(seance.getMovie());
        holder.date.setText(seance.getSeance_date());
        holder.place.setText("\uD83D\uDCCC" + " " +seance.getSeance_place());

        // loading album cover using Glide library
        Glide.with(mContext).load("https://image.tmdb.org/t/p/w500" + seance.getMovie_poster()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return seancesList.size();
    }

}
