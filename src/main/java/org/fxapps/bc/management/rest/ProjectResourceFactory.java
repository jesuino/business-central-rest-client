package org.fxapps.bc.management.rest;

import java.net.URL;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

/**
 * 
 * Old style singleton to create a client instance
 * 
 * @author wsiqueir
 *
 */
public class ProjectResourceFactory {
    
    
    final static String BC_URL = System.getProperty("business-central.url", "http://localhost:8080/business-central");
    final static String BC_USERNAME = System.getProperty("business-central.user", "rhpamAdmin");
    final static String BC_PASSWORD = System.getProperty("business-central.password", "redhat2019!");
    
    private static ProjectResource proxy;
    
    private ProjectResourceFactory() {}
    
    static {
        try {
            proxy = RestClientBuilder.newBuilder()
                             .register(new BasicAuthentication(BC_USERNAME, BC_PASSWORD))
                             .baseUrl(new URL(BC_URL))
                             .build(ProjectResource.class);
        } catch (Exception e) {
            throw new RuntimeException("Error creating the client", e);
        }
    }
    
    public static ProjectResource get() {
        return proxy;
    }

}
