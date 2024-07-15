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
public class VehicleModelFramePattern {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Model_Frame_Pattern.xml");
        
        String lsSQL =    "   SELECT "                                               
                        + "   a.sModelIDx "                                          
                        + " , a.nEntryNox "                                          
                        + " , a.sFrmePtrn "                                          
                        + " , a.nFrmeLenx "                                          
                        + " , a.sEntryByx "                                          
                        + " , a.dEntryDte "                                          
                        + " , a.sModified "                                          
                        + " , a.dModified "                                          
                        + " , b.sModelDsc "                                          
                        + " , c.sMakeIDxx "                                          
                        + " , c.sMakeDesc "                                          
                        + " FROM vehicle_model_frame_pattern a "                     
                        + " LEFT JOIN vehicle_model b ON b.sModelIDx = a.sModelIDx " 
                        + " LEFT JOIN vehicle_make c ON c.sMakeIDxx = b.sMakeIDxx  "      
                        + " WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_model_frame_pattern", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
