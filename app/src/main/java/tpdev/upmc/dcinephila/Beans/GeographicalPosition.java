package tpdev.upmc.dcinephila.Beans;

/**
 * Created by Wissou on 23/12/2017.
 */

public class GeographicalPosition {

    private String cinama_name;
    private String cinema_address;
    private String cinema_town;
    private double cinema_longitude;
    private double cinema_latitude;

    public GeographicalPosition(String cinama_name, String cinema_address, String cinema_town, double cinema_longitude, double cinema_latitude) {
        this.cinama_name = cinama_name;
        this.cinema_address = cinema_address;
        this.cinema_town = cinema_town;
        this.cinema_longitude = cinema_longitude;
        this.cinema_latitude = cinema_latitude;
    }

    public String getCinama_name() {
        return cinama_name;
    }

    public String getCinema_address() {
        return cinema_address;
    }

    public String getCinema_town() {
        return cinema_town;
    }

    public double getCinema_longitude() {
        return cinema_longitude;
    }

    public double getCinema_latitude() {
        return cinema_latitude;
    }
}
