/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.parameter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.parameter.Model_Vehicle_Make;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Make_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Make poModel;
    JSONObject poJSON;
    

    public Vehicle_Make_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Make(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public void setRecordStatus(String fsValue) {
        psRecdStat = fsValue;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poModel.setValue(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poModel.setValue(fsCol, foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        return poModel.getValue(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return poModel.getValue(fsCol);
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poModel = new Model_Vehicle_Make(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setMakeID(MiscUtil.getNextCode(poModel.getTable(), "sMakeIDxx", false, loConn, psBranchCd+"MK"));
            poModel.newRecord();
            
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
                pnEditMode = EditMode.ADDNEW;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
        
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Vehicle_Make(poGRider);
        poJSON = poModel.openRecord(fsValue);
        
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        } 
        return poJSON;
    }

    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();
        if (pnEditMode != EditMode.READY && pnEditMode != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode.");
            return poJSON;
        }
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        
        poJSON = validateEntry();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON = poModel.saveRecord();

        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWtParent) {poGRider.commitTrans();}
        } else {
            if (!pbWtParent) {poGRider.rollbackTrans();}
        }
        
        return poJSON;
    }

    @Override
    public JSONObject deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deactivateRecord(String fsValue) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            poJSON = poModel.setActive(false);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
            poJSON = validateEntry();
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModel.saveRecord();
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "success");
                poJSON.put("message", "Deactivation success.");
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject activateRecord(String fsValue) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            poJSON = poModel.setActive(true);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
            poJSON = validateEntry();
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
            poJSON = poModel.saveRecord();
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "success");
                poJSON.put("message", "Activation success.");
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByActive) {
//        String lsCondition = "";
//
//        if (psRecdStat.length() > 1) {
//            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
//                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
//            }
//
//            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
//        } else {
//            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
//        }

        String lsSQL =  "   SELECT "              
                        + "   sMakeIDxx " //1    
                        + ",  sMakeDesc " //2    
                        + ",  sMakeCode " //3     
                        + ",  cRecdStat " //4    
                        + "FROM vehicle_make "  ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " sMakeDesc LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " sMakeDesc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH MAKE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Name",
                "sMakeIDxx»sMakeDesc",
                "sMakeIDxx»sMakeDesc",
                1);

        if (poJSON != null) {
            poJSON.put("result", "success");
            poJSON.put("message", "New selected record.");
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
        
        return poJSON;
    }

    @Override
    public Model_Vehicle_Make getModel() {
        return poModel;
    }
    
    private Connection setConnection(){
        Connection foConn;
        
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        
        return foConn;
    }
    
    private JSONObject validateEntry(){
        JSONObject jObj = new JSONObject();
        try {
            if(poModel.getMakeID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make ID cannot be Empty.");
                return jObj;
            }

            if(poModel.getMakeDesc().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make Description cannot be Empty.");
                return jObj;
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sMakeDesc,' ','') = " + SQLUtil.toSQL(poModel.getMakeDesc().replace(" ",""))) +
                                                    " AND sMakeIDxx <> " + SQLUtil.toSQL(poModel.getMakeID()) ;
            System.out.println("EXISTING VEHICLE MAKE CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sMakeIDxx");
                        lsDesc = loRS.getString("sMakeDesc");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Make Description Record.\n\nMake ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsSQL =   "  SELECT "                                                
                    + "  a.sMakeIDxx "                                           
                    + ", b.sMakeDesc "                                           
                    + ", b.cRecdStat "                                           
                    + ", a.sVhclIDxx "                                           
                    + "FROM vehicle_master a "                                     
                    + "LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx ";
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID())) ;
                System.out.println("EXISTING USAGE OF VEHICLE MAKE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Make Usage.\n\nVehicle ID: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            if(pnEditMode == EditMode.UPDATE){
                lsID = "";
                lsSQL =   "  SELECT "                                                
                    + "  a.sMakeIDxx "                                           
                    + ", b.sMakeDesc "                                           
                    + ", a.cRecdStat "                                                
                    + ",  a.sModelIDx "                                           
                    + "FROM vehicle_model a "                                     
                    + "LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx ";
                
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                                        + " AND REPLACE(b.sMakeDesc, ' ','') <> " + SQLUtil.toSQL(poModel.getMakeDesc().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("VEHICLE MODEL: EXISTING USAGE OF VEHICLE MAKE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sModelIDx");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Make Usage.\n\nModel ID: " + lsID + "\nChanging of make description aborted.");
                        return jObj;
                }
                
                lsID = "";
                lsSQL =   "  SELECT "                                                
                    + "  a.sMakeIDxx "                                           
                    + ", b.sMakeDesc "                                             
                    + ", a.cRecdStat "                                           
                    + ", a.sVhclIDxx "                                           
                    + "FROM vehicle_master a "                                     
                    + "LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx ";
                
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                                        + " AND REPLACE(b.sMakeDesc, ' ','') <> " + SQLUtil.toSQL(poModel.getMakeDesc().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("VEHICLE MASTER: EXISTING USAGE OF VEHICLE MAKE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Make Usage.\n\nVehicle ID: " + lsID + "\nChanging of make description aborted.");
                        return jObj;
                }
                
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Make_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}
