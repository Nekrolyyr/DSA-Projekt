package hsr.dsa.core;

public class ShipSpotNotFreeException extends Exception {
    private final String position;

    public ShipSpotNotFreeException(String position){
        this.position = position;
    }
    @Override
    public String toString() {
        return "The Spot "+position+" is not Free!\n"+super.toString();
    }
}
