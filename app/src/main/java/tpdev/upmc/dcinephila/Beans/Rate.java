package tpdev.upmc.dcinephila.Beans;

/**
 * Created by Wissou on 22/12/2017.
 */

public class Rate {

    private String cinephile;
    private int element_id;
    private float rating_value;

    public Rate(){}

    public Rate(String cinephile, int element_id, float rating_value) {
        this.cinephile = cinephile;
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
}
