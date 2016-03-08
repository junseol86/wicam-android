package com.wicam.a_common_utils.account_related.add_item.select_location;

/**
 * Created by Hyeonmin on 2015-07-27.
 */
public class LocationData {
    private String title;
    private Double latitude, longitude;
    private boolean empty;

    public LocationData(String title, Double latitude, Double longitude, boolean empty) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.empty = empty;
    }

    public String getTitle() {
        return title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public boolean isEmpty() {
        return empty;
    }
}
