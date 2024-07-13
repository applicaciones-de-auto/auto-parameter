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
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.parameter.Model_Vehicle_Type;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Type_Master implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Vehicle_Type poModel;
    JSONObject poJSON;

    public Vehicle_Type_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Vehicle_Type(foGRider);
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

            poModel = new Model_Vehicle_Type(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setTypeID(MiscUtil.getNextCode(poModel.getTable(), "sTypeIDxx", false, loConn, psBranchCd+"TP"));
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
        
        poModel = new Model_Vehicle_Type(poGRider);
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

            poJSON = poModel.saveRecord();
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "success");
                poJSON.put("message", "Activation success.");
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
//        String lsCondition = "";
//
//        if (psRecdStat.length() > 1) {
//            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
//                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
//            }
//
//            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
//        } else {
//            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
//        }
        

//        String lsSQL = MiscUtil.addCondition(poModel.makeSelectSQL(), " sTypeDesc LIKE "
//                + SQLUtil.toSQL(fsValue + "%") + " AND " + lsCondition);

        String lsSQL = poModel.getSQL();
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL, " sTypeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                + " AND cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " sTypeDesc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }


        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Name",
                "sTypeIDxx»sTypeDesc",
                "sTypeIDxx»sTypeDesc",
                1);

        if (poJSON != null) {
            poJSON.put("result", "success");
            poJSON.put("message", "New selected record.");
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
        return poJSON;
    }

    @Override
    public Model_Vehicle_Type getModel() {
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
            
            if(poModel.getTypeID().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type ID cannot be Empty.");
                return jObj;
            }

            if(poModel.getTypeDesc().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type Description cannot be Empty.");
                return jObj;
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(sTypeDesc,' ','') = " + SQLUtil.toSQL(poModel.getTypeDesc().replace(" ",""))) +
                                                    " AND sTypeIDxx <> " + SQLUtil.toSQL(poModel.getTypeID()) ;
            System.out.println("EXISTING VEHICLE TYPE CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sTypeIDxx");
                        lsDesc = loRS.getString("sTypeDesc");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Type Description Record.\n\nType ID: " + lsID + "\nDescription: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            //Deactivation Validation
            String lsVehicleID = "";
            lsSQL =   "  SELECT "                                                
                    + "  a.sTypeIDxx "                                           
                    + ", b.sTypeDesc "                                           
                    + ", b.sTypeCode "                                           
                    + ", b.cRecdStat "                                           
                    + ", a.sVhclIDxx "                                           
                    + "FROM vehicle_master a "                                     
                    + "LEFT JOIN vehicle_type b ON b.sTypeIDxx = a.sTypeIDxx ";
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) +
                                                        " AND a.cRecdStat = '1' ") ;
                System.out.println("EXISTING USAGE OF VEHICLE TYPE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsVehicleID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Type Usage.\n\nVehicle ID: " + lsVehicleID + "\nDeactivation aborted.");
                        return jObj;
                }
            }
            
            String lsTypeDesc = "";
            lsSQL =   "  SELECT "                                                
                    + "  a.sTypeIDxx "                                           
                    + ", b.sTypeDesc "                                           
                    + ", b.sTypeCode "                                           
                    + ", b.cRecdStat "                                           
                    + ", a.sVhclIDxx "                                           
                    + "FROM vehicle_master a "                                     
                    + "LEFT JOIN vehicle_type b ON b.sTypeIDxx = a.sTypeIDxx ";
            if(poModel.getRecdStat().equals("0")){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                                        + " AND REPLACE(b.sTypeDesc, ' ','') <> " + SQLUtil.toSQL(poModel.getTypeDesc().replace(" ", ""))   
                                                        //+ " AND a.cRecdStat = '1' "
                                                        ) ;
                System.out.println("EXISTING USAGE OF VEHICLE TYPE: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsVehicleID = loRS.getString("sVhclIDxx");
                        }

                        MiscUtil.close(loRS);
                        
                        jObj.put("result", "error");
                        jObj.put("message", "Existing Vehicle Type Usage.\n\nVehicle ID: " + lsVehicleID + "\nChanging of type description aborted.");
                        return jObj;
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Type_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    /**
    * Loads a list of type format records into the 'poTypeFormat' CachedRowSet based on the provided value.
    * @return True if the list is successfully loaded, false if there are errors, the application driver is not set, or no records are found.
    */
    public JSONObject loadFormatType() {
        JSONObject jObj = new JSONObject();
        try {
            if(poModel.getMakeID() != null){
                if(poModel.getMakeID().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("result", "Vehicle Make is not set.");
                    return jObj;
                }
            } else {
                jObj.put("result", "error");
                jObj.put("result", "Vehicle Make is not set.");
                return jObj; 
            }

            String lsFormula1 = "";
            String lsFormula2 = "";
            String lsMakeDesc = "";
            String lsDefaultx = "ENGINE_SIZE + VARIANT_A + VARIANT_B";
            String lsSQL =  "  SELECT" + 
                            "  sMakeIDxx" +
                            ",  sMakeDesc" +
                            ", IFNULL(sFormula1, '') sFormula1" + 
                            ", IFNULL(sFormula2, '') sFormula2" + 
                            " FROM vehicle_make " ;
            lsSQL = MiscUtil.addCondition(lsSQL, " sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()));
            
            System.out.println(lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsFormula1 = loRS.getString("sFormula1");
                    lsFormula2 = loRS.getString("sFormula2");
                    lsMakeDesc = loRS.getString("sMakeDesc");
                }
                
//                if(lsFormula1 != null && lsFormula2 != null){
//                    if(lsFormula1.isEmpty() && lsFormula2.isEmpty()){
//                        jObj.put("result", "error");
//                        jObj.put("message", "Please notify System Administrator to config Vehicle Make " + lsMakeDesc.toUpperCase() + " type format.");
//                        return jObj;
//                    }
//                } else {
//                    jObj.put("result", "error");
//                    jObj.put("message", "Please notify System Administrator to config Vehicle Make " + loRS.getString("sMakeDesc").toUpperCase() + " type format.");
//                    return jObj;
//                }
                
                MiscUtil.close(loRS);
//                if(lsDefaultx.trim().toLowerCase().equals(lsFormula1.trim().toCharArray()) && lsDefaultx.trim().equals(lsFormula2.trim().toLowerCase())){
//                    lsDefaultx = "";  
//                }
                jObj.put("sDefaultx", lsDefaultx);
                jObj.put("sFormula1", lsFormula1);
                jObj.put("sFormula2", lsFormula2);
            } 
        
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Type_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jObj.put("result", "success");
        jObj.put("message", "Vehicle Make Type Format load successfully.");
        return jObj;
    }
    
    /**
    * Searches for a vehicle type engine record based on the provided criteria.
    * @param fsValue The value to search for, used as a partial match for the 'sVhclSize' field.
    * @return True if a matching record is found, and the 'sVhclSize' field is set accordingly; false if no record is found.
    */
    public JSONObject searchEngineSize(String fsValue){
        JSONObject loJSON ;
        String lsSQL =  " SELECT " +
                        " sVhclSize " +   
                        " FROM vehicle_type_engine ";
        lsSQL = MiscUtil.addCondition(lsSQL, " sVhclSize LIKE " + SQLUtil.toSQL(fsValue + "%"));
        
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "Engine Size", 
                            "sVhclSize",
                            "sVhclSize",
                            0);
            
        if (loJSON != null) {
            poModel.setVhclSize((String) loJSON.get("sVhclSize"));

            loJSON.put("result", "success");
            loJSON.put("message", "Search spouse success.");
            return loJSON;
        }else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
    }
    
    /**
    * Searches for a vehicle type variant record based on the provided criteria.
    * @param fsValue  The value to search for, used as a partial match for the 'sVariantx' field.
    * @param fsVarGrp The variant group to filter the search results. set A for sVariantx_a, Otherwise set B for sVariantx_b
    * @return True if a matching record is found, and the 'sVariantx_a' and 'sVariantx_b' fields are set accordingly; false if no record is found.
    */
    public JSONObject searchVariantType(String fsValue, String fsVarGrp){
        JSONObject loJSON ;
        
        if(fsVarGrp.isEmpty()){
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "Variant Group cannot be empty.");
            return loJSON;
        }
        
        String lsSQL =  "  SELECT" +
                        "  sVariantx " +   
                        ", sVariantG " +   
                        "  FROM vehicle_type_variant ";
        
        lsSQL = MiscUtil.addCondition(lsSQL, " sVariantx LIKE " + SQLUtil.toSQL(fsValue + "%") +
                                                " AND UPPER(sVariantG) LIKE " + SQLUtil.toSQL(fsVarGrp.toUpperCase())
                                    );       
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "Variant»Variant Group", 
                            "sVariantx»sVariantG",
                            "sVariantx»sVariantG",
                            0);
            
        if (loJSON != null) {
            if(fsVarGrp.toUpperCase().equals("A")){
                poModel.setVarianta((String) loJSON.get("sVariantx"));
            } else {
                poModel.setVariantb((String) loJSON.get("sVariantx"));
            }

            loJSON.put("result", "success");
            loJSON.put("message", "Search record success.");
            return loJSON;
        }else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
    }
}


