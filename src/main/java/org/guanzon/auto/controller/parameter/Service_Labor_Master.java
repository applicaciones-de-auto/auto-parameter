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
import org.guanzon.auto.model.parameter.Model_Labor_Master;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Labor;
import org.guanzon.auto.model.service.Model_JobOrder_Labor;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Service_Labor_Master implements GRecord {
    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Labor_Master poModel;
    JSONObject poJSON;

    public Service_Labor_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Labor_Master(foGRider);
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

            poModel = new Model_Labor_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setLaborCde(MiscUtil.getNextCode(poModel.getTable(), "sLaborCde", true, loConn, psBranchCd));
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
        
        poModel = new Model_Labor_Master(poGRider);
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
            lsSQL = MiscUtil.addCondition(lsSQL, " sLaborDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                + " AND cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " sLaborDsc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH LABOR: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sLaborCde»sLaborDsc",
                "sLaborCde»sLaborDsc",
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
    public Model_Labor_Master getModel() {
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
            if(poModel.getLaborCde().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Labor ID cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getLaborDsc() == null){
                    jObj.put("result", "error");
                    jObj.put("message", "Labor Description cannot be Empty.");
                    return jObj;
            } else {
                if(poModel.getLaborDsc().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Labor Description cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sLaborDsc,' ','') = " + SQLUtil.toSQL(poModel.getLaborDsc().replace(" ",""))) +
                                                    " AND sLaborCde <> " + SQLUtil.toSQL(poModel.getLaborCde()) ;
            System.out.println("EXISTING LABOR CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsID = loRS.getString("sLaborCde");
                    lsDesc = loRS.getString("sLaborDsc");
                }

                MiscUtil.close(loRS);

                jObj.put("result", "error");
                jObj.put("message", "Existing Labor Description Record.\n\nLabor ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                return jObj;
            }
            
            //Deactivation Validation
            //Do not allow deactivation when already connected thru vehicle master that is active or inactive.
            if(poModel.getRecdStat().equals(RecordStatus.INACTIVE) || pnEditMode == EditMode.UPDATE){
                Model_JobOrder_Labor loJobOrder = new Model_JobOrder_Labor(poGRider);
                lsSQL = MiscUtil.addCondition(loJobOrder.makeSelectSQL(), " sLaborCde = " + SQLUtil.toSQL(poModel.getLaborCde())) ;
                System.out.println("EXISTING USAGE OF LABOR: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Labor Usage.\n\nDeactivation aborted.");
                        return jObj;
                }
                
                
                Model_VehicleSalesProposal_Labor loVSPLabor = new Model_VehicleSalesProposal_Labor(poGRider);
                lsSQL = MiscUtil.addCondition(loVSPLabor.makeSelectSQL(), " sLaborCde = " + SQLUtil.toSQL(poModel.getLaborCde())) ;
                System.out.println("EXISTING USAGE OF LABOR: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Labor Usage.\n\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            
            //Do not allow modification on description when already connected thru diagnostics and vsp labor that is active or inactive.
            if(pnEditMode == EditMode.UPDATE){
                Model_Labor_Master loOldEntity = new Model_Labor_Master(poGRider);
                JSONObject loJSON = loOldEntity.openRecord(poModel.getLaborCde());
                if(!"error".equals((String) loJSON.get("result"))){
                    if(!loOldEntity.getLaborCde().equals(poModel.getLaborDsc())){
                        Model_JobOrder_Labor loJobOrder = new Model_JobOrder_Labor(poGRider);
                        lsSQL = MiscUtil.addCondition(loJobOrder.makeSelectSQL(), " sLaborCde = " + SQLUtil.toSQL(poModel.getLaborCde())) ;
                        System.out.println("EXISTING USAGE OF LABOR: " + lsSQL);
                        loRS = poGRider.executeQuery(lsSQL);

                        if (MiscUtil.RecordCount(loRS) > 0){
                                MiscUtil.close(loRS);
                                jObj.put("result", "error");
                                jObj.put("message", "Existing Labor Usage.\n\nChanging of description aborted.");
                                return jObj;
                        }


                        Model_VehicleSalesProposal_Labor loVSPLabor = new Model_VehicleSalesProposal_Labor(poGRider);
                        lsSQL = MiscUtil.addCondition(loVSPLabor.makeSelectSQL(), " sLaborCde = " + SQLUtil.toSQL(poModel.getLaborCde())) ;
                        System.out.println("EXISTING USAGE OF LABOR: " + lsSQL);
                        loRS = poGRider.executeQuery(lsSQL);

                        if (MiscUtil.RecordCount(loRS) > 0){
                                MiscUtil.close(loRS);
                                jObj.put("result", "error");
                                jObj.put("message", "Existing Labor Usage.\n\nChanging of description aborted.");
                                return jObj;
                        }
                    }
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Service_Labor_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
}
