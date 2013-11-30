package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.easytag.json.utils.SimpleResponseWrapper;
import com.easytag.json.utils.TagExceptionWrapper;
import com.easytag.web.utils.SessionUtils;
import com.google.gson.Gson;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.tools.SimpleJavaFileObject;
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
            if (!currentUserId.equals(userId)) {
                throw new TagException("Access denied: you can not update another user profile.", ResponseConstants.ACCESS_DENIED_CODE);
            }
            User user = userMan.getUserById(userId);
            if (user == null) {
                throw new TagException("Specified userId doesn't exist.");
            }
            UserProfile updatedProfile = new Gson().fromJson(jsonData, UserProfile.class);
            UserProfile resultProfile = userMan.getUserProfile(user);
            // TODO: remove, when avatar implemented
            if (updatedProfile.getAvatarSrc() == null) {
                updatedProfile.setAvatarSrc(resultProfile.getAvatarSrc());
            }
            
            resultProfile.updateFromProfile(updatedProfile);
            resultProfile = userMan.updateUserProfile(resultProfile);
            return Response.ok(SimpleResponseWrapper.getJsonResponse(new JsonResponse(ResponseConstants.OK, null, resultProfile))).build();
        } catch (TagException ex) {
            return Response.ok(TagExceptionWrapper.wrapException(ex)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    } 
    
}
