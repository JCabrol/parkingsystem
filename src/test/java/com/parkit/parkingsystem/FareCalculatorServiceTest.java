package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.model.Vehicle;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    Vehicle vehicle;

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
        fareCalculatorService.calculateFare(ticket2);
        assertEquals(ticket2.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals(ticket2.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket2));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket2));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket2.setInTime(inTime);
        ticket2.setOutTime(outTime);
        ticket2.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket2);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket2.getPrice() );
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
        fareCalculatorService.calculateFare(ticket2);
        assertEquals((new BigDecimal(0.75 * Fare.CAR_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_UP).doubleValue()) , ticket2.getPrice());
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
        fareCalculatorService.calculateFare(ticket2);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket2.getPrice());
    }

    @Test
    public void givenARecurrentUserWithACar_whenCalculateFare_thenThePriceIs5PerCentDiscount(){
        //GIVEN
        when(ticket.getInTime().getTime()).thenReturn(1616450410296L);
        when(ticket.getOutTime().getTime()).thenReturn(1616482453046L);
        when(ticket.getParkingSpot().getParkingType()).thenReturn(ParkingType.CAR);
        when(vehicle.getRecurrentUser()).thenReturn(true);
        //WHEN
        double result = fareCalculatorService.calculateFare(ticket);
        //THEN
        assertThat(result).isEqualTo(12.68);
    }

    @Test
    public void givenARecurrentUserWithABike_whenCalculateFare_thenThePriceIs5PerCentDiscount(){
        //GIVEN
        when(ticket.getInTime().getTime()).thenReturn(1616450410296L);
        when(ticket.getOutTime().getTime()).thenReturn(1616482453046L);
        when(ticket.getParkingSpot().getParkingType()).thenReturn(ParkingType.BIKE);
        when(vehicle.getRecurrentUser()).thenReturn(true);
        //WHEN
        double result = fareCalculatorService.calculateFare(ticket);
        //THEN
        assertThat(result).isEqualTo(8.46);
    }
}
