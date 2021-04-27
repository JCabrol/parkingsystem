package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.VehicleDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static VehicleDAO vehicleDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        vehicleDAO = new VehicleDAO();
        vehicleDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();

    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, vehicleDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        //WHEN
        Ticket myTicket = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertThat(myTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(myTicket.getInTime()).isNotNull();
        assertThat(myTicket.getOutTime()).isNull();
        assertThat(myTicket.getPrice()).isEqualTo(0);
        assertThat(myTicket.getParkingSpot().isAvailable()).isFalse();

    }

    @Test
    public void testParkingLotExit(){
        //GIVEN
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, vehicleDAO);
        parkingService.processExitingVehicle();
        FareCalculatorService myFareCalculatorService = new FareCalculatorService();
        //TODO: check that the fare generated and out time are populated correctly in the database
        //WHEN
        Ticket myTicket = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertThat(myTicket.getOutTime()).isNotNull();
        assertThat(myTicket.getPrice()).isEqualTo(myFareCalculatorService.calculateFare(myTicket));
        assertThat(myTicket.getParkingSpot().isAvailable()).isTrue();
    }

}
