package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import tpdev.upmc.dcinephila.R;

/**
 * Created by Wissou on 09/12/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

private Context mContext;
private List<String> imagesList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView poster;
    public CardView cardview;
    public MyViewHolder(View view) {
        super(view);
        poster = (ImageView) view.findViewById(R.id.poster);
        cardview = (CardView) view.findViewById(R.id.card_view);
    }
}


    public ImageAdapter(Context mContext, List<String> imagesList) {
        this.mContext = mContext;
        this.imagesList = imagesList;
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);

        return new ImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.MyViewHolder holder, final int position) {
        String image_url = imagesList.get(position);
        Glide.with(mContext).load(image_url).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

}