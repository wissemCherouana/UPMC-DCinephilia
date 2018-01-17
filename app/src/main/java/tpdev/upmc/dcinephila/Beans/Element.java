package tpdev.upmc.dcinephila.Beans;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wissou on 20/12/2017.
 */
@IgnoreExtraProperties
public class Element {

    private int element_id;
    private String element_genre;

    public Element()
    {

    }

    public Element(int element_id, String element_genre) {
        this.element_id = element_id;
        this.element_genre = element_genre;
    }

    public int getElement_id() {
        return element_id;
    }

    public String getElement_genre() {
        return element_genre;
    }

    public void setElement_genre(String element_genre) {
        this.element_genre = element_genre;
    }
}
