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
public class InsuranceBranches {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Insurance_Branches.xml");
        
        String lsSQL =    " SELECT "                                                    
                        + "    a.sBrInsIDx "                                            
                        + "  , a.sBrInsNme "                                            
                        + "  , a.sBrInsCde "                                            
                        + "  , a.sCompnyTp "                                            
                        + "  , a.sInsurIDx "                                            
                        + "  , a.sContactP "                                            
                        + "  , a.sAddressx "                                            
                        + "  , a.sTownIDxx "                                            
                        + "  , a.sZippCode "                                            
                        + "  , a.sTelNoxxx "                                            
                        + "  , a.sFaxNoxxx "                                            
                        + "  , a.cRecdStat "                                            
                        + "  , a.sModified "                                            
                        + "  , a.dModified "                                            
                        + "  , b.sInsurNme "                                            
                        + "  , c.sTownName "                                            
                        + "  , d.sProvName "                                           
                        + "  , d.sProvIDxx "                                           
                        + "  , CONCAT(b.sInsurNme, ' ',  a.sBrInsNme) AS sInsurnce "                                                 
                        + "  , CONCAT(a.sAddressx, ' ', c.sTownName, ' ',  d.sProvName) AS xAddressx "                                           
                        + " FROM insurance_company_branches a "                         
                        + " LEFT JOIN insurance_company b ON b.sInsurIDx = a.sInsurIDx "
                        + " LEFT JOIN towncity c ON c.sTownIDxx = a.sTownIDxx "         
                        + " LEFT JOIN province d ON d.sProvIDxx = c.sProvIDxx " 
                        + " WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "insurance_company_branches", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
