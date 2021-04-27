package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.VehicleDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    @Mock
    private static VehicleDAO vehicleDAO;
    @Mock
    private static Ticket ticket;
    @Mock
    private static ParkingSpot parkingSpot;



    @BeforeEach
    private void setUpPerTest() {
        try {

            //ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, vehicleDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Nested
    @Tag("processIncomingVehicleTest")
    @DisplayName("ProcessIncomingVehicleTest")
    class processIncomingVehicleTest {
        @Test
        @DisplayName("When a parking spot is returned, the function saveTicket() is called.")
        public void givenAParkingSpot_WhenProcessIncomingVehicle_ThenTheFunctionSaveTicketIsCalled() {
            //GIVEN
           try {
               when(inputReaderUtil.readSelection()).thenReturn(1);
               when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
               when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //WHEN
            parkingService.processIncomingVehicle();
            //THEN
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        }
    }

    @Nested
    @Tag("processExitingVehicleTest")
    @DisplayName("ProcessExitingVehicleTest")
    class processExitingVehicleTest {
        @Test
        @DisplayName("When a vehicle registration number is given by user, the function getTicket() is called with right arguments")
        public void givenAVehicleRegNumber_WhenProcessExitingVehicle_ThenTheFunctionGetTicketIsCalled() {
            //GIVEN
            try {
                when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
                when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //WHEN
            parkingService.processExitingVehicle();
            //THEN
            verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        }

        @Test
        @DisplayName("When a vehicle registration number is given by user, the function getVehicle() is called with right arguments")
        public void givenAVehicleRegNumber_WhenProcessExitingVehicle_ThenTheFunctionGetVehicleIsCalled() {
            //GIVEN
            try {
                when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
                when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //WHEN
            parkingService.processExitingVehicle();
            //THEN
            verify(vehicleDAO, Mockito.times(1)).getVehicle("ABCDEF");
        }
    }

        @Nested
        @Tag("getVehicleTypeTest")
        @DisplayName("GetVehicleTypeTest")
        class getVehicleTypeTest {
            @Test
            @DisplayName("When user enters 1, the vehicle type returned should be CAR")
            public void givenAUSerEnter1_WhenGetVehicleType_ThenTheResultShouldBeCAR() {
                //GIVEN
                String result;
                when(inputReaderUtil.readSelection()).thenReturn(1);
                //WHEN
                result = valueOf(parkingService.getVehicleType());
                //THEN
                assertThat(result).isEqualTo("CAR");
            }

            @Test
            @DisplayName("When user enters 2, the vehicle type returned should be BIKE")
            public void givenAUSerEnter1_WhenGetVehicleType_ThenTheResultShouldBeBIKE() {
                //GIVEN
                String result;
                when(inputReaderUtil.readSelection()).thenReturn(2);
                //WHEN
                result = valueOf(parkingService.getVehicleType());
                //THEN
                assertThat(result).isEqualTo("BIKE");
            }

            @Test
            @DisplayName("When user enters something else than 1 or 2, an exception is thrown")
            public void givenAUSerEnterSomethingElse_WhenGetVehicleType_ThenAnExceptionIsThrown() {
                //GIVEN
                when(inputReaderUtil.readSelection()).thenReturn(0).thenThrow(new ArithmeticException());
                //WHEN
                assertThrows(IllegalArgumentException.class, () -> parkingService.getVehicleType());
            }
        }
    }


