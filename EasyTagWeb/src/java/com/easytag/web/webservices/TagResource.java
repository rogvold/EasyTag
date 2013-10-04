/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytag.web.webservices;

import com.easytag.core.managers.TagManagerLocal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Vitaly
 */
@Path("Tag")
@Stateless
public class TagResource {

    @Context
    private UriInfo context;
    @EJB
    TagManagerLocal tagMan;

    /**
     * Creates a new instance of TagResource
     */
    public TagResource() {
    }

    
    
    
    /**
     * Retrieves representation of an instance of com.easytag.web.utils.TagResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of TagResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
