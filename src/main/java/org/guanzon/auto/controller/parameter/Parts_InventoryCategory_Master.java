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
import org.guanzon.auto.model.parameter.Model_Parts_Inventory_Category;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Parts_InventoryCategory_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Parts_Inventory_Category poModel;
    JSONObject poJSON;
    

    public Parts_InventoryCategory_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Parts_Inventory_Category(foGRider);
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

            poModel = new Model_Parts_Inventory_Category(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.newRecord();
            poModel.setCategrCd(MiscUtil.getNextCode(poModel.getTable(), "sCategrCd", true, loConn, psBranchCd));
            
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
        
        poModel = new Model_Parts_Inventory_Category(poGRider);
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
        String lsSQL =    " SELECT "        
                        + "    sCategrCd "  
                        + "  , sInvTypCd "  
                        + "  , sDescript "  
                        + "  , cRecdStat "  
                        + " FROM inventory_category "       ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " sDescript LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH INVENTORY CATEGORY: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sCategrCd»sDescript",
                "sCategrCd»sDescript",
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
    public Model_Parts_Inventory_Category getModel() {
        return poModel;
    }
    
    private JSONObject validateEntry(){
        JSONObject jObj = new JSONObject();
        try {
            
            if(poModel.getCategrCd()== null){
                jObj.put("result", "error");
                jObj.put("message", "ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getCategrCd().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getDescript()== null){
                jObj.put("result", "error");
                jObj.put("message", "Description cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getDescript().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Description cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sDescript,' ','') = " + SQLUtil.toSQL(poModel.getDescript().replace(" ",""))) +
                                                    " AND a.sCategrCd <> " + SQLUtil.toSQL(poModel.getCategrCd()) ;
            System.out.println("EXISTING INVENTORY CATEGORY CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sCategrCd");
                        lsDesc = loRS.getString("sDescript");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Inventory Category Description Record.\n\nCategory ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsSQL =   " SELECT "                                      
                    + "    a.sStockIDx "                              
                    + "  , a.sBarCodex "                              
                    + "  , a.sDescript "                              
                    + "  , a.sCategCd1 "                               
                    + "  , a.sCategCd2 "                              
                    + "  , a.sCategCd3 "                              
                    + "  , a.sCategCd4 "                             
                    + "  , b.sDescript "                              
                    + " FROM inventory a "                        
                    + " LEFT JOIN inventory_category b ON (b.sCategrCd = a.sCategCd1 OR b.sCategrCd = a.sCategCd2 OR b.sCategrCd = a.sCategCd3 OR b.sCategrCd = a.sCategCd4) ";
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " (a.sCategCd1 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd2 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd3 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd4 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " ) ") ;
                System.out.println("EXISTING USAGE OF INVENTORY CAGTEGORY: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBarCodex");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Inventory Category Usage.\n\nBarcode: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            if(pnEditMode == EditMode.UPDATE){
                lsID = "";
                lsSQL =   " SELECT "                                      
                        + "    a.sStockIDx "                              
                        + "  , a.sBarCodex "                              
                        + "  , a.sDescript "                              
                        + "  , a.sCategCd1 "                               
                        + "  , a.sCategCd2 "                              
                        + "  , a.sCategCd3 "                              
                        + "  , a.sCategCd4 "                             
                        + "  , b.sDescript "                              
                        + " FROM inventory a "                        
                        + " LEFT JOIN inventory_category b ON (b.sCategrCd = a.sCategCd1 OR b.sCategrCd = a.sCategCd2 OR b.sCategrCd = a.sCategCd3 OR b.sCategrCd = a.sCategCd4) ";
                lsSQL = MiscUtil.addCondition(lsSQL, " (a.sCategCd1 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd2 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd3 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " OR a.sCategCd4 = " + SQLUtil.toSQL(poModel.getCategrCd())
                                                        + " ) AND REPLACE(b.sDescript, ' ','') <> " + SQLUtil.toSQL(poModel.getDescript().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("INVENTORY: EXISTING USAGE OF INVENTORY CATEGORY: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBarCodex");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Inventory Category Usage.\n\nBarcode: " + lsID + "\nChanging of inventory category description aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Parts_InventoryCategory_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}