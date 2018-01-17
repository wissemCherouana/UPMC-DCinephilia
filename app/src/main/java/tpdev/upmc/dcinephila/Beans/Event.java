package tpdev.upmc.dcinephila.Beans;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wissou on 24/12/2017.
 */
@IgnoreExtraProperties
public class Event {

    private String event_id;
    private String event_name;
    private String event_description ;
    private Seance event_seance;
    private Cinephile event_created_by;
    private int event_limit;

    public Event(){}

    public Event(String event_id, String event_name, String event_description, Seance event_seance, Cinephile event_created_by, int event_limit) {
        this.event_id = event_id;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_seance = event_seance;
        this.event_created_by = event_created_by;
        this.event_limit = event_limit;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_description() {
        return event_description;
    }

    public Seance getEvent_seance() {
        return event_seance;
    }

    public Cinephile getEvent_created_by() {
        return event_created_by;
    }

    public int getEvent_limit() {
        return event_limit;
    }
}
