package com.camunda.consulting.test_example.unit_test;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.camunda.consulting.test_example.CreateTweetDelegate;
import com.camunda.consulting.test_example.TwitterService;

import twitter4j.TwitterException;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessUnitTest {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "test-example";
  
  @Mock
  public TwitterService mockedTwitterService;

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() throws TwitterException {
    Mocks.register("createTweetDelegate", new CreateTweetDelegate(mockedTwitterService));
    Mockito.when(mockedTwitterService.publishTweet(Mockito.anyString())).thenReturn(100L);
    
	  ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
	      withVariables("content", "Some test"));
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  assertThat(processInstance)
	      .isWaitingAt(findId("Approve tweet"))
	      .task()
	      .hasCandidateGroup("management");
	  
	  complete(task(), withVariables("approved", true));
	  
	  assertThat(processInstance).isWaitingAt(findId("Publish tweet"));
	  
	  execute(job());
	  
	  assertThat(processInstance).isEnded().variables().containsEntry("tweetId", 100L);
	  Mockito.verify(mockedTwitterService).publishTweet("Some test");
  }

}
