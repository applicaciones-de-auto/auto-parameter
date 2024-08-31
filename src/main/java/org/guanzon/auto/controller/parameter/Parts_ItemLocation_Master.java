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
import org.guanzon.auto.model.parameter.Model_Parts_Item_Location;
import org.guanzon.auto.model.parameter.Model_Parts_Item_Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Parts_ItemLocation_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Parts_Item_Location poModel;
    JSONObject poJSON;
    

    public Parts_ItemLocation_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Parts_Item_Location(foGRider);
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

            poModel = new Model_Parts_Item_Location(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.newRecord();
            poModel.setLocatnID(MiscUtil.getNextCode(poModel.getTable(), "sLocatnID", false, loConn, psBranchCd));
            
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
        
        poModel = new Model_Parts_Item_Location(poGRider);
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
                        + "    sLocatnID "  
                        + "  , sLocatnDs "  
                        + "  , sSectnIDx "  
                        + "  , sBinIDxxx "  
                        + "  , cRecdStat "  
                        + "  , cRecdStat "  
                        + " FROM item_location "       ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " sLocatnDs LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " sLocatnDs LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH ITEM LOCATION: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sLocatnID»sLocatnDs",
                "sLocatnID»sLocatnDs",
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
    public Model_Parts_Item_Location getModel() {
        return poModel;
    }
    
    private JSONObject validateEntry(){
        JSONObject jObj = new JSONObject();
        try {
            
            if(poModel.getLocatnID()== null){
                jObj.put("result", "error");
                jObj.put("message", "ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getLocatnID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getLocatnDs()== null){
                jObj.put("result", "error");
                jObj.put("message", "Location Description cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getLocatnDs().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Location Description cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getWHouseID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Warehouse cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getWHouseID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Warehouse cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getSectnID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Section cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getSectnID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Section cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getBinID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Bin cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getBinID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Bin cannot be Empty.");
                    return jObj;
                }
            }
            
            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sLocatnDs,' ','') = " + SQLUtil.toSQL(poModel.getLocatnDs().replace(" ",""))) +
                                                    " AND a.sLocatnID <> " + SQLUtil.toSQL(poModel.getLocatnID()) ;
            System.out.println("EXISTING ITEM LOCATION CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sLocatnID");
                        lsDesc = loRS.getString("sLocatnDs");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Item Location Description Record.\n\nItem Location ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsSQL =   " SELECT "                                      
                    + "    a.sStockIDx "                               
                    + "  , a.sLocatnID "                              
                    + "  , b.sLocatnDs "                             
                    + "  , c.sBarCodex "                              
                    + "  , c.sDescript "                              
                    + " FROM inv_master a "                        
                    + " LEFT JOIN item_location b ON b.sLocatnID = a.sLocatnID "
                    + " LEFT JOIN inventory c ON c.sStockIDx = a.sStockIDx ";
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sLocatnID = " + SQLUtil.toSQL(poModel.getLocatnID())) ;
                System.out.println("EXISTING USAGE OF ITEM LOCATION: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBarCodex");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Item Location Usage.\n\nBarcode: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            if(pnEditMode == EditMode.UPDATE){
                lsID = "";
                lsSQL =   " SELECT "                                      
                        + "    a.sStockIDx "                               
                        + "  , a.sLocatnID "                              
                        + "  , b.sLocatnDs "                             
                        + "  , c.sBarCodex "                              
                        + "  , c.sDescript "                              
                        + " FROM inv_master a "                        
                        + " LEFT JOIN item_location b ON b.sLocatnID = a.sLocatnID "
                        + " LEFT JOIN inventory c ON c.sStockIDx = a.sStockIDx ";
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sLocatnID = " + SQLUtil.toSQL(poModel.getLocatnID()) 
                                                        + " AND REPLACE(b.sLocatnDs, ' ','') <> " + SQLUtil.toSQL(poModel.getLocatnDs().replace(" ", ""))  
                                                        //+ " AND a.cRecdStat = '1'"
                                                        ) ;
                System.out.println("INVENTORY MASTER: EXISTING USAGE OF ITEM LOCATION: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sBarCodex");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Item Location Usage.\n\nBarcode: " + lsID + "\nChanging of item location description aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Parts_ItemLocation_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
}
