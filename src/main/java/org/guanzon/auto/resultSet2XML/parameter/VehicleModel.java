/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.resultSet2XML.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;

/**
 *
 * @author Arsiela
 */
public class VehicleModel {
    
    public static void main (String [] args){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider instance = new GRider("gRider");

        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Model.xml");
        
        String lsSQL =    "  SELECT "                                               
                        + "  a.sModelIDx "                                          
                        + ", a.sModelDsc "                                          
                        + ", a.sMakeIDxx "                                          
                        + ", a.sUnitType "                                          
                        + ", a.sBodyType "                                          
//                        + ", a.cVhclSize "                                          
                        + ", a.sModelCde "                                          
                        + ", a.cRecdStat "                                          
                        + ", a.sEntryByx "                                          
                        + ", a.dEntryDte "                                          
                        + ", a.sModified "                                          
                        + ", a.dModified "                                          
                        + ", b.sMakeDesc "	                                        
                        + "FROM vehicle_model a "                                   
                        + "LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx "         
                        + "WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_model", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
