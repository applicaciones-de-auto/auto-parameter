
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.parameter.Activity_Source;
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
public class ActivitySourceTest {
    
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
        Activity_Source model = new Activity_Source(instance, false, instance.getBranchCode());
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.newRecord();
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        System.err.println("result = " + (String) json.get("result"));
        
        json = model.setMaster("sActTypDs","TYPE DESCRIPTION");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sEventTyp","sal");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
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
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------RETRIEVAL--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        //retrieval
        json = model.openRecord("M001ACTP0001");
        System.err.println((String) json.get("message"));
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("ACTIVITY SOURCE TYPE");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("sActTypID  :  " + model.getMaster("sActTypID"));
        System.out.println("sActTypDs  :  " + model.getMaster("sActTypDs"));
        System.out.println("sEventTyp  :  " + model.getMaster("sEventTyp"));
        System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
        System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
        System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        
//        json = model.setMaster("sActTypDs","EDITED TYPE DESCRIPTION");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
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
//        json = model.deactivateRecord("M001ACTP0001");
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
        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.activateRecord("M001ACTP0001");
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        } else {
            System.out.println((String) json.get("message"));
            System.exit(0);
        }
        
        
        
    }
    
}
