package tpdev.upmc.dcinephila.Beans;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wissou on 21/12/2017.
 */
@IgnoreExtraProperties
public class Like {

    private String cinephile;
    private int element_id;

    public Like(){}

    public Like(String cinephile, int element_id) {
        this.cinephile = cinephile;
        this.element_id = element_id;
    }

    public String getCinephile() {
        return cinephile;
    }

    public void setCinephile(String cinephile) {
        this.cinephile = cinephile;
    }

    public int getElement_id() {
        return element_id;
    }

    public void setElement_id(int element_id) {
        this.element_id = element_id;
    }
}
