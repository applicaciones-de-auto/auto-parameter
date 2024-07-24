
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.parameter.Vehicle_Description;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VehicleDescriptionTest {
    static Vehicle_Description model;
    JSONObject json;
    boolean result;
    
    public VehicleDescriptionTest(){}
    
    @BeforeClass
    public static void setUpClass() {   
        
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        GRider instance = new GRider("gRider");
        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getMessage() + instance.getErrMsg());
            System.exit(1);
        }
        
        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        model = new Vehicle_Description(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    @Test
    public void test01NewRecord() {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.newRecord();
        if ("success".equals((String) json.get("result"))){
            json = model.setMaster("sMakeIDxx","M001MK000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sMakeDesc","HONDA");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sModelIDx","M001MD000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sModelDsc","PASSPORT");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sColorIDx","M001CL000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sColorDsc","BLACK");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sTypeIDxx","M001TP000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sTypeDesc","1.2 TURBO");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sTransMsn","AT");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("nYearModl",2024);
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("cVhclSize","1");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
                 
        } else {
            System.err.println("result = " + (String) json.get("result"));
            fail((String) json.get("message"));
        }
        
    }
    
    @Test
    public void test01NewRecordSave(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.saveRecord();
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        assertTrue(result);
    }
    
    @Test
    public void test02OpenRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------RETRIEVAL--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.openRecord("M001VM000001");
        
        if (!"success".equals((String) json.get("result"))){
            result = false;
        } else {
            System.out.println("--------------------------------------------------------------------");
            System.out.println("VEHICLE DESCRIPTION");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx"));
            System.out.println("sDescript  :  " + model.getMaster("sDescript"));
            System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx"));
            System.out.println("sModelIDx  :  " + model.getMaster("sModelIDx"));
            System.out.println("sColorIDx  :  " + model.getMaster("sColorIDx"));
            System.out.println("sTypeIDxx  :  " + model.getMaster("sTypeIDxx"));
            System.out.println("sTransMsn  :  " + model.getMaster("sTransMsn"));
            System.out.println("nYearModl  :  " + model.getMaster("nYearModl"));
            System.out.println("cVhclSize  :  " + model.getMaster("cVhclSize"));
            System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
            System.out.println("sModified  :  " + model.getMaster("sModified"));
            System.out.println("dModified  :  " + model.getMaster("dModified"));
            System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc"));
            System.out.println("sModelDsc  :  " + model.getMaster("sModelDsc"));
            System.out.println("sColorDsc  :  " + model.getMaster("sColorDsc"));
            System.out.println("sTypeDesc  :  " + model.getMaster("sTypeDesc"));
            result = true;
        }
        assertTrue(result);
    }
    
    @Test
    public void test03UpdateRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.updateRecord();
        System.err.println((String) json.get("message"));
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            result = true;
        }
        
        json = model.setMaster("sMakeIDxx","M001MK000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sModelIDx","M001MD000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sColorIDx","M001CL000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sTypeIDxx","M001TP000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sTransMsn","CVT");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("nYearModl",2024);
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("cVhclSize","2");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        assertTrue(result);
        //assertFalse(result);
    }
    
    @Test
    public void test03UpdateRecordSave(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------UPDATE RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.saveRecord();
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        assertTrue(result);
        //assertFalse(result);
    }
    
    @Test
    public void test04DeactivateRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------DEACTIVATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.deactivateRecord("M001VM000001");
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        
        assertTrue(result);
        //assertFalse(result);
    }
    
    @Test
    public void test04ActivateRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.activateRecord("M001VM000001");
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        
        assertTrue(result);
        //assertFalse(result);
    }
    
    
    
//    public static void main(String [] args){
//        String path;
//        if(System.getProperty("os.name").toLowerCase().contains("win")){
//            path = "D:/GGC_Maven_Systems";
//        }
//        else{
//            path = "/srv/GGC_Maven_Systems";
//        }
//        System.setProperty("sys.default.path.config", path);
//
//        GRider instance = new GRider("gRider");
//
//        if (!instance.logUser("gRider", "M001000001")){
//            System.err.println(instance.getErrMsg());
//            System.exit(1);
//        }
//
//        System.out.println("Connected");
//        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
//        
//        JSONObject json;
//        
//        System.out.println("sBranch code = " + instance.getBranchCode());
//        Vehicle_Description model = new Vehicle_Description(instance, false, instance.getBranchCode());
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newRecord();
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        System.err.println("result = " + (String) json.get("result"));
//        
//        json = model.setMaster("sMakeIDxx","M001MK000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sMakeDesc","HONDA");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sModelIDx","M001MD000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sModelDsc","PASSPORT");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sColorIDx","M001CL000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sColorDsc","BLACK");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTypeIDxx","M001TP000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTypeDesc","1.2 TURBO");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTransMsn","AT");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("nYearModl",2024);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cVhclSize","1");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveRecord();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        //retrieval
//        json = model.openRecord("M001VM000001");
//        System.err.println((String) json.get("message"));
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("VEHICLE DESCRIPTION");
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx"));
//        System.out.println("sDescript  :  " + model.getMaster("sDescript"));
//        System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx"));
//        System.out.println("sModelIDx  :  " + model.getMaster("sModelIDx"));
//        System.out.println("sColorIDx  :  " + model.getMaster("sColorIDx"));
//        System.out.println("sTypeIDxx  :  " + model.getMaster("sTypeIDxx"));
//        System.out.println("sTransMsn  :  " + model.getMaster("sTransMsn"));
//        System.out.println("nYearModl  :  " + model.getMaster("nYearModl"));
//        System.out.println("cVhclSize  :  " + model.getMaster("cVhclSize"));
//        System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
//        System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//        System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//        System.out.println("sModified  :  " + model.getMaster("sModified"));
//        System.out.println("dModified  :  " + model.getMaster("dModified"));
//        System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc"));
//        System.out.println("sModelDsc  :  " + model.getMaster("sModelDsc"));
//        System.out.println("sColorDsc  :  " + model.getMaster("sColorDsc"));
//        System.out.println("sTypeDesc  :  " + model.getMaster("sTypeDesc"));
//
//
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        
//        json = model.setMaster("sMakeIDxx","M001MK000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sModelIDx","M001MD000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sColorIDx","M001CL000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTypeIDxx","M001TP000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTransMsn","CVT");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("nYearModl",2024);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cVhclSize","2");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.saveRecord();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }


//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------DEACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.deactivateRecord("M00100000001");
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.activateRecord("M00100000001");
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
//    }
    
}
