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
import org.guanzon.auto.model.parameter.Model_Vehicle_Color;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Color_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Color poModel;
    JSONObject poJSON;

    public Vehicle_Color_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Color(foGRider);
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

            poModel = new Model_Vehicle_Color(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setColorID(MiscUtil.getNextCode(poModel.getTable(), "sColorIDx", false, loConn, psBranchCd+"CL"));
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
        
        poModel = new Model_Vehicle_Color(poGRider);
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
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Deactivation failed.");
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
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Activation failed.");
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
        String lsSQL = poModel.getSQL();
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL, " sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH COLOR: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sColorIDx»sColorDsc",
                "sColorIDx»sColorDsc",
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

    @Override
    public Model_Vehicle_Color getModel() {
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
            if(poModel.getColorID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Color ID cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getColorDsc() == null){
                    jObj.put("result", "error");
                    jObj.put("message", "Color Description cannot be Empty.");
                    return jObj;
            } else {
                if(poModel.getColorDsc().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Color Description cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sColorDsc,' ','') = " + SQLUtil.toSQL(poModel.getColorDsc().replace(" ",""))) +
                                                    " AND sColorIDx <> " + SQLUtil.toSQL(poModel.getColorID()) ;
            System.out.println("EXISTING VEHICLE COLOR CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sColorIDx");
                        lsDesc = loRS.getString("sColorDsc");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Color Description Record.\n\nColor ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            //Deactivation Validation
            //Do not allow deactivation when already connected thru vehicle master that is active or inactive.
            String lsVehicleID = "";
            if(poModel.getRecdStat().equals("0")){
                lsSQL =   "  SELECT "                                                
                        + "  a.sColorIDx "                                           
                        + ", b.sColorDsc "                                           
                        + ", b.sColorCde "                                           
                        + ", b.cRecdStat "                                           
                        + ", a.sVhclIDxx "                                           
                        + "FROM vehicle_master a "                                     
                        + "LEFT JOIN vehicle_color b ON b.sColorIDx = a.sColorIDx ";

                lsSQL = MiscUtil.addCondition(lsSQL, " a.sColorIDx = " + SQLUtil.toSQL(poModel.getColorID())
                                                        //" AND a.cRecdStat = '1'") ;
                                                        ) ;
                System.out.println("EXISTING USAGE OF VEHICLE COLOR: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsVehicleID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Color Usage.\n\nVehicle ID: " + lsVehicleID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            
            //Do not allow modification on description when already connected thru vehicle master that is active or inactive.
            lsVehicleID = "";
            lsSQL =   "  SELECT "                                                
                    + "  a.sColorIDx "                                           
                    + ", b.sColorDsc "                                           
                    + ", b.sColorCde "                                           
                    + ", b.cRecdStat "                                           
                    + ", a.sVhclIDxx "                                           
                    + "FROM vehicle_master a "                                     
                    + "LEFT JOIN vehicle_color b ON b.sColorIDx = a.sColorIDx ";
            if(pnEditMode == EditMode.UPDATE){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sColorIDx = " + SQLUtil.toSQL(poModel.getColorID()) 
                                                        + " AND REPLACE(b.sColorDsc, ' ', '') <> " + SQLUtil.toSQL(poModel.getColorDsc().replace(" ", "")) 
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("EXISTING USAGE OF VEHICLE COLOR: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsVehicleID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Color Usage.\n\nVehicle ID: " + lsVehicleID + "\nChanging of color description aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Color_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}
