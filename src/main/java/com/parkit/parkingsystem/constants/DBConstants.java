package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";
    public static final String GET_PARKING_SPOT = "select AVAILABLE,TYPE from parking where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, REDUCTION_RATE) values(?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE, t.REDUCTION_RATE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME desc  limit 1";

    /**
     * This query result is only one boolean indicating if a vehicle is a recurrent user or not.
     */
    public static final String GET_VEHICLE_RECURRENT = "select RECURRENT_USER from vehicle where ID_VEHICLE = ?";
    /**
     * This query result is the creation of a new vehicle in the dataBase.
     * The vehicle's ID_VEHICLE is the String given in parameter.
     * The vehicle's RECURRENT_USER is a boolean set to false.
     */
    public static final String CREATE_VEHICLE = "insert into vehicle(ID_VEHICLE) value(?)";
    /**
     * This query result is the change of the boolean RECURRENT_USER to the value given in parameter(2)
     * on the vehicle defined by the String ID_VEHICLE given in parameter(1).
     */
    public static final String UPDATE_VEHICLE = "update vehicle set RECURRENT_USER=? where ID_VEHICLE=?";
    /**
     * This query result is only one line representing the vehicle registered in the dataBase with the ID_VEHICLE given in parameter
     * or it's empty if the vehicle doesn't exist.
     */
    public static final String EXIST_VEHICLE= "select * from vehicle where ID_VEHICLE=?";
    /**
     * This query result is the information about the vehicle having the ID_VEHICLE given in parameter.
     */
    public static final String GET_VEHICLE = "select RECURRENT_USER from vehicle where ID_VEHICLE = ?";
}
