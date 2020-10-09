package com.camunda.consulting.test_example.integration;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ArquillianTest {
  
  private static final String PROCESS_DEFINITION_KEY = "test-example";

  @Deployment
  public static WebArchive createDeployment() {
    // resolve given dependencies from Maven POM
    File[] libs = Maven.resolver()
      //.offline(false)
      .loadPomFromFile("pom.xml")
      .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
    
    WebArchive resource = ShrinkWrap
    .create(WebArchive.class, "test-example.war")
    // add needed dependencies
    .addAsLibraries(libs)
    // prepare as process application archive for camunda BPM Platform
    .addAsWebResource("META-INF/processes.xml", "WEB-INF/classes/META-INF/processes.xml")
    // enable CDI
    //.addAsWebResource("WEB-INF/beans.xml", "WEB-INF/beans.xml")
    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
    // boot JPA persistence unit
    .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
    // add your own classes (could be done one by one as well)
    .addPackages(false, "com.camunda.consulting.test_example") // not recursive to skip package 'nonarquillian'
    // add process definition
    .addAsResource("process.bpmn")
    // now you can add additional stuff required for your test case
    ;
    
    System.out.println(resource.toString(true));

    return resource; 
  }

  @Inject
  private ProcessEngine processEngine;
  
  /**
   * Tests that the process is executable and reaches its end.
   */
  @Test
  public void testProcessExecution() throws Exception {
    
    cleanUpRunningProcessInstances();
    
    ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
        withVariables("content", "testExample " + new Date().getTime()));
    
    assertEquals(1, processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).active().count());
    
    Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    assertEquals("Approve tweet", task.getName());
    
    processEngine.getTaskService().complete(task.getId(), withVariables("approved", true));
    
    assertEquals(1, processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).finished().count());    
  }

  /**
   * Helper to delete all running process instances, which might disturb our Arquillian Test case
   * Better run test cases in a clean environment, but this is pretty handy for demo purposes
   */
  private void cleanUpRunningProcessInstances() {
    List<ProcessInstance> runningInstances = processEngine.getRuntimeService().createProcessInstanceQuery().processDefinitionKey(PROCESS_DEFINITION_KEY).list();
    for (ProcessInstance processInstance : runningInstances) {
      processEngine.getRuntimeService().deleteProcessInstance(processInstance.getId(), "deleted to have a clean environment for Arquillian");
    }
  }  
}
