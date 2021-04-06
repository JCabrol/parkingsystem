package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.model.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class permit to execute and get results from queries in database prod (table vehicle).
 * The queries are stocked in the DBConstant class.
 * @see DBConstants
 */
public class VehicleDAO {

    private static final Logger logger = LogManager.getLogger("VehicleDAO");
    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * The function getVehicle permits to get a Vehicle entering its registration number.
     * Its uses the query GET_VEHICLE in DBConstants
     * @see DBConstants#GET_VEHICLE
     * @param iDVehicle which is a String representing the vehicle's registration number.
     * @return vehicle which is the vehicle having the registration number given in parameter.
     * @see Vehicle
     */
    public Vehicle getVehicle(String iDVehicle){
        Connection con = null;
        Vehicle vehicle = new Vehicle();

        try {
           //prepare and execute the query.
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_VEHICLE);
            ps.setString(1,iDVehicle);
            ResultSet rs = ps.executeQuery();
           //verify if there is a result and place the cursor to get it.
            if(rs.next()){
               //constructs a vehicle with the information given by the query.
                vehicle.setIDVehicle(iDVehicle);
                vehicle.setRecurrentUser(rs.getBoolean(1));
            }
        }catch (Exception ex){
            //if there is an exception an error message is logged.
            logger.error("Error getting vehicle:",ex);
       //Close connexion with dataBase.
        }finally{
            dataBaseConfig.closeConnection(con);
        }
        return vehicle;
    }

    /**
     * The function isUserRecurrent permits to know if a vehicle is a parking's recurrent user or not.
     * Its uses the query GET_VEHICLE_RECURRENT in DBConstants class.
     * @see DBConstants#GET_VEHICLE_RECURRENT
     * @param iDVehicle which is a String representing the vehicle's registration number.
     * @return isRecurrent which is a boolean indicating if the vehicle is a parking's recurrent user or not.
     */
    public boolean isUserRecurrent(String iDVehicle){
        Connection con = null;
        boolean isRecurrent = false;
        try {
            //prepare and execute the query.
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_VEHICLE_RECURRENT);
            ps.setString(1,iDVehicle);
            ResultSet rs = ps.executeQuery();
            // put the first value (there is only one value) returned by the query in the boolean isRecurrent.
            isRecurrent = rs.first();
        }catch (Exception ex){
            //if there is an exception an error message is logged.
            logger.error("Error getting boolean isRecurrent on a vehicle:",ex);
        }finally{
            //Close connexion with dataBase.
            dataBaseConfig.closeConnection(con);
        }
        return isRecurrent;
    }

    /**
     * The function createVehicle permits to create a new vehicle having its registration number and to put it in the database.
     * This function uses the query CREATE_VEHICLE in DBConstants.
     * @see DBConstants#CREATE_VEHICLE
     * @param iDVehicle which is a String representing the vehicle's registration number.
     */
    public void createVehicle(String iDVehicle){
        Connection con = null;
        try {
            //prepare and execute the query.
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.CREATE_VEHICLE);
            ps.setString(1,iDVehicle);
            ps.execute();
        }catch (Exception ex){
            //if there is an exception an error message is logged.
            logger.error("Error creating vehicle:",ex);
        }finally {
            //Close connexion with dataBase.
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * The function updateVehicle permits to modify a vehicle in the dataBase having its registration number.
     * It's used to change the boolean isRecurrent indicating if the vehicle is a parking's recurrent user or not.
     * This function uses the query UPDATE_VEHICLE in DBConstants.
     * @see DBConstants#UPDATE_VEHICLE
     * @param isRecurrent is a boolean which indicates if the vehicle is a parking's recurrent user or not.
     * @param iDVehicle is a String which represents the vehicle's registration number.
     */
    public void updateVehicle(boolean isRecurrent, String iDVehicle){
        Connection con = null;
        try {
            //prepare and execute the query.
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_VEHICLE);
            ps.setBoolean(1,isRecurrent);
            ps.setString(2,iDVehicle);
            ps.execute();
        }catch (Exception ex){
            //if there is an exception an error message is logged.
            logger.error("Error modifying vehicle:",ex);
        }finally {
            //Close connexion with dataBase.
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * The function existVehicle permits to verify if a vehicle is already registered in the dataBase.
     * This function uses the query EXIST_VEHICLE in DBConstants.
     * @see DBConstants#EXIST_VEHICLE
     * @param iDVehicle which is a String representing the registration number of a vehicle;
     * @return exists which is a boolean indicating if the vehicle is already registered in the dataBase or not.
     */
    public boolean existVehicle(String iDVehicle){
        Connection con = null;
        boolean exists = false;
        try {
            //prepare and execute the query.
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.EXIST_VEHICLE);
            ps.setString(1,iDVehicle);
            ResultSet rs = ps.executeQuery();
            // if there is a result in the query, exists is set to true, if there is not, exists stays false.
            exists = rs.next();
        }catch (Exception ex){
            //if there is an exception an error message is logged.
            logger.error("Error verifying a vehicle exists:",ex);
        }finally {
            //Close connexion with dataBase.
            dataBaseConfig.closeConnection(con);
        }
        return exists;
    }

}
