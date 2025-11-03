package model;

public class Hotel {
    private int id;
    private String name;
    private String city;
    private double rating;
    private String address;

    public Hotel(int id, String name, String city, double rating, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.rating = rating;
        this.address = address;
    }
    public int getId()
    { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-15s | %.1f/5.0 | %s",
                id, name, city, rating, address);
    }
}