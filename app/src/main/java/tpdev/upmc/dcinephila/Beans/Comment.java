package tpdev.upmc.dcinephila.Beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wissou on 20/12/2017.
 */

public class Comment {


    private String comment_content;
    private Date comment_date;
    private String cinephile_id;
    private int element_id;
    private String comment_thumbnail ;

    public Comment(){}

    public Comment(String comment_content, Date comment_date, String cinephile_id, int element_id) {
        this.comment_content = comment_content;
        this.comment_date = comment_date;
        this.cinephile_id = cinephile_id;
        this.element_id = element_id;
    }

    public Comment(String comment_content, Date comment_date, String cinephile_id, int element_id, String comment_thumbnail) {
        this.comment_content = comment_content;
        this.comment_date = comment_date;
        this.cinephile_id = cinephile_id;
        this.element_id = element_id;
        this.comment_thumbnail = comment_thumbnail;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public String getCinephile_id() {
        return cinephile_id;
    }

    public void setCinephile_id(String cinephile_id) {
        this.cinephile_id = cinephile_id;
    }

    public int getElement_id() {
        return element_id;
    }

    public void setElement_id(int element_id) {
        this.element_id = element_id;
    }

    public String getComment_thumbnail() {
        return comment_thumbnail;
    }

    public void setComment_thumbnail(String comment_thumbnail) {
        this.comment_thumbnail = comment_thumbnail;
    }
}
