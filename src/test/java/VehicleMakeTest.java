
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.parameter.Vehicle_Make;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
public class VehicleMakeTest {
    
    public static void main(String [] args){
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
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        Vehicle_Make model = new Vehicle_Make(instance, false, instance.getBranchCode());
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.newRecord();
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        System.err.println("result = " + (String) json.get("result"));
        
        json = model.setMaster("sMakeDesc","HONDA");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sMakeCode","");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sFormula1","");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sFormula2","");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
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
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------RETRIEVAL--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        //retrieval
        json = model.openRecord("M001MK000001");
        System.err.println((String) json.get("message"));
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("VEHICLE MAKE");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx"));
        System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc"));
        System.out.println("sMakeCode  :  " + model.getMaster("sMakeCode"));
        System.out.println("sFormula1  :  " + model.getMaster("sFormula1"));
        System.out.println("sFormula2  :  " + model.getMaster("sFormula2"));
        System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
        System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
        System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
        System.out.println("sModified  :  " + model.getMaster("sModified"));
        System.out.println("dModified  :  " + model.getMaster("dModified"));

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        
//        json = model.setMaster("sMakeDesc","MAKING");
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


        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------DEACTIVATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.deactivateRecord("M001MK000001");
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        } else {
            System.out.println((String) json.get("message"));
            System.exit(0);
        }

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.activateRecord("M001MK000001");
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
        
        
        
    }
    
}
