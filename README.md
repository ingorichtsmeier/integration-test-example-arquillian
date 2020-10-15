# Integration Test with Arquillian and Wildlfy
A Process Application for [Camunda BPM](http://docs.camunda.org).

## How does it work?

This project shows how to build an Arquillian test to test a process instance running remotely on the preinstalled Wildfly server with a container managed shared process engine. It leverages the camunda-bpm-assert library.

The process contains the most important parts: 
* User task
* Asynchronous service task with Java delegate implementation using a separate service bean

Have a look into the [test class](src/test/java/com/camunda/consulting/test_example/integration/ArquillianTest.java) for further details.

The [pom.xml](pom.xml) contains all required dependencies. 

## How to use it?

### Unit Test
You can run the JUnit test [ProcessUnitTest](src/test/java/com/camunda/consulting/test_example/unit_test/ProcessUnitTest.java) in your IDE or using:

```bash
mvn clean test
```

### Integration Test
Run the Arquillian test either from your IDE or with the maven profile `arq-wildfly-remote`:

```bash
mvn clean test -Parq-wildfy-remote
```

You have to add the profile explcitly as the Arquillian tests are excluded from the default profile. 

The integration test deploys the war to the running wildfly server and executes the test here.

As the process engine has the job executor enabled, you have to delay the test before you can check for the completed process instance.

### Deployment to an Application Server
You can also build and deploy the process application to an application server.
For an easy start you can download JBoss Wildfly with a pre-installed Camunda
from our [Download Page](https://camunda.com/download/).

#### Manually
1. Build the application using:

```bash
mvn clean package
```
2. Copy the *.war file from the `target` directory to the deployment directory
of your application server e.g. `wildfly/standalone/deployments`.
For a faster 1-click (re-)deployment see the alternatives below.

#### Wildfly (using Wildfly Maven Plugin)
1. Build and deploy the process application using:

```bash
mvn clean wildfly:deploy
```

## Alternative: Postman Collection

An alternative to the Arquillian integration test is the usage of a Postman collection.

As the Camunda BPM Platform comes with a REST API, you can leverage this for integration tests as well.

To test the happy path of this simple process, just call the [start process instance](https://docs.camunda.org/manual/7.14/reference/rest/process-definition/post-start-process-instance/), [get task (list)](https://docs.camunda.org/manual/7.14/reference/rest/task/get-query/), [complete task](https://docs.camunda.org/manual/7.14/reference/rest/task/post-complete/) and [get historic process instance](https://docs.camunda.org/manual/7.14/reference/rest/history/process-instance/get-process-instance/) endpoints.

Before you can check for the completed process instance you have to delay the test. During this delay the process engine will execute the asynchronous continuation on the "Publish tweet" task.

Have a look at the [postman collection](postman-collection/Twitter-HappyPath.postman_collection.json) to test the happy path. 

The state of each request is [tested with some JavaScript commands](https://learning.postman.com/docs/writing-scripts/test-scripts/) to assert the state of the process.

## Environment Restrictions
Built and tested against Camunda BPM version 7.13.0.

## Known Limitations

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
