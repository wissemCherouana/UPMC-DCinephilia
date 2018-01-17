package tpdev.upmc.dcinephila.DesignClasses.ArticleSlider;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import tpdev.upmc.dcinephila.R;


public class SliderCard extends RecyclerView.ViewHolder {

    private Context mContext;
    private static int viewWidth = 0;
    private static int viewHeight = 0;

    private final ImageView imageView;

    private DecodeBitmapTask task;

    public SliderCard(Context context, View itemView) {
        super(itemView);
        mContext = context;
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    void setContent(String url_image) {
        Glide.with(mContext).load(url_image).into(imageView);
    }


}