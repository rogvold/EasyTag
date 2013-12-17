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
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("Photo")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
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
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            
            Photo photo = phMan.getPhotoById(photoId);
            JsonResponse<Photo> jr = new JsonResponse<Photo>(ResponseConstants.OK, null, photo);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Path("update")
    public String updatePhoto(@Context HttpServletRequest req, @FormParam("data") String data) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            Photo photo = new Gson().fromJson(data, Photo.class);
            if (photo == null) {
                throw new TagException("Unabled to parse Photo from json-string");
            }
            if (photo.getId() == null || photo.getAlbumId() == null) {
                throw new TagException("Parameter photoId mustn't be null.");
            }
            
            Photo originalPhoto = phMan.getPhotoById(photo.getId());
            if (originalPhoto == null) {
                throw new TagException("Photo doesn't exist.");
            }
            
            // check tphoto owner!
//            if (!userId.equals(originalPhoto.getCreatorId())) {
//                throw new TagException("Operation is not permitted: you must be owner of the album to delete it.", ResponseConstants.NOT_AUTHORIZED_CODE);
//            }
            
            if (photo.getAlbumId() != null) {
                originalPhoto.setAlbumId(photo.getAlbumId());
            }
            
            
            originalPhoto.setName(photo.getName());
            originalPhoto.setDescription(photo.getDescription());
            originalPhoto.setTags(photo.getTags());
            
            originalPhoto = phMan.updatePhoto(originalPhoto);
            JsonResponse<Photo> jr = new JsonResponse<Photo>(ResponseConstants.OK, null, originalPhoto);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }
    
    @GET
    @Path("deletePhoto")
    public String deletePhotoById(@Context HttpServletRequest req, @QueryParam("photoId") Long photoId){
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);
            
            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            
            phMan.deletePhoto(photoId);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
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
            List<Photo> photos = phMan.getPhotosInAlbum(albumId, false);
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
    
    @GET
    @Path("generateDefaultViews")
    public String generateDefaultViews(@Context HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);          
            
            phMan.generateDefaultViews();
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("generatePreViews")
    public String generatePreviews(@Context HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);          
            
            phMan.generatePreViews();
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("findPhotos")
    public String findPhotosByTagName(@Context HttpServletRequest req,  @QueryParam("q") String query) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            List<Photo> photos = phMan.findPhotosByTagName(query);
            JsonResponse<List<Photo>> jr = new JsonResponse<List<Photo>>(ResponseConstants.OK, null, photos);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("findPhotosLocally")
    public String findPhotosInAlbum(@Context HttpServletRequest req, @QueryParam("id") Long albumId, @QueryParam("q") String query) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            List<Photo> photos = phMan.findPhotosInAlbum(query, albumId);                    
            JsonResponse<List<Photo>> jr = new JsonResponse<List<Photo>>(ResponseConstants.OK, null, photos);
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
    
}
