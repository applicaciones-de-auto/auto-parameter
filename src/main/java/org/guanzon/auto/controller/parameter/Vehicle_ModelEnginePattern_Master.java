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
import org.guanzon.auto.model.parameter.Model_Vehicle_Model_Engine_Pattern;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_ModelEnginePattern_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Model_Engine_Pattern poModel;
    
    JSONObject poJSON;

    public Vehicle_ModelEnginePattern_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Model_Engine_Pattern(foGRider);
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

            poModel = new Model_Vehicle_Model_Engine_Pattern(poGRider);
            
            Connection loConn = null;
            loConn = setConnection();
            poModel.setEntryNo(Integer.valueOf(MiscUtil.getNextCode(poModel.getTable(), "nEntryNox", false, loConn, "")));
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject openRecord(String fsValue, Integer fnEntryNo) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Vehicle_Model_Engine_Pattern(poGRider);
        poJSON = poModel.openRecord(fsValue, fnEntryNo);
        
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
    public JSONObject deactivateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject activateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByCode) {
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), " a.sEngnPtrn LIKE "
                + SQLUtil.toSQL(fsValue + "%") );
        System.out.println("SEARCH ENGINE PATTERN: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Make»Model»Model Engine Pattern»Length",
                "sMakeDesc»sModelDsc»sEngnPtrn»nEngnLenx",
                "c.sMakeDesc»b.sModelDsc»a.sEngnPtrn»a.nEngnLenx",
                2);

        if (poJSON != null) {
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }

    @Override
    public Model_Vehicle_Model_Engine_Pattern getModel() {
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
            
            if(poModel.getModelID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getModelID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Model cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getEngnPtrn() == null){
                jObj.put("result", "error");
                jObj.put("message", "Engine Pattern cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getEngnPtrn().trim().isEmpty() || poModel.getEngnPtrn().replace(" ", "").length() < 3){
                    jObj.put("result", "error");
                    jObj.put("message", "Invalid Engine Pattern.");
                    return jObj;
                }
            }
            
            if(poModel.getEngnLen() == null){
                jObj.put("result", "error");
                jObj.put("message", "Engine Length cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getEngnLen() < 3){
                    jObj.put("result", "error");
                    jObj.put("message", "Invalid Engine Length.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sEngnPtrn,' ','') = " + SQLUtil.toSQL(poModel.getEngnPtrn().replace(" ",""))) +
                                                    " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) +
                                                    " AND a.nEntryNox <> " + SQLUtil.toSQL(poModel.getEntryNo()) ;
            System.out.println("EXISTING VEHICLE MODEL ENGINE PATTERN CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sModelDsc");
                        lsDesc = loRS.getString("sEngnPtrn");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Model Engine Pattern Record.\n\nModel: " + lsID + "\nEngine Pattern: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
//            lsID = "";
//            lsSQL =   "  SELECT "           
//                    + "  sSerialID "        
//                    + ", sFrameNox "        
//                    + ", sEngineNo "        
//                    + "FROM vehicle_serial ";
//
//            if(pnEditMode == EditMode.UPDATE){
//                lsSQL = MiscUtil.addCondition(lsSQL, " sEngineNo LIKE " + SQLUtil.toSQL(poModel.getEngnPtrn()+ "%")   
//                                                        //+ " AND a.cRecdStat = '1' "
//                                                        ) ;
//                System.out.println("EXISTING USAGE OF VEHICLE MODEL ENGINE PATTERN: " + lsSQL);
//                loRS = poGRider.executeQuery(lsSQL);
//
//                if (MiscUtil.RecordCount(loRS) > 0){
//                        while(loRS.next()){
//                            lsID = loRS.getString("sSerialID");
//                        }
//
//                        MiscUtil.close(loRS);
//                        
//                        jObj.put("result", "error");
//                        jObj.put("message", "Existing Vehicle Model Engine Pattern Usage.\n\nVehicle ID: " + lsID + "\nChanging of engine pattern aborted.");
//                        return jObj;
//                }
//            }
//        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_ModelEnginePattern_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    public JSONObject searchModel(String fsValue) {
        poJSON = new JSONObject();
        
        if(poModel.getMakeID() == null){
            poJSON.put("result", "error");
            poJSON.put("message", "Make cannot be Empty.");
            return poJSON;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Make cannot be Empty.");
                return poJSON;
            }
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
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
}
