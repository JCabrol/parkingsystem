package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.VehicleDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.model.Vehicle;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static VehicleDAO vehicleDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static InputReaderUtil inputReaderUtil2;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        vehicleDAO = new VehicleDAO();
        vehicleDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
     }

    @AfterAll
    private static void tearDown(){
    }

    @Test
    public void testParkingACar(){
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, vehicleDAO);
        Calendar dateTest = Calendar.getInstance();
        parkingService.processIncomingVehicle();
        long allDateTime = dateTest.getTimeInMillis();
        int millis = dateTest.get(Calendar.MILLISECOND);
        long dateTestIncoming = allDateTime - millis;
        long dateTestIncoming2 = dateTestIncoming + 1000;
        //WHEN
        Ticket myTicket = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertThat(myTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(myTicket.getInTime().getTime()).isIn(dateTestIncoming,dateTestIncoming2);
        assertThat(myTicket.getOutTime()).isNull();
        assertThat(myTicket.getPrice()).isEqualTo(0);
        assertThat(myTicket.getParkingSpot().isAvailable()).isFalse();
    }

    @Test
    public void testParkingLotExit(){
        //GIVEN
        when(inputReaderUtil2.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil2.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParkingService parkingService = new ParkingService(inputReaderUtil2, parkingSpotDAO, ticketDAO, vehicleDAO);
        Calendar dateTest = Calendar.getInstance();
        parkingService.processIncomingVehicle();
        long allDateTime = dateTest.getTimeInMillis();
        int millis = dateTest.get(Calendar.MILLISECOND);
        long dateTestIncoming = allDateTime - millis;
        long dateTestIncoming2 = dateTestIncoming + 2000;
        long dateTestExiting = dateTestIncoming + 3000;
        long dateTestExiting2 = dateTestExiting + 2000;
         try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        parkingService.processExitingVehicle();
        FareCalculatorService myFareCalculatorService = new FareCalculatorService();
        //WHEN
        Ticket myTicket = ticketDAO.getTicket("GHIJKL");
        //THEN
        assertThat((myTicket.getInTime().getTime())).isBetween(dateTestIncoming,dateTestIncoming2);
        assertThat((myTicket.getOutTime().getTime())).isBetween(dateTestExiting,dateTestExiting2);
        assertThat(myTicket.getPrice()).isEqualTo(myFareCalculatorService.calculateFare(myTicket));
        assertThat(myTicket.getParkingSpot().isAvailable()).isTrue();
    }
    @Test
    public void GivenTheSameUserEnterSeveralTimes_WhenExitParkingLot_ThenDataBaseShouldBeCorrectlyCompleted(){
        //GIVEN
        when(inputReaderUtil2.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil2.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParkingService parkingService = new ParkingService(inputReaderUtil2, parkingSpotDAO, ticketDAO, vehicleDAO);
        Calendar dateTest = Calendar.getInstance();
        parkingService.processIncomingVehicle();
        long allDateTime = dateTest.getTimeInMillis();
        int millis = dateTest.get(Calendar.MILLISECOND);
        long dateTestIncoming = allDateTime - millis;
        long dateTestIncoming2 = dateTestIncoming + 2000;
        long dateTestExiting = dateTestIncoming + 3000;
        long dateTestExiting2 = dateTestExiting + 2000;
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        parkingService.processExitingVehicle();
        FareCalculatorService myFareCalculatorService = new FareCalculatorService();
        Ticket myTicket = ticketDAO.getTicket("GHIJKL");

        Calendar dateTest2 = Calendar.getInstance();
        parkingService.processIncomingVehicle();
        long allDateTime2 = dateTest2.getTimeInMillis();
        int millis2 = dateTest2.get(Calendar.MILLISECOND);
        long dateTestIncoming3 = allDateTime2 - millis2;
        long dateTestIncoming4 = dateTestIncoming2 + 2000;
        long dateTestExiting3 = dateTestIncoming3 + 3000;
        long dateTestExiting4 = dateTestExiting3 + 2000;
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        parkingService.processExitingVehicle();
        //WHEN
        Ticket myTicket2 = ticketDAO.getTicket("GHIJKL");
        //THEN
        assertThat((myTicket.getInTime().getTime())).isBetween(dateTestIncoming,dateTestIncoming2);
        assertThat((myTicket.getOutTime().getTime())).isBetween(dateTestExiting,dateTestExiting2);
        assertThat(myTicket.getPrice()).isEqualTo(myFareCalculatorService.calculateFare(myTicket));
        assertThat(myTicket.getParkingSpot().isAvailable()).isTrue();
        assertThat((myTicket2.getInTime().getTime())).isBetween(dateTestIncoming3,dateTestIncoming4);
        assertThat((myTicket2.getOutTime().getTime())).isBetween(dateTestExiting3,dateTestExiting4);
        assertThat(myTicket2.getPrice()).isEqualTo(myFareCalculatorService.calculateFare(myTicket2));
        assertThat(myTicket2.getParkingSpot().isAvailable()).isTrue();
    }

    @Test
    public void GivenAUserPayingParkingFees_WhenEntersParkingSecondTime_ThenHeShouldBeARecurrentUser(){
        //GIVEN
        when(inputReaderUtil2.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil2.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParkingService parkingService = new ParkingService(inputReaderUtil2, parkingSpotDAO, ticketDAO, vehicleDAO);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,-91);
        Date dateTestEnter = cal.getTime();
        cal.add(Calendar.MINUTE,+31);//the user "stays" 31 minutes
        Date dateTestExit =cal.getTime();
        parkingService.processIncomingVehicle();//the vehicle "enters in the parking"
        Ticket myTicket = ticketDAO.getTicket("GHIJKL");//get informations from data base.
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("truncate table ticket").execute();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        //clear ticket table in data base to not have problems with redundant tickets.
        myTicket.setInTime(dateTestEnter);//give the entry date an hour
        ticketDAO.saveTicket(myTicket);//save informations in data base.
        parkingService.processExitingVehicle();// the vehicle "enters" in the parking.
        Ticket myTicket2 = ticketDAO.getTicket("GHIJKL");//get informations from data base
        try{
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("truncate table ticket").execute();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }//clear ticket table in data base to not have problems with redundant tickets.
        myTicket2.setOutTime(dateTestExit);//enter the exit time
        ticketDAO.saveTicket(myTicket);
        ticketDAO.updateTicket(myTicket2);//save informations in data base.
        parkingService.processIncomingVehicle();//the vehicle "enters" a second time.
        //WHEN
        Ticket myTicket3 = ticketDAO.getTicket("GHIJKL");
        Vehicle myVehicle = vehicleDAO.getVehicle("GHIJKL");//get informations from data base
        //THEN
        assertThat((myTicket2.getReductionRate())).isEqualTo(1);//first time there should not be reduction.
        assertThat((myTicket3.getReductionRate())).isEqualTo(0.95);//second time there should be 5% reduction.
        assertThat((myVehicle.getRecurrentUser())).isTrue();//the user should have become recurrent user.
    }
    @Test
    public void GivenAUserHavingParkingFree_WhenEntersParkingSecondTime_ThenHeShouldNotBeARecurrentUser(){
        //GIVEN
        when(inputReaderUtil2.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil2.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParkingService parkingService = new ParkingService(inputReaderUtil2, parkingSpotDAO, ticketDAO, vehicleDAO);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,-20);
        Date dateTestEnter = cal.getTime();
        cal.add(Calendar.MINUTE,+10);//the user "stays" 10 minutes
        Date dateTestExit =cal.getTime();
        parkingService.processIncomingVehicle();//the vehicle "enters in the parking"
        Ticket myTicket = ticketDAO.getTicket("GHIJKL");//get informations from data base.
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("truncate table ticket").execute();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        //clear ticket table in data base to not have problems with redundant tickets.
        myTicket.setInTime(dateTestEnter);//give the entry date an hour
        ticketDAO.saveTicket(myTicket);//save informations in data base.
        parkingService.processExitingVehicle();// the vehicle "enters" in the parking.
        Ticket myTicket2 = ticketDAO.getTicket("GHIJKL");//get informations from data base
        try{
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("truncate table ticket").execute();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }//clear ticket table in data base to not have problems with redundant tickets.
        myTicket2.setOutTime(dateTestExit);//enter the exit time
        ticketDAO.saveTicket(myTicket);
        ticketDAO.updateTicket(myTicket2);//save informations in data base.
        parkingService.processIncomingVehicle();//the vehicle "enters" a second time.
        //WHEN
        Ticket myTicket3 = ticketDAO.getTicket("GHIJKL");
        Vehicle myVehicle = vehicleDAO.getVehicle("GHIJKL");//get informations from data base
        //THEN
        assertThat((myTicket2.getReductionRate())).isEqualTo(1);//there should not be reduction.
        assertThat((myTicket3.getReductionRate())).isEqualTo(1);//there should not be reduction.
        assertThat((myVehicle.getRecurrentUser())).isFalse();//the user shouldn't be recurrent user.
    }
   }
