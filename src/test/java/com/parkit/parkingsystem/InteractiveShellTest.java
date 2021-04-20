package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.VehicleDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.parkit.parkingsystem.constants.Fare.*;
import static com.parkit.parkingsystem.constants.ParkingType.BIKE;
import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {

    private static InteractiveShell interactiveShell;


    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingService parkingService;


    @Test
    public void loadInterfaceTest() {
        //GIVEN
        //when(inputReaderUtil.readSelection()).thenReturn(1).thenReturn(2).thenReturn(3);
       //doNothing().when(parkingService).processIncomingVehicle();
      //  doNothing().when(parkingService).processExitingVehicle();
     //   doThrow(new NullPointerException()).when(parkingService).processIncomingVehicle();
     //   doThrow(new NullPointerException()).when(parkingService).processExitingVehicle();
        when(inputReaderUtil.readSelection()).thenReturn(3);

        //WHEN
        InteractiveShell.loadInterface(inputReaderUtil);
        //THEN
   //    verify(parkingService,Mockito.times(1)).processIncomingVehicle();
  //      verify(parkingService,Mockito.times(1)).processExitingVehicle();
    }


}
