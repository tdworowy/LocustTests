package test.java.com.example;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;


public class AppTest {

    //@Test
    //public void exampleTest(){
    public static void main(String[] args){
        // Engine
        StandardJMeterEngine jm = new StandardJMeterEngine();
        // jmeter.properties
        JMeterUtils.loadJMeterProperties("jmeter.properties");
        JMeterUtils.setJMeterHome("D:\\jmeter\\apache-jmeter-5.1.1\\"); //Path to jmeter
        JMeterUtils.initLocale();

        HashTree hashTree = new HashTree();     

        // HTTP Sampler
        HTTPSampler getData = new HTTPSampler();
        getData.setDomain("localhost");
        getData.setPort(3000);
        getData.setPath("/");
        getData.setMethod("GET");

        HTTPSampler postData = new HTTPSampler();
        postData.setDomain("localhost");
        postData.setPort(3000);
        postData.setPath("/data");
        postData.setMethod("POST");
        postData.setPostBodyRaw(true);
        postData.addNonEncodedArgument("body", "{ \"data1\": \"${__RandomString(20,abcdefg)}\", \"data2\":  \"${__RandomString(20,abcdefg)}\"}\"", "");
        

        // Loop Controller
        LoopController loopCtrl = new LoopController();
        loopCtrl.setLoops(1);
        loopCtrl.addTestElement(getData);
        loopCtrl.addTestElement(postData);
        loopCtrl.setFirst(true);

        // Thread Group
        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopCtrl);

        // Test plan
        TestPlan testPlan = new TestPlan("TEST PLAN");

        hashTree.add("testPlan", testPlan);
        hashTree.add("loopCtrl", loopCtrl);
        hashTree.add("threadGroup", threadGroup);
        hashTree.add("getData", getData);
        hashTree.add("postData", postData);

        try {
            SaveService.saveTree(hashTree, new FileOutputStream(
                    "test.jmx"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        jm.configure(hashTree);
        jm.run();

    }
}