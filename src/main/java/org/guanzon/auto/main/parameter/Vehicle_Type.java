/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.parameter;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.parameter.Vehicle_Make_Master;
import org.guanzon.auto.controller.parameter.Vehicle_Type_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Type implements GRecord{
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psRecdStat;
    
    public JSONObject poJSON;
    
    Vehicle_Type_Master poController;
    Vehicle_Make_Master poMake;
    
    public Vehicle_Type(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Vehicle_Type_Master(foAppDrver,fbWtParent,fsBranchCd);
        poMake = new Vehicle_Make_Master(foAppDrver,fbWtParent,fsBranchCd); 
        
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
    public JSONObject updateRecord() {
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
            poJSON = openRecord((String) poJSON.get("sTypeIDxx"));
        }
        return poJSON;
    }

    @Override
    public Vehicle_Type_Master getModel() {
        return poController;
    }
    
    public JSONObject searchMake(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poMake.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sMakeIDxx", poJSON.get("sMakeIDxx"));
            poController.setMaster("sMakeDesc", poJSON.get("sMakeDesc"));
            
            System.out.println(poController.getMaster("sMakeDesc"));
        }
        
        return poJSON;
    }
    
    /**
    * Loads a list of type format records;
    * @return True if the list is successfully loaded, false if there are errors, the application driver is not set, or no records are found.
    */
    public JSONObject loadFormatType() {
        poJSON = new JSONObject();  
        poJSON = poController.loadFormatType();
        return poJSON;
    }
    
    /**
    * Searches for a vehicle type engine record based on the provided criteria.
    * @param fsValue The value to search for, used as a partial match for the 'sVhclSize' field.
    * @return True if a matching record is found, and the 'sVhclSize' field is set accordingly; false if no record is found.
    */
    public JSONObject searchEngineSize(String fsValue) {
        poJSON = new JSONObject();  
        poJSON = poController.searchEngineSize(fsValue);
        return poJSON;
    }
    
    /**
    * Searches for a vehicle type variant record based on the provided criteria.
    * @param fsValue  The value to search for, used as a partial match for the 'sVariantx' field.
    * @param fsVarGrp The variant group to filter the search results. set A for sVariantx_a, Otherwise set B for sVariantx_b
    * @return True if a matching record is found, and the 'sVariantx_a' and 'sVariantx_b' fields are set accordingly; false if no record is found.
    */
    public JSONObject searchVariantType(String fsValue, String fsVarGrp) {
        poJSON = new JSONObject();  
        poJSON = poController.searchVariantType(fsValue, fsVarGrp);
        return poJSON;
    }
    
}
