package org.example.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value={ "_closed", "_claimed", "_index", "_id", "_score"}, allowGetters=true)
public class Business {

    // ------------------------------ Fields ------------------------------------ //
    private String id;
    private String distance;
    private String alias;
    private String name;
    private String image_url;
    private String url;
    private String phone;
    private String display_phone;
    public Double open_at;
    private String price;
    private String timestamp;
    private Boolean is_claimed;
    public Boolean open_now;
    private Boolean is_closed;
    private Integer review_count;
    private Double rating;
    private Location location;
    private Coordinates coordinates;
    private List<String> transactions;
    private List<String> photos;
    private List<String> attributes;
    private List<Hours> hours;
    private List<Category> categories;
    private Object messaging;

    private List<Object> special_hours;

    public Business() {
    }


    public List<Object> getSpecial_hours() {
        return special_hours;
    }

    public void setSpecial_hours(List<Object> special_hours) {
        this.special_hours = special_hours;
    }

    public Object getMessaging() {
        return messaging;
    }

    public void setMessaging(Object messaging) {
        this.messaging = messaging;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplay_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public Double getOpen_at() {
        return open_at;
    }

    public void setOpen_at(Double open_at) {
        this.open_at = open_at;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIs_claimed() {
        return is_claimed;
    }

    public void setIs_claimed(Boolean is_claimed) {
        this.is_claimed = is_claimed;
    }

    public Boolean getOpen_now() {
        return open_now;
    }

    public void setOpen_now(Boolean open_now) {
        this.open_now = open_now;
    }

    public Boolean getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(Boolean is_closed) {
        this.is_closed = is_closed;
    }

    public Integer getReview_count() {
        return review_count;
    }

    public void setReview_count(Integer review_count) {
        this.review_count = review_count;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<Hours> getHours() {
        return hours;
    }

    public void setHours(List<Hours> hours) {
        this.hours = hours;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
