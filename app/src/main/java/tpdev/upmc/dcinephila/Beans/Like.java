package tpdev.upmc.dcinephila.Beans;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wissou on 21/12/2017.
 */
@IgnoreExtraProperties
public class Like {

    private String cinephile;
    private int element_id;
    private String title;
    private String url;
    private String date;

    public Like(){}

    public Like(String cinephile, int element_id) {
        this.cinephile = cinephile;
        this.element_id = element_id;
    }

    public Like(String cinephile, int element_id, String title, String url, String date) {
        this.cinephile = cinephile;
        this.element_id = element_id;
        this.title = title;
        this.url = url;
        this.date = date;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
