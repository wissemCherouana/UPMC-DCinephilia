package tpdev.upmc.dcinephila.Beans;

/**
 * Created by Wissou on 22/12/2017.
 */

public class Rate {

    private String cinephile;
    private int element_id;
    private float rating_value;
    private String title;
    private String url;

    public Rate(){}

    public Rate(String cinephile, int element_id, float rating_value) {
        this.cinephile = cinephile;
        this.element_id = element_id;
        this.rating_value = rating_value;
    }

    public Rate(String cinephile, int element_id, float rating_value, String title, String url) {
        this.cinephile = cinephile;
        this.element_id = element_id;
        this.rating_value = rating_value;
        this.title = title;
        this.url = url;
    }

    public Rate(int element_id, float rating_value){
        this.element_id = element_id;
        this.rating_value = rating_value;
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

    public float getRating_value() {
        return rating_value;
    }

    public void setRating_value(float rating_value) {
        this.rating_value = rating_value;
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
}
