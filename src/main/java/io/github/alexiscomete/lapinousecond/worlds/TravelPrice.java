package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Player;

import java.util.Optional;

public class TravelPrice {

    private final double price;
    private final double totalPrice;
    private final Integer taxe;
    private final Double distanceCoos;
    private final Long distanceID;

    public TravelPrice(double price, double totalPrice, Integer taxe, Double distanceCoos, Long distanceID) {
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

    public Optional<Integer> getTaxe() {
        return Optional.ofNullable(taxe);
    }

    public Optional<Double> getDistanceCoos() {
        return Optional.ofNullable(distanceCoos);
    }

    public Optional<Long> getDistanceID() {
        return Optional.ofNullable(distanceID);
    }

    public static TravelPrice getTravelPrice(ServerBot serv1, ServerBot serv2) {
        long distanceID = Math.abs(serv1.getId() - serv2.getId());
        double price = Math.sqrt(distanceID) / 1000000;
        return new TravelPrice(price, price, null, null, distanceID);
    }

    public static TravelPrice getTravelPrice(ServerBot serv1, ServerBot serv2, World world) {
        long distanceID = Math.abs(serv1.getId() - serv2.getId());
        double price = Math.sqrt(distanceID) / 1000000;
        int taxe = world.getTravelPrice();
        return new TravelPrice(price, price + taxe, taxe, null, distanceID);
    }

    public static TravelPrice getTravelPrice(Player p, Place place1, Place place2) {
        double price = 0;
        Double distanceCoos = null;
        Integer taxe = null;
        Long distanceID = null;
        if (place1.getWorld() == place2.getWorld()) {
            if (place1.getWorld() instanceof WorldWithCoos) {
                int x1 = place1.getX().isPresent()?place1.getX().get():0;
                int x2 = place2.getX().isPresent()?place2.getX().get():0;
                int y1 = place1.getY().isPresent()?place1.getY().get():0;
                int y2 = place2.getY().isPresent()?place2.getY().get():0;
                distanceCoos = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
            } else {
                long id1 = place1.getServerID().isPresent()?place1.getServerID().get():854288660147994634L;
                long id2 = place2.getServerID().isPresent()?place2.getServerID().get():854288660147994634L;
                distanceID = Math.abs(id1 - id2);
            }
        } else {
            taxe = place2.getWorld().getTravelPrice();
            //TODO : le joueur a des informations comme les différents lieux en fonction du monde ... le prix peut être différent, je pourrais surement enlever place1

        }
        return new TravelPrice(price, price + (taxe==null?0:taxe), taxe, distanceCoos, distanceID);
    }
}
