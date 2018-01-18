package tpdev.upmc.dcinephila.Adapaters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import tpdev.upmc.dcinephila.Beans.Cinephile;
import tpdev.upmc.dcinephila.Beans.Comment;
import tpdev.upmc.dcinephila.R;

/**
 * Created by Wissou on 21/12/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    final int layoutResource;
    private Context mContext;
    private final List<Comment> commens;

    private static class ViewHolder {
        ImageView thumbnail;
        TextView cinephile;
        TextView date;
        TextView content;
    }

    public CommentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Comment> comments) {
        super(context, resource, comments);
        layoutResource = resource;
        mContext = context;
        this.commens = comments;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = createView();
        }
        final Comment comment = getItem(position);
        ViewHolder holder = (ViewHolder)convertView.getTag();

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Typeface face= Typeface.createFromAsset(getContext().getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(getContext().getAssets(), "font/Comfortaa-Bold.ttf");

        holder.cinephile.setTypeface(face_bold);
        holder.date.setTypeface(face);
        holder.content.setTypeface(face);

        holder.cinephile.setText(comment.getCinephile_id());
        holder.date.setText(df.format(comment.getComment_date()));
        holder.content.setText(comment.getComment_content());
        Glide.with(mContext).load(comment.getComment_thumbnail()).into(holder.thumbnail);

        return convertView;
    }

    private View createView(){
        // Create event_card
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);

        ViewHolder holder = new ViewHolder();
        holder.cinephile = view.findViewById(R.id.cinephile);
        holder.date = view.findViewById(R.id.date);
        holder.content = view.findViewById(R.id.content);
        holder.thumbnail = view.findViewById(R.id.thumbnail);

        view.setTag(holder);
        return view;
    }

}