package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.VehicleDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.model.Vehicle;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

import static com.parkit.parkingsystem.constants.Fare.REDUCTION_RATE_FOR_RECURRENT_USER;

@SuppressWarnings({"unused", "UnusedAssignment"})
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private VehicleDAO vehicleDAO;

    public void setInputReaderUtil(InputReaderUtil inputReaderUtil){
        this.inputReaderUtil=inputReaderUtil;
    }
    public void setParkingSpotDAO(ParkingSpotDAO parkingSpotDAO){
        this.parkingSpotDAO=parkingSpotDAO;
    }
    public void setTicketDAO(TicketDAO ticketDAO){
        this.ticketDAO=ticketDAO;
    }
    public void setVehicleDAO(VehicleDAO vehicleDAO){
        this.vehicleDAO=vehicleDAO;
    }
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO, VehicleDAO vehicleDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
        this.vehicleDAO = vehicleDAO;
    }

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = getVehicleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                Vehicle vehicle = new Vehicle();
                vehicle.setIDVehicle(vehicleRegNumber);
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticket.setVehicle(vehicle);
                ticket.setReductionRate(1);
                if ((vehicleDAO.existVehicle(vehicleRegNumber))&&(vehicleDAO.isUserRecurrent(vehicleRegNumber))){
                    System.out.println("\n"+"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount."+"\n");
                    ticket.setReductionRate(REDUCTION_RATE_FOR_RECURRENT_USER);
                }
                else if(!(vehicleDAO.existVehicle(vehicleRegNumber))){
                    vehicleDAO.createVehicle(vehicleRegNumber);
                }

                ticketDAO.saveTicket(ticket);

                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);
            }
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehicleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehicleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            logger.error("Error parsing user input for type of vehicle", ie);
        }catch(Exception e){
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    public ParkingType getVehicleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        try{
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Vehicle vehicle = vehicleDAO.getVehicle(vehicleRegNumber);
            ticket.setVehicle(vehicle);
            Date outTime = new Date();
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if(ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
                //The user become recurrent if he pays.
                if(ticket.getPrice()!=0)
                    {vehicleDAO.updateVehicle(true,vehicleRegNumber);}
            }else{
                System.out.println("Unable to update ticket information. Error occurred");
            }
        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }
    }
}
