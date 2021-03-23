package com.parkit.parkingsystem.model;

public class Vehicle {
    private String iDVehicle;
    private boolean recurrentUser;

    public String getIDVehicle(){
        return iDVehicle;
    }

    public void setIDVehicle(String ID){
       this.iDVehicle = ID;
    }

    public boolean getRecurrentUser() {
        return recurrentUser;
    }

    public void setRecurrentUser(boolean recurrent){
        this.recurrentUser = recurrent;
    }
}
