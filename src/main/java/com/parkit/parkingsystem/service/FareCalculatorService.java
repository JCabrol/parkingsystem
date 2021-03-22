package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        // Calculate time the vehicle stays in the parking. The result is in milliseconds.
        long inMilliseconds = ticket.getInTime().getTime();
        long outMilliseconds = ticket.getOutTime().getTime();
        long durationInMilliseconds = outMilliseconds - inMilliseconds;
        //Convert the duration in hours. The result is not rounded.
        double durationWithTooMuchNumbers = (double)durationInMilliseconds/3600000;
        //Round the result to have only two numbers after the dot.
        BigDecimal bd = new BigDecimal(durationWithTooMuchNumbers).setScale(2, RoundingMode.HALF_UP);
        double duration = bd.doubleValue();


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                //Calculate price if the vehicle is a car.
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                //Calculate price if the vehicle is a bike.
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}