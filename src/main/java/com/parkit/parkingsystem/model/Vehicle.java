package com.parkit.parkingsystem.model;

/**
 * Class vehicle represents a vehicle (car or bike) which uses the parking.
 *
 * A vehicle is characterised by two elements:
 * -A string iDVehicle which is the registration number of the car. It cannot be changed.
 *
 * -A boolean recurrentUser which indicate if the vehicle is a recurrent user of the parking.
 * When the vehicle is created, it's automatically set to false.
 * It can be changed to true when the vehicle uses the parking several times.
 */
public class Vehicle {
    /**
     * The id of the vehicle which is its registration number. It cannot be changed.
     */
    private String iDVehicle;
    /**
     * The boolean indicates if the vehicle is a recurrent user of the parking or not.
     * It's set to false when a new vehicle is created.
     */
    private boolean recurrentUser;

    /**
     * Return the ID of a vehicle.
     * @return iDVehicle which is a String representing the vehicle's registration number.
     */
    public String getIDVehicle(){
        return iDVehicle;
    }

    /**
     * A constructor of the class Vehicle.
     * @param ID
     * which is a string representing the vehicle's registration number.
     */
    public void setIDVehicle(String ID){
       this.iDVehicle = ID;
    }

    /**
     *
     * @return recurrentUser
     * which is a boolean indicating if the vehicle is a recurrent user of the parking or not.
     */
    public boolean getRecurrentUser() {
        return recurrentUser;
    }

    /**
     * A constructor of the class Vehicle.
     * @param recurrent
     * which is a boolean indicating if the vehicle is a recurrent parking user or not.
     */
    public void setRecurrentUser(boolean recurrent){
        this.recurrentUser = recurrent;
    }

}
