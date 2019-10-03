package org.fxapps.bc.management.rest;

import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.junit.Test;

/**
 * 
 * To run this test make sure you have a Business Central 7.x is running and you
 * correctly configured URL, username and password.
 * 
 * @author wsiqueir
 *
 */
public class ProjectResourceTest {

    private static final String SPACE_NAME = "test-from-client";
    private static final int TOTAL_ATTEMPTS = 3;
    ProjectResource projectResource = ProjectResourceFactory.get();

    @Test
    public void spacesTest() throws InterruptedException {
        Space space = new Space();
        space.setName(SPACE_NAME);
        space.setDefaultGroupId("org.fxapps");
        space.setOwner(ProjectResourceFactory.BC_USERNAME);
        System.out.println();
        System.out.println("ATTEMPT TO CREATE SPACE...");
        SpaceRequest request = projectResource.createSpace(space);
        followJob(request.getJobId());
        System.out.println("SUCCESS CREATING SPACE!");
        
        Thread.sleep(10000);
        
        System.out.println("ATTEMPT TO DELETE SPACE...");
        RemoveSpaceRequest deleteSpace = projectResource.deleteSpace(SPACE_NAME);
        followJob(deleteSpace.getJobId());
        System.out.println("DELETED SPACE");
    }

    private JobResult followJob(String jobId) throws InterruptedException {
        int totalAttempts = TOTAL_ATTEMPTS;
        System.out.println(">>CHECKING JOB STATUS");
        JobResult jobStatus = projectResource.getJobStatus(jobId);
        while (totalAttempts-- > 0  && ! approvedOrSuccess(jobStatus) ) {
            System.out.println(">> JOB NOT SUCCESSFUL: "+ jobStatus.getStatus());
            System.out.println(">> JOB CHECKINGS: " + (TOTAL_ATTEMPTS - totalAttempts));
            jobStatus = projectResource.getJobStatus(jobId);
            Thread.sleep(1000);
        }
        
        if (! approvedOrSuccess(jobStatus)) {
            throw new RuntimeException("JOB FAILED AFTER " + TOTAL_ATTEMPTS + " ATTEMPTS!");
        }
        System.out.println(">> JOB IS SUCCESSFUL!");
        return jobStatus;
    }

    private boolean approvedOrSuccess(JobResult jobStatus) {
        return jobStatus.getStatus().equals(JobStatus.SUCCESS) || jobStatus.getStatus().equals(JobStatus.APPROVED);
    }

}
