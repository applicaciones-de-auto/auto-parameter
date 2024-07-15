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
import org.guanzon.auto.model.parameter.Model_Vehicle_Make_Frame_Pattern;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_MakeFramePattern_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Make_Frame_Pattern poModel;
    
    JSONObject poJSON;

    public Vehicle_MakeFramePattern_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Make_Frame_Pattern(foGRider);
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

            poModel = new Model_Vehicle_Make_Frame_Pattern(poGRider);
            
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
        
        poModel = new Model_Vehicle_Make_Frame_Pattern(poGRider);
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
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), " a.sFrmePtrn LIKE "
                + SQLUtil.toSQL(fsValue + "%") );
        System.out.println("SEARCH MAKE FRAME PATTERN: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Make»Make Frame Pattern",
                "sMakeDesc»sFrmePtrn",
                "b.sMakeDesc»a.sFrmePtrn",
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
    public Model_Vehicle_Make_Frame_Pattern getModel() {
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

            if(poModel.getFrmePtrn().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make Frame Pattern cannot be Empty.");
                return jObj;
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sFrmePtrn,' ','') = " + SQLUtil.toSQL(poModel.getFrmePtrn().replace(" ",""))) +
                                                    " AND a.sMakeIDxx <> " + SQLUtil.toSQL(poModel.getMakeID()) +
                                                    " AND a.nEntryNox <> " + SQLUtil.toSQL(poModel.getEntryNo()) ;
            System.out.println("EXISTING VEHICLE MAKE FRAME PATTERN CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sMakeIDxx");
                        lsDesc = loRS.getString("sEngnPtrn");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Make Frame Pattern Record.\n\nMake ID: " + lsID + "\nMake Frame Pattern: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsSQL =   "  SELECT "           
                    + "  sSerialID "        
                    + ", sFrameNox "        
                    + ", sEngineNo "        
                    + "FROM vehicle_serial ";

            if(pnEditMode == EditMode.UPDATE){
                lsSQL = MiscUtil.addCondition(lsSQL, " sFrameNox LIKE " + SQLUtil.toSQL(poModel.getFrmePtrn()+ "%")   
                                                        //+ " AND a.cRecdStat = '1' "
                                                        ) ;
                System.out.println("EXISTING USAGE OF VEHICLE MAKE FRAME PATTERN: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sSerialID");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Make Frame Pattern Usage.\n\nVehicle ID: " + lsID + "\nChanging of frame pattern aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_MakeFramePattern_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}
