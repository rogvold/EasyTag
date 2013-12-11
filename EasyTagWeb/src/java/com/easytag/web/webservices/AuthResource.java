package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
import com.easytag.core.enums.UserType;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.easytag.json.utils.SimpleResponseWrapper;
import com.easytag.web.utils.SessionUtils;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.easytag.json.utils.TagExceptionWrapper;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("auth")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Context
    private UriInfo context;
    
    @EJB
    UserManagerLocal userMan;
    
    public static final org.apache.logging.log4j.Logger log = LogManager.getLogger(AuthResource.class.getName());

    /**
     * Creates a new instance of AuthResource
     */
    public AuthResource() {
    }

    
    @POST
    @Path("register")
    public String register(@Context HttpServletRequest req, @FormParam("email") String email, @FormParam("password") String password) {
        try {
            if (email == null || password == null) {
                throw new TagException("Email or password cannot be empty.");
            }
            User user = new User(email, password);
            log.trace("userMan = " + userMan);
            User u = userMan.registerUser(user.getEmail(), user.getPassword(), UserType.USER);
            if (u != null) {
                HttpSession session = SessionUtils.resetSession(req);
                SessionUtils.setUserId(session, u.getId());
            }
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Path("login")
    public String login(@Context HttpServletRequest req, @FormParam("email") String email, @FormParam("password") String password) {
        try {
            HttpSession session = SessionUtils.resetSession(req);
            if (email == null || password == null) {
                throw new TagException("Login and/or password = null");
            }
            User user = new User(email, password);
            user = userMan.login(user.getEmail(), user.getPassword());
            if (user == null) {
                throw new TagException("Incorrect pair email/password", ResponseConstants.LOGIN_FAILED_CODE);
            }
            
            SessionUtils.setUserId(session, user.getId());
            log.info("setting user id to session/ userId = " + user.getId());
            JsonResponse<User> jr = new JsonResponse<User>(ResponseConstants.OK, null, user);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Path("logout")
    public String logout(@Context HttpServletRequest req) {
        SessionUtils.resetSession(req);
        JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
        return SimpleResponseWrapper.getJsonResponse(jr);
    }
    
    @GET
    @Path("test")
    public String test(){
        return "reogfdgd";
    }

//    @POST
//    @Path("update")
//    public String update(@Context HttpServletRequest req,@FormParam("data") String data) {
//        try {
//            HttpSession session = req.getSession(false);
//            Long currentUserId = SessionUtils.getUserId(session);
//
//            if (currentUserId == null) {
//                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
//            }
//
//            if (data == null) {
//                throw new TagException("data is null");
//            }
//            User user = new Gson().fromJson(data, User.class);
//            if (user == null) {
//                throw new TagException("Gson: can't convert user");
//            }
//            if (user.getId() == null) {
//                throw new TagException("user id is not specified");
//            }
//
//            user = userMan.updateUserInfo(user.getId(), user.getFirstName(), user.getLastName(), user.getAvatarSrc());
//
//            JsonResponse<User> jr = new JsonResponse<User>(ResponseConstants.OK, null, user);
//            return SimpleResponseWrapper.getJsonResponse(jr);
//        } catch (TagException e) {
//            return TagExceptionWrapper.wrapException(e);
//        }
//    }

    @GET
    @Path("currentUser")
    public String currentUser(@Context HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);

            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }

            User user = userMan.getUserById(currentUserId);

            JsonResponse<User> jr = new JsonResponse<User>(ResponseConstants.OK, null, user);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("getUsers")
    public String getPhotosInAlbum(@Context HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            List<UserProfile> profiles = userMan.getAllUserProfiles();
            JsonResponse<List<UserProfile>> jr = new JsonResponse<List<UserProfile>>(ResponseConstants.OK, null, profiles);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    /**
     * Retrieves representation of an instance of com.easytag.web.webservices.AuthResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of AuthResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
