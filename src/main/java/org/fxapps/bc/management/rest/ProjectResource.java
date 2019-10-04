package org.fxapps.bc.management.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;

@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProjectResource {
    
    @GET
    @Path("/jobs/{jobId}")
    public JobResult getJobStatus(@PathParam("jobId") String jobId);
    
    @DELETE
    @Path("/jobs/{jobId}")
    public JobResult removeJob(@PathParam("jobId") String jobId);
    
    @POST
    @Path("/spaces/{spaceName}/git/clone")
    public CloneProjectJobRequest cloneProject(@PathParam("spaceName") String spaceName, 
                                            CloneProjectRequest cloneProjectRequest);
    
    @POST
    @Path("/spaces/{spaceName}/projects")
    public CreateProjectJobRequest createProject(@PathParam("spaceName") String spaceName,
                                              CreateProjectRequest createProjectRequest);
    
    @GET
    @Path("/spaces/{spaceName}/projects")
    public Collection<ProjectResponse> getProjects(@PathParam("spaceName") String spaceName);
    
    @DELETE
    @Path("/spaces/{spaceName}/projects/{projectName}")
    public DeleteProjectRequest deleteProject(@PathParam("spaceName") String spaceName,
                                              @PathParam("projectName") String projectName);
    
    @GET
    @Path("/spaces/{spaceName}/projects/{projectName}")
    public ProjectResponse getProject(@PathParam("spaceName") String spaceName,
                                      @PathParam("projectName") String projectName); 
    
    @POST
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/compile")
    public CompileProjectRequest compileProject(@PathParam("spaceName") String spaceName,
                                                @PathParam("projectName") String projectName);
    
    @POST
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/install")
    public InstallProjectRequest installProject(@PathParam("spaceName") String spaceName,
                                                @PathParam("projectName") String projectName);
    
    @POST
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/test")
    public TestProjectRequest testProject(@PathParam("spaceName") String spaceName,
                                          @PathParam("projectName") String projectName);
    
    @POST
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/deploy")
    public DeployProjectRequest deployProject(@PathParam("spaceName") String spaceName,
                                              @PathParam("projectName") String projectName);
    
    @GET
    @Path("/spaces")
    public Collection<Space> getSpaces();
    
    @GET
    @Path("/spaces/{spaceName}")
    public Space getSpace(@PathParam("spaceName") String spaceName); 
    
    @POST
    @Path("/spaces")
    public SpaceRequest createSpace(Space space); 
    
    @DELETE
    @Path("/spaces/{spaceName}")
    public RemoveSpaceRequest deleteSpace(@PathParam("spaceName") String spaceName);
    
}