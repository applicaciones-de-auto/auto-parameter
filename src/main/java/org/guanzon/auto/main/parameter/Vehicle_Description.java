/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.parameter;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.parameter.Vehicle_Color_Master;
import org.guanzon.auto.controller.parameter.Vehicle_Description_Master;
import org.guanzon.auto.controller.parameter.Vehicle_Make_Master;
import org.guanzon.auto.controller.parameter.Vehicle_Model_Master;
import org.guanzon.auto.controller.parameter.Vehicle_Type_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Description implements GRecord{
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psRecdStat;
    
    public JSONObject poJSON;
    
    Vehicle_Description_Master poController;
    Vehicle_Make_Master poMake;
    Vehicle_Type_Master poType;
    Vehicle_Color_Master poColor;
    
    public Vehicle_Description(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Vehicle_Description_Master(foAppDrver,fbWtParent,fsBranchCd);
        poMake = new Vehicle_Make_Master(foAppDrver,fbWtParent,fsBranchCd); 
        poType = new Vehicle_Type_Master(foAppDrver,fbWtParent,fsBranchCd); 
        poColor = new Vehicle_Color_Master(foAppDrver,fbWtParent,fsBranchCd); 
        
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
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
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
            poJSON = openRecord((String) poJSON.get("sVhclIDxx"));
        }
        return poJSON;
    }

    @Override
    public Vehicle_Description_Master getModel() {
        return poController;
    }
    
    public JSONObject searchMake(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poMake.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sMakeIDxx", poJSON.get("sMakeIDxx"));
            poController.setMaster("sMakeDesc", poJSON.get("sMakeDesc"));
        }
        
        return poJSON;
    }
    
    public JSONObject searchModel(String fsValue) {
        poJSON = new JSONObject();  
        poJSON = poController.searchModel(fsValue);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sModelIDx", poJSON.get("sModelIDx"));
            poController.setMaster("sModelDsc", poJSON.get("sModelDsc"));
        }
        
        return poJSON;
    }
    
    public JSONObject searchType(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poType.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sTypeIDxx", poJSON.get("sTypeIDxx"));
            poController.setMaster("sTypeDesc", poJSON.get("sTypeDesc"));
        }
        
        return poJSON;
    }
    
    public JSONObject searchColor(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poColor.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sColorIDx", poJSON.get("sColorIDx"));
            poController.setMaster("sColorDsc", poJSON.get("sColorDsc"));
        }
        
        return poJSON;
    }
    
}
