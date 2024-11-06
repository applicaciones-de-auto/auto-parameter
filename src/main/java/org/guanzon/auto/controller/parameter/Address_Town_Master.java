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
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.parameter.Model_Address_Barangay;
import org.guanzon.auto.model.parameter.Model_Address_Province;
import org.guanzon.auto.model.parameter.Model_Address_Town;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Address_Town_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Address_Town poModel;
    JSONObject poJSON;
    
    public Address_Town_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Address_Town(foGRider);
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

            poModel = new Model_Address_Town(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.newRecord();
            poModel.setTownID(MiscUtil.getNextCode(poModel.getTable(), "sTownIDxx", false, loConn, ""));
            
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
    
    private Connection setConnection(){
        Connection foConn;
        
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        
        return foConn;
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Address_Town(poGRider);
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
    public JSONObject saveRecord(){
        
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
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND a.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH TOWN: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Town Name»Province Name",
                "sTownIDxx»sTownName»sProvName",
                "sTownIDxx»sTownName»sProvName",
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
    public Model_Address_Town getModel() {
        return poModel;
    }
    
    private JSONObject validateEntry(){
        JSONObject jObj = new JSONObject();
        try {
            if(poModel.getTownID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Town ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTownID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Town ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getProvID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Province cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getProvID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Province cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getTownName()== null){
                jObj.put("result", "error");
                jObj.put("message", "Town Name cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTownName().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Town Name cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sTownName,' ','') = " + SQLUtil.toSQL(poModel.getTownName().replace(" ",""))) +
                                                    " AND a.sTownIDxx <> " + SQLUtil.toSQL(poModel.getTownID()) ;
            System.out.println("EXISTING TOWN CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sTownIDxx");
                        lsDesc = loRS.getString("sTownName");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Town Record.\n\nTown ID: " + lsID + "\nTown Name: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            Model_Address_Barangay loEntity = new Model_Address_Barangay(poGRider);
            lsSQL = loEntity.makeSelectSQL();
//            Deactivation Validation 
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " sTownIDxx = " + SQLUtil.toSQL(poModel.getTownID())) ;
                System.out.println("EXISTING USAGE OF TOWN IN BARANGAY: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBrgyIDxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Town Usage.\n\nBarangay ID: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            if(pnEditMode == EditMode.UPDATE){
                lsID = "";
                lsSQL = loEntity.getSQL();
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sTownIDxx = " + SQLUtil.toSQL(poModel.getTownID()) 
                                                        + " AND REPLACE(b.sTownName, ' ','') <> " + SQLUtil.toSQL(poModel.getTownName().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("BARANGAY: EXISTING USAGE OF TOWN: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBrgyIDxx");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Town Usage.\n\nBarangay ID: " + lsID + "\nChanging of town name aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Address_Town_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    public JSONObject searchProvince(String fsValue) {
        JSONObject loJSON = new JSONObject();
        Model_Address_Province loEntitiy = new Model_Address_Province(poGRider);
        String lsSQL = loEntitiy.getSQL();
        lsSQL = MiscUtil.addCondition(lsSQL,  " a.sProvName LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                + " AND a.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE));
        
        
        System.out.println("SEARCH PROVINCE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Province Name",
                "sProvIDxx»sProvName",
                "sProvIDxx»sProvName",
                1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
}
