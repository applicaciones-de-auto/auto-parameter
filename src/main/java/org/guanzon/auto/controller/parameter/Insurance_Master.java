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
import org.guanzon.auto.model.parameter.Model_Insurance_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Insurance_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Insurance_Master poModel;
    JSONObject poJSON;

    public Insurance_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Insurance_Master(foGRider);
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

            poModel = new Model_Insurance_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setInsurID(MiscUtil.getNextCode(poModel.getTable(), "sInsurIDx", true, loConn, psBranchCd));
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
        
        poModel = new Model_Insurance_Master(poGRider);
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
        String lsSQL =    "  SELECT "     
                        + "   sInsurIDx " 
                        + " , sInsurNme " 
                        + " , cRecdStat " 
                        + "  FROM insurance_company " ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " sInsurNme LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " sInsurNme LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH INSURANCE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Insurance Name",
                "sInsurIDx»sInsurNme",
                "sInsurIDx»sInsurNme",
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
    public Model_Insurance_Master getModel() {
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
            
            if(poModel.getInsurID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Insurance ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getInsurID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Insurance ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getInsurNme()== null){
                jObj.put("result", "error");
                jObj.put("message", "Insurance Name cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getInsurNme().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Insurance Name cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getInsurCde()== null){
                jObj.put("result", "error");
                jObj.put("message", "Insurance Code cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getInsurCde().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Insurance Code cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sInsurNme,' ','') = " + SQLUtil.toSQL(poModel.getInsurNme().replace(" ",""))) +
                                                    " AND sInsurIDx <> " + SQLUtil.toSQL(poModel.getInsurID()) ;
            System.out.println("EXISTING INSURANCE CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sInsurIDx");
                        lsDesc = loRS.getString("sInsurNme");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Insurance Record.\n\nInsurance ID: " + lsID + "\nInsurance Name: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsSQL =    "  SELECT "       
                    + "   a.sBrInsIDx "  
                    + " , a.sInsurIDx " 
                    + " , a.sBrInsNme "    
                    + " , a.cRecdStat "    
                    + " , b.sInsurNme "    
                    + "  FROM insurance_company_branches a"
                    + " LEFT JOIN insurance_company b ON b.sInsurIDx = a.sInsurIDx"   ;
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sInsurIDx = " + SQLUtil.toSQL(poModel.getInsurID())) ;
                System.out.println("EXISTING USAGE OF INSURANCE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBrInsIDx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Insurance Usage.\n\nInsurance Branch ID: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            if(pnEditMode == EditMode.UPDATE){
                lsID = "";
                lsSQL =   "  SELECT "       
                        + "   a.sBrInsIDx "  
                        + " , a.sInsurIDx " 
                        + " , a.sBrInsNme "    
                        + " , a.cRecdStat "    
                        + " , b.sInsurNme "    
                        + "  FROM insurance_company_branches a"
                        + " LEFT JOIN insurance_company b ON b.sInsurIDx = a.sInsurIDx" ;
                
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sInsurIDx = " + SQLUtil.toSQL(poModel.getInsurID()) 
                                                        + " AND REPLACE(b.sInsurNme, ' ','') <> " + SQLUtil.toSQL(poModel.getInsurNme().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("INSURANCE BRANCH: EXISTING USAGE OF INSURANCE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBrInsNme");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Insurance Usage.\n\nInsurance Branch ID: " + lsID + "\nChanging of insurance name aborted.");
                        return jObj;
                }
                
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Insurance_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}
