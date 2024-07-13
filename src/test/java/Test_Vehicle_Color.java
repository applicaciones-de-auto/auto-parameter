
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.auto.controller.parameter.Vehicle_Color_Master;
import org.guanzon.auto.main.parameter.Vehicle_Color;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
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
public class Test_Vehicle_Color {
//    
//    static GRider instance;
//    static Vehicle_Color_Master record;
//
//    @BeforeClass
//    public static void setUpClass() {
//        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
//
//        instance = MiscUtil.Connect();
//        record = new Vehicle_Color_Master(instance, false);
//    }
//
//    @Test
//    public void testProgramFlow() {
//        JSONObject loJSON;
//
//        loJSON = record.newRecord();
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//
//        loJSON = record.getModel().setColorCde("US");
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//
//        loJSON = record.getModel().setColorDsc("33");
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//
//        loJSON = record.getModel().setModified(instance.getUserID());
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//
//        loJSON = record.getModel().setModifiedDte(instance.getServerDate());
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//
//        loJSON = record.saveRecord();
//        if ("error".equals((String) loJSON.get("result"))) {
//            Assert.fail((String) loJSON.get("message"));
//        }
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        record = null;
//        instance = null;
//    }
    
}
