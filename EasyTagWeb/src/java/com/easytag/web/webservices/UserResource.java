package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.EasyTag;
import com.easytag.core.entity.jpa.User;
import com.easytag.core.managers.TagManagerLocal;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonError;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.easytag.json.utils.TagExceptionWrapper;
import com.easytag.web.utils.SessionUtils;
import com.google.gson.Gson;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Vitaly
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    @Context
    private UriInfo context;
    @EJB
    UserManagerLocal userMan;

    /**
     * Creates a new instance of TagResource
     */
    public UserResource() {
    }

    @Path("{id}/update")
    @POST
    public Response updateProfile(@Context HttpServletRequest request, @PathParam("id") long userId, @FormParam("data") String jsonData) {
        try {
            HttpSession session = SessionUtils.getSession(request, false);
            Long currentUserId = SessionUtils.getUserId(session);
            if (currentUserId == null) {
                throw new TagException("Access denied: you should sign in first.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            User user = userMan.getUserById(userId);
            if (user == null) {
                throw new TagException("Specified userId doesn't exist.");
            }
            User updatedProfile = new Gson().fromJson(jsonData, User.class);
            updatedProfile.setId(userId);
            updatedProfile.setPassword(user.getPassword());
            updatedProfile.setEmail(user.getEmail());
            updatedProfile = userMan.updateUser(updatedProfile);
            updatedProfile.setPassword(null);
            return Response.ok(new Gson().toJson(updatedProfile)).build();
        } catch (TagException ex) {
            return Response.ok(TagExceptionWrapper.wrapException(ex)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    } 
    
}
