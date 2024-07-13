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
import org.guanzon.auto.model.parameter.Model_Vehicle_Description;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Description_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Description poModel;
    JSONObject poJSON;

    public Vehicle_Description_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Description(foGRider);
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

            poModel = new Model_Vehicle_Description(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setVhclID(MiscUtil.getNextCode(poModel.getTable(), "sVhclIDxx", false, loConn, psBranchCd+"VM"));
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
        
        poModel = new Model_Vehicle_Description(poGRider);
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
        
        //set Concatinated Vehicle Description
        poModel.setDescript((poModel.getMakeDesc().trim() + " " + poModel.getModelDsc().trim() + " " + poModel.getTypeDesc().trim() + " " + poModel.getTransMsn().trim() + " " + 
                            poModel.getColorDsc().trim() + " " + poModel.getYearModl()).toUpperCase());

        if(poModel.getDescript().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Vehicle Description cannot be Empty.");
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
//
//        
//        String lsSQL = MiscUtil.addCondition(poModel.makeSelectSQL(), " sDescript LIKE "
//                + SQLUtil.toSQL(fsValue + "%") + " AND " + lsCondition);

        String lsSQL = poModel.getSQL();
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL, " sDescript LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println("SEARCH VEHICLE MASTER: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sVhclIDxx»sDescript",
                "sVhclIDxx»sDescript",
                1);

        if (poJSON != null) {
            poJSON.put("result", "success");
            poJSON.put("message", "New selected record.");
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
        return poJSON;
    }

    @Override
    public Model_Vehicle_Description getModel() {
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
            if(poModel.getVhclID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Vehicle ID cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getMakeID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getModelID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getColorID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Color cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getTypeID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getTransMsn().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Transmission cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getYearModl() == null || poModel.getYearModl() == 0){
                jObj.put("result", "error");
                jObj.put("message", "Year Model cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getVhclSize().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Vehicle Size cannot be Empty.");
                return jObj;
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, " a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) +
                                                " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) + 
                                                " AND a.sColorIDx = " + SQLUtil.toSQL(poModel.getColorID())+
                                                " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) + 
                                                " AND a.nYearModl = " + SQLUtil.toSQL(poModel.getYearModl())+
                                                " AND a.sTransMsn = " + SQLUtil.toSQL(poModel.getTransMsn()) +
                                                " AND a.sVhclIDxx <> " + SQLUtil.toSQL(poModel.getVhclID())); 
            System.out.println("EXISTING VEHICLE DESCRIPTION CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sVhclIDxx");
                        lsDesc = loRS.getString("sMakeDesc") + " " + loRS.getString("sModelDsc") + " " + loRS.getString("sTypeDesc") + 
                                 " " + loRS.getString("sTransMsn") + " " + loRS.getString("sColorDsc") + " " + loRS.getString("nYearModl");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Description Record.\n\nVehicle ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            //Do not allow changing of vehicle description when it is already linked thru vehicle_serial
            lsSQL =   "  SELECT  "                                                            
                    + "  a.sSerialID "                                                        
                    + ", a.sVhclIDxx "                                                        
                    + ", a.sCSNoxxxx "                                                        
                    + ", a.sEngineNo "                                                        
                    + ", a.sFrameNox "                                                        
                    + ", b.sPlateNox "                                                        
                    + ", c.sDescript "                                                        
                    + ", c.sMakeIDxx "                                                        
                    + ", c.sModelIDx "                                                        
                    + ", c.sColorIDx "                                                        
                    + ", c.sTypeIDxx "                                                        
                    + ", c.sTransMsn "                                                        
                    + ", c.nYearModl "                                                        
                    + ", c.cVhclSize "                                                        
                    + ", c.cRecdStat "                                                        
                    + "FROM vehicle_serial a  "                                               
                    + "LEFT JOIN vehicle_serial_registration b ON b.sSerialID = a.sSerialID " 
                    + "LEFT JOIN vehicle_master c ON c.sVhclIDxx = a.sVhclIDxx ";             

            lsSQL = MiscUtil.addCondition(lsSQL, " a.sVhclIDxx = " + SQLUtil.toSQL(poModel.getVhclID())  +
                                                " AND c.sMakeIDxx <> " + SQLUtil.toSQL(poModel.getMakeID())  +
                                                " AND c.sModelIDx <> " + SQLUtil.toSQL(poModel.getModelID()) + 
                                                " AND c.sColorIDx <> " + SQLUtil.toSQL(poModel.getColorID()) +
                                                " AND c.sTypeIDxx <> " + SQLUtil.toSQL(poModel.getTypeID())  + 
                                                " AND c.nYearModl <> " + SQLUtil.toSQL(poModel.getYearModl())+
                                                " AND c.sTransMsn <> " + SQLUtil.toSQL(poModel.getTransMsn()) ) ;
            System.out.println("CHANGE DESCRIPTION; EXISTING USAGE: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sSerialID");
                    }

                    MiscUtil.close(loRS);

                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Description Usage.\n\nVehicle Serial ID: " + lsID + "\nChanging of vehicle description aborted.");
                    return jObj;
            }
            
            //Deactivation Validation
            lsID = "";
            if(poModel.getRecdStat().equals("0")){
                lsSQL =   "  SELECT "                                                               
                        + "  sSerialID "                                                          
                        + ", sFrameNox "                                                          
                        + ", sEngineNo "                                                          
                        + ", sVhclIDxx "                                                          
                        + ", sClientID "                                                          
                        + ", sCSNoxxxx "                                                          
                        + " FROM vehicle_serial " ;

                lsSQL = MiscUtil.addCondition(lsSQL, " sVhclIDxx = " + SQLUtil.toSQL(poModel.getVhclID())) ;
                System.out.println("DEACTIVATE VEHICLE DESCRIPTION; EXISTING USAGE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sSerialID");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Description Usage.\n\nVehicle Serial ID: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Description_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
     public JSONObject searchModel(String fsValue) {
        poJSON = new JSONObject();
        
        if(poModel.getMakeID().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Make cannot be Empty.");
            return poJSON;
        }
         
        String lsSQL =    "  SELECT "                                               
                        + "  a.sModelIDx "                                          
                        + ", a.sModelDsc "                                          
                        + ", a.sMakeIDxx "                                          
                        + ", a.cRecdStat "                                         
                        + ", b.sMakeDesc "	                                        
                        + "FROM vehicle_model a "                                   
                        + "LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx ";
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sModelDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH MODEL: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sModelIDx»sModelDsc",
                "sModelIDx»sModelDsc",
                1);

        if (poJSON != null) {
            poJSON.put("result", "success");
            poJSON.put("message", "New selected record.");
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
        
        return poJSON;
    }
}
