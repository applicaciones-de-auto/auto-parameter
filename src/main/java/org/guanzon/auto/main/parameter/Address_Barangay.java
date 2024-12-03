/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.parameter;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.parameter.Address_Barangay_Master;
import org.guanzon.auto.controller.parameter.Address_Province_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Address_Barangay implements GRecord{
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psRecdStat;
    
    public JSONObject poJSON;
    
    Address_Barangay_Master poController;
    
    public Address_Barangay(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Address_Barangay_Master(foAppDrver,fbWtParent,fsBranchCd);
        
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        pnEditMode = poController.getEditMode();
        return pnEditMode;
    }

    @Override
    public void setRecordStatus(String fsValue) {
        psRecdStat = fsValue;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject(); 
        obj.put("pnEditMode", pnEditMode);
        obj = poController.setMaster(fnCol, foData);
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newRecord();
            
            if("success".equals(poJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openRecord(fsValue);
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
        return poJSON;
    }

    @Override
    public JSONObject updateRecord(){
        poJSON = new JSONObject();  
        poJSON = poController.updateRecord();
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON =  poController.saveRecord();
        return poJSON;
    }

    @Override
    public JSONObject deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deactivateRecord(String fsValue) {
        return poController.deactivateRecord(fsValue);
    }

    @Override
    public JSONObject activateRecord(String fsValue) {
        return poController.activateRecord(fsValue);
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poController.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sBrgyIDxx"));
        }
        return poJSON;
    }

    @Override
    public Address_Barangay_Master getModel() {
        return poController;
    }
    
    public JSONObject searchProvince(String fsValue) {
        JSONObject loJSON = new JSONObject();  
        loJSON = poController.searchProvince(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getModel().setProvID((String) loJSON.get("sProvIDxx"));
            poController.getModel().setProvName((String) loJSON.get("sProvName"));
        }
        return loJSON;
    }
    
    public JSONObject searchTown(String fsValue) {
        JSONObject loJSON = new JSONObject();  
        
        if(poController.getModel().getProvID() == null){
            loJSON.put("result", "error");
            loJSON.put("message", "Province ID cannot be empty.");
            return loJSON;
        } else {
            if(poController.getModel().getProvID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Province ID cannot be empty.");
                return loJSON;
            }
        }
        
        loJSON = poController.searchTown(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getModel().setTownID((String) loJSON.get("sTownIDxx"));
            poController.getModel().setTownName((String) loJSON.get("sTownName"));
            poController.getModel().setZippCode((String) loJSON.get("sZippCode"));
        }
        return loJSON;
    }
    
}
