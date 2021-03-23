package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    public double calculateFare(Ticket ticket){
        double price = 0;
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        // Calculate time the vehicle stays in the parking. The result is in milliseconds.
        long inMilliseconds = ticket.getInTime().getTime();
        long outMilliseconds = ticket.getOutTime().getTime();
        long durationInMilliseconds = outMilliseconds - inMilliseconds;
        //Convert the duration in hours. The result is not rounded.
        double duration = (double)durationInMilliseconds/3600000;



        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                //Calculate price if the vehicle is a car.
                double priceNotRounded = duration * Fare.CAR_RATE_PER_HOUR;
                BigDecimal bd = new BigDecimal(priceNotRounded).setScale(2, RoundingMode.HALF_UP);
                price = bd.doubleValue();
                ticket.setPrice(price);
                break;
            }
            case BIKE: {
                //Calculate price if the vehicle is a bike.
                double priceNotRounded = duration * Fare.BIKE_RATE_PER_HOUR;
                BigDecimal bd = new BigDecimal(priceNotRounded).setScale(2, RoundingMode.HALF_UP);
                price = bd.doubleValue();
                ticket.setPrice(price);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    return price;
    }
}