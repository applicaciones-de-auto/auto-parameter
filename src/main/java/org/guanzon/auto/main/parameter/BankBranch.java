/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.parameter;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.parameter.Bank_Branches;
import org.guanzon.auto.controller.parameter.Bank_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class BankBranch  implements GRecord{
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psRecdStat;
    
    public JSONObject poJSON;
    
    Bank_Branches poController;
    Bank_Master poBank;
    
    public BankBranch(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Bank_Branches(foAppDrver,fbWtParent,fsBranchCd);
        poBank = new Bank_Master(foAppDrver,fbWtParent,fsBranchCd);
        
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
            poJSON = openRecord((String) poJSON.get("sBrBankID"));
        }
        return poJSON;
    }

    @Override
    public Bank_Branches getModel() {
        return poController;
    }
    
    public JSONObject searchBank(String fsValue, boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poBank.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poController.setMaster("sBankIDxx", poJSON.get("sBankIDxx"));
            poController.setMaster("sBankName", poJSON.get("sBankName"));
            poController.setMaster("sBankType", poJSON.get("sBankType"));
        }
        
        return poJSON;
    }
    
    /**
     * Search Town
     * @param fsValue searching for value
     * @param fbByCode set fbByCode into TRUE if you're searching Town by CODE, otherwise set FALSE.
     * @return 
     */
    public JSONObject searchTown(String fsValue, boolean fbByCode){
        return poController.searchTown(fsValue, fbByCode);
    }
    
    /**
     * Search Province
     * @param fsValue searching for value
     * @param fbByCode set fbByCode into TRUE if you're searching Province by CODE, otherwise set FALSE.
     * @return 
     */
    public JSONObject searchProvince(String fsValue, boolean fbByCode){
        return poController.searchProvince(fsValue, fbByCode);
    }
    
}
