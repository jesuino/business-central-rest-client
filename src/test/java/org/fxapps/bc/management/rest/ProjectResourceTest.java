package org.fxapps.bc.management.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.junit.BeforeClass;
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

    private static final int INTERVAL_BETWEEN_REQUESTS = 6000;
    private static final int INTERVAL_BETWEEN_JOB_ATTEMPTS = 1000;
    private static final String SPACE_NAME = "testFromClient";
    private static final int TOTAL_ATTEMPTS = 3;
    ProjectResource projectResource = ProjectResourceFactory.get();
    private static Space space;
    
    @BeforeClass
    public static void setup() {
        space = new Space();
        space.setName(SPACE_NAME);
        space.setDefaultGroupId("org.fxapps");
        space.setDescription("This is a test description");
        space.setOwner(ProjectResourceFactory.BC_USERNAME);
    }


    @Test
    public void spacesTest() throws InterruptedException {
        // CREATING SPACE
        System.out.println("ATTEMPT TO CREATE SPACE...");
        createSpace(space);
        System.out.println("SUCCESS CREATING SPACE!");
        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        // GETTING SPACE
        Space space2 = projectResource.getSpace(SPACE_NAME);
        assertEquals(space.getDefaultGroupId(), space2.getDefaultGroupId());
        
        // GETTING SPACES
        Collection<Space> spaces = projectResource.getSpaces();
        assertTrue(spaces.size() >= 1);
        boolean present = spaces.stream()
                                .filter(s -> s.getName().equals(space.getName()))
                                .findFirst().isPresent();
        assertTrue(present);
        
        
        // PROJECTS TESTS
        System.out.println("PROJECTS TEST");
        projectsTest();
        
        // DELETING SPACE
        System.out.println("ATTEMPT TO DELETE SPACE...");
        deleteSpace();
        System.out.println("DELETED SPACE");
        
        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        // CHECKING IF SPACE WAS DELETED
        spaces = projectResource.getSpaces();
        present = spaces.stream()
                        .filter(s -> s.getName().equals(space.getName()))
                        .findFirst().isPresent();
        assertFalse(present);
        
    }
    
    public void projectsTest() throws InterruptedException {
        CreateProjectRequest newProjectRequest = new CreateProjectRequest();
        newProjectRequest.setDescription("Sample Project Description");
        newProjectRequest.setGroupId("org.fxapps");
        newProjectRequest.setName("project-from-client-" + System.currentTimeMillis());
        newProjectRequest.setVersion("1.0");
        
        System.out.println("ATTEMPT TO CREATE PROJECT");
        CreateProjectJobRequest createProjectJobRequest = projectResource.createProject(SPACE_NAME, newProjectRequest);
        followJob(createProjectJobRequest.getJobId());
        
        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        System.out.println("GETTING PROJECT");
        ProjectResponse project = projectResource.getProject(SPACE_NAME, newProjectRequest.getName());
        assertEquals(newProjectRequest.getName(), project.getName());
        assertEquals(newProjectRequest.getDescription(), project.getDescription());
        assertEquals(newProjectRequest.getGroupId(), project.getGroupId());
        assertEquals(newProjectRequest.getVersion(), project.getVersion());
        
        
        System.out.println("COMPILING PROJECT");
        CompileProjectRequest compileProject = projectResource.compileProject(SPACE_NAME, newProjectRequest.getName());
        followJob(compileProject.getJobId());
        
        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        System.out.println("TESTING PROJECT");
        TestProjectRequest testProject = projectResource.testProject(SPACE_NAME, newProjectRequest.getName());
        followJob(testProject.getJobId());
        
        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        System.out.println("INSTALLING PROJECT");
        InstallProjectRequest installProject = projectResource.installProject(SPACE_NAME, newProjectRequest.getName());
        followJob(installProject.getJobId());

        Thread.sleep(INTERVAL_BETWEEN_REQUESTS);
        
        DeleteProjectRequest deleteProject = projectResource.deleteProject(SPACE_NAME, newProjectRequest.getName());
        followJob(deleteProject.getJobId());
        
    }

    private void createSpace(Space space) throws InterruptedException {
        SpaceRequest request = projectResource.createSpace(space);
        followJob(request.getJobId());
    }

    private void deleteSpace() throws InterruptedException {
        RemoveSpaceRequest deleteSpace = projectResource.deleteSpace(SPACE_NAME);
        followJob(deleteSpace.getJobId());
    }

    private JobResult followJob(String jobId) throws InterruptedException {
        int totalAttempts = TOTAL_ATTEMPTS;
        System.out.println("\t>> CHECKING JOB STATUS");
        JobResult jobStatus = projectResource.getJobStatus(jobId);
        while (totalAttempts-- > 0  && ! approvedOrSuccess(jobStatus) ) {
            System.out.println("\t>> JOB NOT SUCCESSFUL: "+ jobStatus.getStatus());
            System.out.println("\t>> JOB CHECKINGS: " + (TOTAL_ATTEMPTS - totalAttempts));
            jobStatus = projectResource.getJobStatus(jobId);
            Thread.sleep(INTERVAL_BETWEEN_JOB_ATTEMPTS);
        }
        
        if (! approvedOrSuccess(jobStatus)) {
            throw new RuntimeException("JOB FAILED AFTER " + TOTAL_ATTEMPTS + " ATTEMPTS!");
        }
        System.out.println("\t>> JOB IS SUCCESSFUL!");
        return jobStatus;
    }

    private boolean approvedOrSuccess(JobResult jobStatus) {
        return jobStatus.getStatus().equals(JobStatus.SUCCESS) || jobStatus.getStatus().equals(JobStatus.APPROVED);
    }

}
