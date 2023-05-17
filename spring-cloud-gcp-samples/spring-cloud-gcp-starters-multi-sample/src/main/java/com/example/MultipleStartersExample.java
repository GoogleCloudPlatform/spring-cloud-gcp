package com.example;

import com.google.cloud.functions.v2.FunctionServiceClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.workflows.executions.v1.CreateExecutionRequest;
import com.google.cloud.workflows.executions.v1.Execution;
import com.google.cloud.workflows.executions.v1.ExecutionsClient;
import com.google.cloud.workflows.executions.v1.WorkflowName;
import com.google.cloud.workflows.v1.LocationName;
import com.google.cloud.workflows.v1.Workflow;
import com.google.cloud.workflows.v1.WorkflowsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class MultipleStartersExample {

    @Autowired
    private LanguageServiceClient languageClient;

    @Autowired
    private FunctionServiceClient functionsClient;

    @Autowired
    private WorkflowsClient workflowsClient;

    @Autowired
    private ExecutionsClient executionsClient;

    @Value( "${workflows-project}" )
    private String workflowsProject;

    @Value("${workflows-location}")
    private String workflowsLocation;

    private static final String SAMPLE_TEXT = "Hello, world!";
    private static final String SAMPLE_WORKFLOW_ID = "workflow-" + UUID.randomUUID();

    void listWorkflows() {
        LocationName locationName = LocationName.of(workflowsProject, workflowsLocation);
        System.out.println("In location: " + locationName);
        for (Workflow element : workflowsClient.listWorkflows(locationName).iterateAll()) {
            System.out.println("Workflow found: " + element.getName());
        }
    }

//    Workflow createWorkflow() throws ExecutionException, InterruptedException {
//        LocationName locationName = LocationName.of(workflowsProject, workflowsLocation);
//        System.out.println("Creating workflow with ID " + SAMPLE_WORKFLOW_ID + ", location " + locationName);
//        Workflow newWorkflow = Workflow.newBuilder()
//                .setName(SAMPLE_WORKFLOW_ID)
//                .build();
//        CreateWorkflowRequest request =
//                CreateWorkflowRequest.newBuilder()
//                        .setParent(locationName.toString())
//                        .setWorkflow(newWorkflow)
//                        .setWorkflowId(SAMPLE_WORKFLOW_ID)
//                        .build();
//        try {
//            Workflow response = workflowsClient.createWorkflowAsync(request).get();
//            System.out.println("Created workflow: " + response.getName());
//            return response;
//        } catch (Exception ex) {
//            System.out.println("Exception: " + ex.getMessage());
//            ex.printStackTrace();
//            return null;
//        }
//    }

    void executeWorkflow(String workflowName) throws InterruptedException {
        System.out.println("Executing workflow: " + workflowName);
        CreateExecutionRequest request =
                CreateExecutionRequest.newBuilder()
                        .setParent(workflowName)
                        .setExecution(Execution.newBuilder().setArgument("{}").build())
                        .build();
        Execution response = executionsClient.createExecution(request);
        String executionName = response.getName();
        System.out.printf("Created execution: %s%n", executionName);

        // Wait for execution to finish, then print results.
        Execution execution = executionsClient.getExecution(executionName);
        while (execution.getState() == Execution.State.ACTIVE) {
            System.out.println("- Waiting for results");
            Thread.sleep(1000);
            execution = executionsClient.getExecution(executionName);
        }
        System.out.println("Execution finished with state: " + execution.getState().name());
        System.out.println("Execution results: " + execution.getResult());
    }

    String analyzeSentiment() {
        Document doc = Document.newBuilder().setContent(SAMPLE_TEXT).setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = languageClient.analyzeSentiment(doc).getDocumentSentiment();
        String result = String.format("Text: %s%n Sentiment: %s, %s%n",
                SAMPLE_TEXT, sentiment.getScore(), sentiment.getMagnitude());
        return result;
    }

    public static void main(String[] args) {
        SpringApplication.run(MultipleStartersExample.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
//            Workflow newWorkflow = createWorkflow();
            listWorkflows();
            String exampleWorkflow = WorkflowName.of(workflowsProject, workflowsLocation, "workflow-1").toString();
            executeWorkflow(exampleWorkflow);
        };
    }

}
