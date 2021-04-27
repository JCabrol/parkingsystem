package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class App {
    private static final Logger logger = LogManager.getLogger("App");
    public static void main(String[] args){

        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface(new InputReaderUtil());
    }
}
