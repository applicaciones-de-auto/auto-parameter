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
import org.guanzon.auto.model.parameter.Model_Bank_Branches;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Bank_Branches implements GRecord {

    GRider poGRider;
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;

    Model_Bank_Branches poModel;
    JSONObject poJSON;
    
    public Bank_Branches(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Bank_Branches(foGRider);
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

            poModel = new Model_Bank_Branches(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poModel.setBrBankID(MiscUtil.getNextCode(poModel.getTable(), "sBrBankID", true, loConn, psBranchCd+"BK"));
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
        
        poModel = new Model_Bank_Branches(poGRider);
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
        String lsSQL =    "   SELECT "                                           
                        + "   a.sBrBankID "                                    
                        + " , a.sBrBankNm "                                    
                        + " , a.sBrBankCd "                                    
                        + " , a.sBankIDxx "                                    
                        + " , a.sContactP "                                    
                        + " , a.sAddressx "                                     
                        + " , a.sZippCode "                                    
                        + " , a.cRecdStat "                                    
                        + " , b.sBankName "                                   
                        + " , c.sTownName "                                  
                        + " , d.sProvName "     
                        + " , UPPER(CONCAT(a.sAddressx,' ', c.sTownName, ', ', d.sProvName)) xAddressx "
                        + " FROM banks_branches a "                           
                        + " LEFT JOIN banks b ON b.sBankIDxx = a.sBankIDxx "  
                        + " LEFT JOIN towncity c ON c.sTownIDxx = a.sTownIDxx "
                        + " LEFT JOIN province d ON d.sProvIDxx = c.sProvIDxx " ;
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " b.sBankName LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND a.cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " b.sBankName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH BANKS BRANCHES: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Bank Branch ID»Bank Name»Branch»Address",
                "sBrBankID»sBankName»sBrBankNm»xAddressx",
                "a.sBrBankID»b.sBankName»a.sBrBankNm»UPPER(CONCAT(a.sAddressx,' ', c.sTownName, ', ', d.sProvName))",
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
    public Model_Bank_Branches getModel() {
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
            
            if(poModel.getBrBankID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Bank Branch ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getBrBankID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Bank Branch ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getBankID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Bank ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getBankID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Bank ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getBrBankNm()== null){
                jObj.put("result", "error");
                jObj.put("message", "Bank Branch Name cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getBrBankNm().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Bank Branch Name cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getAddress()== null){
                jObj.put("result", "error");
                jObj.put("message", "Address cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getAddress().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Address cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getTownID()== null){
                jObj.put("result", "error");
                jObj.put("message", "Town cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTownID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Town cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getZippCode()== null){
                jObj.put("result", "error");
                jObj.put("message", "Zippcode cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getZippCode().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Zippcode cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getTelNo()== null){
                jObj.put("result", "error");
                jObj.put("message", "Telephone Number cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTelNo().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Telephone Number cannot be Empty.");
                    return jObj;
                }
            }

            String lsID = "";
            String lsDesc  = "";
            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, "REPLACE(a.sBrBankNm,' ','') = " + SQLUtil.toSQL(poModel.getBrBankNm().replace(" ",""))) +
                                                    " AND a.sBankIDxx = " + SQLUtil.toSQL(poModel.getBankID()) +
                                                    " AND a.sBrBankID <> " + SQLUtil.toSQL(poModel.getBrBankID()) ;
            System.out.println("EXISTING BANKS BRANCH CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sBrBankID");
                        lsDesc = loRS.getString("sBrBankNm");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Bank Branch Record.\n\nBank ID: " + lsID + "\nBank Branch: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            //Deactivation Validation
            if(poModel.getRecdStat().equals("0")){
                //BANK APPLICATION
                lsID = "";
                lsSQL =   " SELECT "               
                        + "   sTransNox "          
                        + " , sApplicNo "          
                        + " , cPayModex "          
                        + " , sBrBankID "          
                        + " , cTranStat "          
                        + " FROM bank_application " ;
                lsSQL = MiscUtil.addCondition(lsSQL, " sBrBankID = " + SQLUtil.toSQL(poModel.getBrBankID())
                                                      //+ " cTranStat = 1"
                                                        ) ;
                System.out.println("EXISTING USAGE OF BANK BRANCH > BANK APPLICATION: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sApplicNo");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Bank Branch Usage in Bank Application.\n\nApplication No: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
                
                //BANK ACCOUNT
                lsID = "";
                lsSQL =   "   SELECT "           
                        + "   sBnAcctCD "        
                        + " , sBrBankID "        
                        + " , sAcctName "        
                        + " , sAcctNoxx "        
                        + " , sAcctType "        
                        + " , sAcctStat "        
                        + "FROM bank_accounts " ;

                lsSQL = MiscUtil.addCondition(lsSQL, " sBrBankID = " + SQLUtil.toSQL(poModel.getBrBankID())
                                                      //+ " sAcctStat = 1"
                                                        ) ;
                System.out.println("EXISTING USAGE OF BANK BRANCH > BANK ACCOUNT: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sAcctNoxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Bank Branch Usage in Bank Account.\n\nAccount No: " + lsID + "\nDeactivation aborted.");
                        return jObj;
                }
                
                //TODO:
                //CASHIER
                
                //ACCOUNTING
            }
            
            if(pnEditMode == EditMode.UPDATE){
                //BANK APPLICATION
                lsID = "";
                lsSQL =   " SELECT "               
                        + "   a.sTransNox "          
                        + " , a.sApplicNo "          
                        + " , a.cPayModex "          
                        + " , a.sBrBankID "          
                        + " , a.cTranStat "           
                        + " , b.sBrBankNm "         
                        + " FROM bank_application a " 
                        + " LEFT JOIN banks_branches b ON b.sBrBankID = a.sBrBankID" ;
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sBrBankID = " + SQLUtil.toSQL(poModel.getBrBankID())
                                                        + " AND REPLACE(b.sBrBankNm, ' ','') <> " + SQLUtil.toSQL(poModel.getBrBankNm().replace(" ", "")) 
                                                      //+ " cTranStat = 1"
                                                        ) ;
                System.out.println("EXISTING USAGE OF BANK BRANCH > BANK APPLICATION: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sApplicNo");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Bank Branch Usage in Bank Application.\n\nApplication No: " + lsID + "\nChanging of bank branch name aborted.");
                        return jObj;
                }
                
                //BANK ACCOUNT
                lsID = "";
                lsSQL =   "   SELECT "           
                        + "   a.sBnAcctCD "        
                        + " , a.sBrBankID "        
                        + " , a.sAcctName "        
                        + " , a.sAcctNoxx "        
                        + " , a.sAcctType "        
                        + " , a.sAcctStat "               
                        + " , b.sBrBankNm "    
                        + " FROM bank_accounts a " 
                        + " LEFT JOIN banks_branches b ON b.sBrBankID = a.sBrBankID" ;

                lsSQL = MiscUtil.addCondition(lsSQL, " a.sBrBankID = " + SQLUtil.toSQL(poModel.getBrBankID())
                                                        + " AND REPLACE(b.sBrBankNm, ' ','') <> " + SQLUtil.toSQL(poModel.getBrBankNm().replace(" ", "")) 
                                                      //+ " sAcctStat = 1"
                                                        ) ;
                System.out.println("EXISTING USAGE OF BANK BRANCH > BANK ACCOUNT: " + lsSQL);
                loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sAcctNoxx");
                        }

                        MiscUtil.close(loRS);

                        jObj.put("result", "error");
                        jObj.put("message", "Existing Bank Branch Usage in Bank Account.\n\nAccount No: " + lsID + "\nChanging of bank branch name aborted.");
                        return jObj;
                }
                
                //TODO:
                //CASHIER
                
                //ACCOUNTING
                
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Bank_Branches.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    /**
     * Searches for a province based on the provided value.
     *
     * @param fsValue the value used to search for a province
     * @return true if the province is found and set as the master record, false
     * otherwise
     */
    public JSONObject searchProvince(String fsValue, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        if (fbByCode){
            if (fsValue.equals((String) poModel.getProvID())) {
                loJSON.put("result", "success");
                loJSON.put("message", "Search province success.");
                return loJSON;
            }
        }else{
            String lsProvince = String.valueOf(poModel.getValue("sProvName"));
            
            if(!lsProvince.isEmpty()){
                if (fsValue.equals(lsProvince)){
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search province success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = " SELECT "
                    + " sProvName "
                    + ", sProvIDxx "
                    + " FROM Province  " 
                    + " WHERE cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Province", 
                            "sProvIDxx»sProvName", 
                            "sProvIDxx»sProvName", 
                            fbByCode ? 0 : 1);
            
        if (loJSON != null) {
            if("error".equals(loJSON.get("result"))){
                poModel.setProvID("");
                poModel.setProvName("");
                poModel.setTownID("");
                poModel.setTownName("");
                poModel.setZippCode("");
            } else {
                poModel.setProvID((String) loJSON.get("sProvIDxx"));
                poModel.setProvName((String) loJSON.get("sProvName"));
                poModel.setTownID("");
                poModel.setTownName("");
                poModel.setZippCode("");
            }
        }else {
            poModel.setProvID("");
            poModel.setProvName("");
            poModel.setTownID("");
            poModel.setTownName("");
            poModel.setZippCode("");
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
        
        return loJSON;
    }
    /**
     * Searches for a town based on the provided value.
     *
     * @param fsValue the value used to search for a province
     * @return true if the province is found and set as the master record, false
     * otherwise
     */
    public JSONObject searchTown(String fsValue, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        
        if(poModel.getProvID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Province cannot be empty.");
            return loJSON;
        } else {
            if(poModel.getProvID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Province cannot be empty.");
                return loJSON;
            }
        }
        
        if (fbByCode){
            if (fsValue.equals((String) poModel.getTownID())) {
                loJSON = new JSONObject();
                loJSON.put("result", "success");
                loJSON.put("message", "Search town success.");
                return loJSON;
            }
        }else{
            
            String townProvince = String.valueOf(poModel.getValue("sTownName"));
            if(!townProvince.isEmpty()){
                if (fsValue.equals(townProvince)){
                    loJSON = new JSONObject();
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search town success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = "SELECT " +
                            "  a.sTownIDxx" +
                            ", a.sTownName" + 
                            ", a.sZippCode" +
                            ", b.sProvName" + 
                            ", b.sProvIDxx" +
                        " FROM TownCity a" +
                            ", Province b" +
                        " WHERE a.sProvIDxx = b.sProvIDxx AND a.cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(poModel.getProvID()) + " AND a.sTownIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(poModel.getProvID()) + " AND a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Town»Postal Code»Province", 
                            "sTownIDxx»sTownName»sZippCode»sProvName", 
                            "a.sTownIDxx»a.sTownName»a.sZippCode»b.sProvName", 
                            fbByCode ? 0 : 1);
            
            if (loJSON != null) {
                if("error".equals(loJSON.get("result"))){
                    poModel.setTownID("");
                    poModel.setTownName("");
                    poModel.setZippCode("");
                } else {
                    poModel.setTownID((String) loJSON.get("sTownIDxx"));
                    poModel.setTownName((String) loJSON.get("sTownName"));
                    poModel.setZippCode((String) loJSON.get("sZippCode"));
                }
            }else {
                poModel.setTownID("");
                poModel.setTownName("");
                poModel.setZippCode("");
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
            
        return loJSON;
    }
}
