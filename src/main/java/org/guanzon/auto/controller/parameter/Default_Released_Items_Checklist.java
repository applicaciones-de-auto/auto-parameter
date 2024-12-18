/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.parameter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.parameter.Model_Default_Released_Items_Checklist;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Default_Released_Items_Checklist implements GRecord {
    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Default_Released_Items_Checklist poModel;
    ArrayList<Model_Default_Released_Items_Checklist> paDetail;
    JSONObject poJSON;

    public Default_Released_Items_Checklist(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Default_Released_Items_Checklist(foGRider);
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

            poModel = new Model_Default_Released_Items_Checklist(poGRider);
            Connection loConn = null;
            loConn = setConnection();
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
        
        poModel = new Model_Default_Released_Items_Checklist(poGRider);
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
        String lsSQL =   poModel.makeSelectSQL() ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " sItemDesc LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND cRecdStat = ") + SQLUtil.toSQL(RecordStatus.ACTIVE);
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " sItemDesc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH DEFAULT RELEASE ITEM: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Item Code»Item Description",
                "sItemCode»sItemDesc",
                "sItemCode»sItemDesc",
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
    public Model_Default_Released_Items_Checklist getModel() {
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
            
            if(poModel.getItemCode()== null){
                jObj.put("result", "error");
                jObj.put("message", "Item Code cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getItemCode().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Item Code cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getItemDesc()== null){
                jObj.put("result", "error");
                jObj.put("message", "Item Description cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getItemDesc().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Item Description cannot be Empty.");
                    return jObj;
                }
            }
            
            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sItemDesc,' ','') = " + SQLUtil.toSQL(poModel.getItemDesc().replace(" ",""))
                                                    + " AND sItemCode <> " + SQLUtil.toSQL(poModel.getItemCode())
                                                    ) ;
            System.out.println("EXISTING DEFAULT ITEM CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sItemCode");
                        lsDesc = loRS.getString("sItemDesc");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Default Item Record.\n\nItem Code: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            String lsSQLSelect =   " SELECT "                                             
                + "    a.sTransNox "                                     
                + "  , a.sItemType "                                     
                + "  , a.sItemCode "                                        
                + "  , a.nQuantity "                                     
                + "  , a.nReleased "                                       
                + " FROM vehicle_released_items a "                      
                + " LEFT JOIN default_released_items_checklist b ON b.sItemCode = a.sItemCode " ;
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQLSelect, " a.sItemType = 'd' AND a.sItemCode = " + SQLUtil.toSQL(poModel.getItemCode())) ;
                System.out.println("EXISTING USAGE OF DEFAULT ITEM: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sTransNox");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Default Released Item Usage.\n\nVehicle Gatepass ID: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            //Do not allow changing of Item Description when Item is already linked thru Vehicle Gatepass
            lsSQL = MiscUtil.addCondition(lsSQLSelect, " a.sItemType = 'd' AND a.sItemCode = " + SQLUtil.toSQL(poModel.getItemCode())
                                                        + " AND REPLACE(b.sItemDesc,' ','') <> " + SQLUtil.toSQL(poModel.getItemDesc().replace(" ",""))) ;
            System.out.println("EXISTING USAGE OF DEFAULT ITEM: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sTransNox");
                    }

                    MiscUtil.close(loRS);

                    jObj.put("result", "error");
                    jObj.put("message", "Existing Default Released Item Usage.\n\nVehicle Gatepass ID: " + lsID + "\nChanging of Item Description aborted.");
                    return jObj;
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Default_Released_Items_Checklist.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    
    public Model_Default_Released_Items_Checklist getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public ArrayList<Model_Default_Released_Items_Checklist> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    
    public void setDetailList(ArrayList<Model_Default_Released_Items_Checklist> foObj){this.paDetail = foObj;}
   
    public JSONObject loadDefaultItem(){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        Model_Default_Released_Items_Checklist loEntity = new Model_Default_Released_Items_Checklist(poGRider);
        String lsSQL =  loEntity.makeSelectSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, " cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE)) ;
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Default_Released_Items_Checklist(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sItemCode"));
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }
    
}
