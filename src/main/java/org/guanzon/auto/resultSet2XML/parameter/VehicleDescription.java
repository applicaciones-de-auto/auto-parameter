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
public class VehicleDescription {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Description.xml");
        
        String lsSQL =    "   SELECT "                                                        
                        + "   a.sVhclIDxx " //1                                               
                        + " , a.sDescript " //2                                               
                        + " , a.sMakeIDxx " //3                                               
                        + " , a.sModelIDx " //4                                               
                        + " , a.sColorIDx " //5                                               
                        + " , a.sTypeIDxx " //6                                               
                        + " , a.sTransMsn " //7                                               
                        + " , a.nYearModl " //8                                               
                        + " , a.cVhclSize " //9                                               
                        + " , a.cRecdStat " //10                                              
                        + " , a.sEntryByx " //11                                              
                        + " , a.dEntryDte " //12                                              
                        + " , a.sModified " //13                                              
                        + " , a.dModified " //14                                              
                        + " , c.sMakeDesc " //15                                              
                        + " , b.sModelDsc " //16                                              
                        + " , d.sColorDsc " //17                                              
                        + " , e.sTypeDesc " //18                                              
                        + " FROM vehicle_master a "                                           
                        + " LEFT JOIN vehicle_model b ON a.sModelIDx = b.sModelIDx "          
                        + " LEFT JOIN vehicle_make c ON a.sMakeIDxx = c.sMakeIDxx  "          
                        + " LEFT JOIN vehicle_color d ON a.sColorIDx = d.sColorIDx "          
                        + " LEFT JOIN vehicle_type e ON a.sTypeIDxx = e.sTypeIDxx  "               
                        + " WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
