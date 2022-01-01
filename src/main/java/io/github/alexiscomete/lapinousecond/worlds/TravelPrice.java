package io.github.alexiscomete.lapinousecond.worlds;

import java.util.Optional;

public class TravelPrice {

    private final double price;
    private final double totalPrice;
    private final Double taxe;
    private final Double distanceCoos;
    private final Double distanceID;

    public TravelPrice(double price, double totalPrice, Double taxe, Double distanceCoos, Double distanceID) {
        this.price = price;
        this.totalPrice = totalPrice;
        this.taxe = taxe;
        this.distanceCoos = distanceCoos;
        this.distanceID = distanceID;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Optional<Double> getTaxe() {
        return Optional.ofNullable(taxe);
    }

    public Optional<Double> getDistanceCoos() {
        return Optional.ofNullable(distanceCoos);
    }

    public Optional<Double> getDistanceID() {
        return Optional.ofNullable(distanceID);
    }

    public static TravelPrice getTravelPrice(ServerBot serv1, ServerBot serv2) {
        double distanceID = Math.abs(serv1.getId() - serv2.getId());
        double price = Math.sqrt(distanceID) / 1000000;
        return new TravelPrice(price, price, null, null, distanceID);
    }

    public static TravelPrice getTravelPrice(ServerBot serv1, ServerBot serv2, World world) {
        double distanceID = Math.abs(serv1.getId() - serv2.getId());
        double price = Math.sqrt(distanceID) / 1000000;
        double taxe = world.getTravelPrice();
        return new TravelPrice(price, price + taxe, taxe, null, distanceID);
    }

    public static TravelPrice getTravelPrice(Place place1, Place place2) {
        if (place1.getWorld() == place2.getWorld()) {
            if (place1.getWorld() instanceof WorldWithCoos) {

            } else {

            }
        } else {

        }
    }
}
