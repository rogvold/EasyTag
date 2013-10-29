/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.managers.PhotoManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.easytag.json.utils.SimpleResponseWrapper;
import com.easytag.json.utils.TagExceptionWrapper;
import com.easytag.web.utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Vitaly
 */
@Path("Photo")
@Stateless
public class PhotoResource {

    @Context
    private UriInfo context;
    @EJB
    PhotoManagerLocal phMan;

    /**
     * Creates a new instance of PhotoResource
     */
    public PhotoResource() {
    }
    
    @GET
    @Path("getPhoto")
    public String getPhotoById(@Context HttpServletRequest req, @QueryParam("photoId") Long photoId){
        HttpSession session = req.getSession(false);
        Long currentUserId = SessionUtils.getUserId(session);
        Photo photo = phMan.getPhotoById(photoId);
        JsonResponse<Photo> jr = new JsonResponse<Photo>(ResponseConstants.OK, null, photo);
        return SimpleResponseWrapper.getJsonResponse(jr);
    }
    
    @GET
    @Path("getPhotos")
    public String getPhotosInAlbum(@Context HttpServletRequest req,  @QueryParam("albumId") Long albumId) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            List<Photo> photos = phMan.getPhotosInAlbum(albumId);
            JsonResponse<List<Photo>> jr = new JsonResponse<List<Photo>>(ResponseConstants.OK, null, photos);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("getPhotosAmount")
    public String getPhotosAmount(@Context HttpServletRequest req,  @QueryParam("albumId") Long albumId) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            Integer phAm = phMan.getPhotosAmount(albumId);
            JsonResponse<Integer> jr = new JsonResponse<Integer>(ResponseConstants.OK, null, phAm);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Path("addPhotos")
    public String addPhotos(@Context HttpServletRequest req, @FormParam("data") String data, @QueryParam("albumId") Long albumId) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }

            if (data == null) {
                throw new TagException("data is null");
            }

            //TODO(Vitaly): wrap this string with trycatch block throwing TagException
            List<Long> photosIdList = new Gson().fromJson(data, new TypeToken<List<Long>>(){}.getType());

            if (photosIdList == null) {
                throw new TagException("cannot deserialize photos");
            }
            
            List<Photo> photos = phMan.addPhotos(currentUserId, albumId, photosIdList);
            JsonResponse<List<Photo>> jr = new JsonResponse<List<Photo>>(ResponseConstants.OK, null, photos);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    

    /**
     * Retrieves representation of an instance of com.easytag.web.utils.PhotoResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PhotoResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
