package tpdev.upmc.dcinephila.Beans;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Wissou on 24/12/2017.
 */

@IgnoreExtraProperties
public class Seance {

    private int element_id;
    private String movie;
    private String movie_poster;
    private String seance_date;
    private String seance_place;


    public Seance(){}

    public Seance(int element_id, String movie, String movie_poster, String seance_date, String seance_place) {
        this.element_id = element_id;
        this.movie = movie;
        this.movie_poster = movie_poster;
        this.seance_date = seance_date;
        this.seance_place = seance_place;
    }

    public int getElement_id() {
        return element_id;
    }

    public String getMovie() {
        return movie;
    }

    public String getMovie_poster() {
        return movie_poster;
    }

    public String getSeance_date() {
        return seance_date;
    }

    public String getSeance_place() {
        return seance_place;
    }
}
