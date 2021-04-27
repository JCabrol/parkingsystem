package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.parkit.parkingsystem.constants.Fare.*;
import static com.parkit.parkingsystem.constants.ParkingType.BIKE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket2;

    @Mock
    Ticket ticket;

    @Mock
    ParkingSpot parkingSpot;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket2 = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals(ticket2.getPrice(), CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals(ticket2.getPrice(), BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnknownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket2));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket2));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        fareCalculatorService.calculateFare(ticket2);
        ticket2.setReductionRate(1);
        assertEquals((0.75 * BIKE_RATE_PER_HOUR), ticket2.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals((new BigDecimal(0.75 * CAR_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_UP).doubleValue()) , ticket2.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setReductionRate(1);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals( (24 * CAR_RATE_PER_HOUR) , ticket2.getPrice());
    }

    @Test
    public void givenARecurrentUserWithACar_whenCalculateFare_thenDiscount(){
        //GIVEN
        Date dateInExample= new Date();
        dateInExample.setTime( System.currentTimeMillis() - (  150 * 60 * 1000) );
        Date dateOutExample= new Date();
        when(ticket.getInTime()).thenReturn(dateInExample);
        when(ticket.getOutTime()).thenReturn(dateOutExample);
        when(ticket.getParkingSpot()).thenReturn(parkingSpot);
        when(parkingSpot.getParkingType()).thenReturn(ParkingType.CAR);
        when(ticket.getReductionRate()).thenReturn(REDUCTION_RATE_FOR_RECURRENT_USER);
        //WHEN
        double result = fareCalculatorService.calculateFare(ticket);
        //THEN
        assertThat(result).isEqualTo((new BigDecimal(2.5*CAR_RATE_PER_HOUR*REDUCTION_RATE_FOR_RECURRENT_USER).setScale(2, RoundingMode.HALF_UP)).doubleValue());
    }


    @Test
    public void givenARecurrentUserWithABike_whenCalculateFare_thenThePriceShouldBe5PerCentDiscount(){
         //GIVEN
        Date dateInExample= new Date();
        dateInExample.setTime( System.currentTimeMillis() - (  75 * 60 * 1000) );
        Date dateOutExample= new Date();
        when(ticket.getInTime()).thenReturn(dateInExample);
        when(ticket.getOutTime()).thenReturn(dateOutExample);
        when(ticket.getParkingSpot()).thenReturn(parkingSpot);
        when(parkingSpot.getParkingType()).thenReturn(BIKE);
        when(ticket.getReductionRate()).thenReturn(REDUCTION_RATE_FOR_RECURRENT_USER);
        //WHEN
        double result = fareCalculatorService.calculateFare(ticket);
        //THEN
        assertThat(result).isEqualTo((new BigDecimal(1.25*BIKE_RATE_PER_HOUR*REDUCTION_RATE_FOR_RECURRENT_USER).setScale(2, RoundingMode.HALF_UP)).doubleValue());
    }

    @Test
    public void givenAUserStayingLessThan30Minutes_whenCalculateFare_thenThePriceShouldBe0(){
        //GIVEN
        Date dateInExample= new Date();
        dateInExample.setTime( System.currentTimeMillis() - (  15 * 60 * 1000) );
        Date dateOutExample= new Date();
        when(ticket.getInTime()).thenReturn(dateInExample);
        when(ticket.getOutTime()).thenReturn(dateOutExample);
        //WHEN
        double result = fareCalculatorService.calculateFare(ticket);
        //THEN
        assertThat(result).isEqualTo(0);
    }


}
