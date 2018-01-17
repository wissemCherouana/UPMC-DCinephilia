package tpdev.upmc.dcinephila.DesignClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tpdev.upmc.dcinephila.R;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {

    public static void setupItem(final View view, final EventItem eventItem, final Context context) {

        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Light.ttf");
        Typeface face_bold= Typeface.createFromAsset(context.getAssets(), "font/Comfortaa-Bold.ttf");

        final TextView event_name = (TextView) view.findViewById(R.id.event_name);
        final TextView event_movie = (TextView) view.findViewById(R.id.event_movie);
        final TextView event_description = (TextView) view.findViewById(R.id.event_description);
        final TextView event_date = (TextView) view.findViewById(R.id.event_date);
        final TextView event_hour = (TextView) view.findViewById(R.id.event_hour);
        final TextView event_place = (TextView) view.findViewById(R.id.event_place);
        final TextView event_limit = (TextView) view.findViewById(R.id.event_limit);
        final TextView event_created_by = (TextView) view.findViewById(R.id.event_created_by);
        final ImageView event_poster = (ImageView) view.findViewById(R.id.event_poster);


        event_name.setTypeface(face_bold);
        event_description.setTypeface(face);
        event_movie.setTypeface(face_bold);
        event_date.setTypeface(face);
        event_hour.setTypeface(face);
        event_place.setTypeface(face);
        event_limit.setTypeface(face);
        event_created_by.setTypeface(face);

        event_name.setText(eventItem.getEvent_name());
        event_created_by.setText("Créé par : " + eventItem.getEvent_created_by());
        event_movie.setText("\uD83C\uDFA5 " + eventItem.getEvent_movie());
        event_description.setText(eventItem.getEvent_description());
        event_date.setText("\uD83D\uDCC6 " + eventItem.getEvent_date());
        event_hour.setText("\uD83D\uDD51 " + eventItem.getEvent_hour());
        event_place.setText("\uD83D\uDCCC " + eventItem.getEvent_place());
        event_limit.setText("\uD83D\uDC6F " + "Nombre maximal : " + String.valueOf(eventItem.getEvent_limit())+ " participants");
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + eventItem.getEvent_poster()).into(event_poster);
    }

    public static class EventItem {

        private String event_id;
        private String event_name;
        private String event_movie;
        private String event_poster;
        private String event_description;
        private String event_date;
        private String event_hour;
        private String event_place;
        private String event_created_by;
        private int event_limit;

        public EventItem(final String event_id, final String event_name, final String event_movie, final String event_poster, final String event_description,
               final String event_date, final String event_hour, final String event_place, final int event_limit, final String event_created_by  ) {
            this.event_id = event_id;
            this.event_name = event_name;
            this.event_movie = event_movie;
            this.event_poster = event_poster;
            this.event_description = event_description;
            this.event_date = event_date;
            this.event_hour = event_hour;
            this.event_place = event_place;
            this.event_limit = event_limit;
            this.event_created_by = event_created_by;
        }

        public String getEvent_id() {
            return event_id;
        }

        public String getEvent_name() {
            return event_name;
        }

        public String getEvent_movie() {
            return event_movie;
        }

        public String getEvent_poster() {
            return event_poster;
        }

        public String getEvent_description() {
            return event_description;
        }

        public String getEvent_date() {
            return event_date;
        }

        public String getEvent_hour() {
            return event_hour;
        }

        public String getEvent_place() {
            return event_place;
        }

        public String getEvent_created_by() {
            return event_created_by;
        }

        public int getEvent_limit() {
            return event_limit;
        }
    }
}
