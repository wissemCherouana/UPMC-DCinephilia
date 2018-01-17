package tpdev.upmc.dcinephila.DesignClasses.ArticleSlider;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tpdev.upmc.dcinephila.Adapaters.ActorAdapter;
import tpdev.upmc.dcinephila.Beans.Actor;
import tpdev.upmc.dcinephila.R;


public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder>  {

    private final ArrayList<String> content;
    private final View.OnClickListener listener;
    private Context mContext;

    public SliderAdapter(Context context, ArrayList<String> content, View.OnClickListener listener) {
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public MyViewHolder(View view) {
            super(view);
            picture = (ImageView) view.findViewById(R.id.image);
            picture.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    @Override
    public SliderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        return new SliderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SliderAdapter.MyViewHolder holder, final int position) {
        String url  = content.get(position);
        Glide.with(mContext).load(url).into(holder.picture);
    }


    @Override
    public int getItemCount() {
        return content.size();
    }

}
