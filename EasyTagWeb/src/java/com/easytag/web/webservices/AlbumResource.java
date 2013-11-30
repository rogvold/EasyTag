package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.managers.AlbumManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.easytag.json.utils.SimpleResponseWrapper;
import com.easytag.web.utils.SessionUtils;
import com.google.gson.Gson;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.easytag.json.utils.TagExceptionWrapper;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("album")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class AlbumResource {

    @Context
    private UriInfo context;
    @EJB
    AlbumManagerLocal alMan;

    /**
     * Creates a new instance of AlbumResource
     */
    public AlbumResource() {
    }

    @GET
    @Path("myAlbums")
    public String getMyAlbums(@Context HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            List<Album> albums = alMan.getUserAlbums(currentUserId);
            JsonResponse<List<Album>> jr = new JsonResponse<List<Album>>(ResponseConstants.OK, null, albums);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("removeAlbum")
    public String removeAlbumById(@Context HttpServletRequest req, @QueryParam("albumId") Long albumId){
        try {
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);
            
            if (currentUserId == null) {
                throw new TagException("you should login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            
            alMan.removeAlbum(albumId);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Path("/{albumId}/update")
    public String updateAlbum(@Context HttpServletRequest req, @PathParam("albumId") long albumId, @FormParam("data") String data) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            Album originalAlbum = alMan.getAlbumById(albumId);
            if (originalAlbum == null) {
                throw new TagException("Album doesn't exist.");
            }
            if (!userId.equals(originalAlbum.getCreatorId())) {
                throw new TagException("Operation is not permitted: you must be owner of the album to delete it.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            Album album = new Gson().fromJson(data, Album.class);
            if (album == null) {
                throw new TagException("Unabled to parse Album from json-string");
            }
            
            originalAlbum.setName(album.getName());
            originalAlbum.setDescription(album.getDescription());
            originalAlbum.setTags(album.getTags());
            
            originalAlbum = alMan.updateAlbum(originalAlbum);
            JsonResponse<Album> jr = new JsonResponse<Album>(ResponseConstants.OK, null, originalAlbum);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }

    @POST
    @Path("create")
    public String createAlbum(@Context HttpServletRequest req, @FormParam("data") String data) {
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
            Album album = new Gson().fromJson(data, Album.class);
            
            if (album == null) {
                throw new TagException("cannot deserialize album");
            }

            album = alMan.createAlbum(currentUserId, album.getName(), album.getDescription(), album.getTags(), album.getCategories(), null, album.getAvatarSrc());
            
            JsonResponse<Album> jr = new JsonResponse<Album>(ResponseConstants.OK, null, album);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Path("getUserAlbums")
    public String getUserAlbums(@Context HttpServletRequest req, @QueryParam("userId") Long userId) {
        HttpSession session = req.getSession(false);
        Long currentUserId = SessionUtils.getUserId(session);
        List<Album> albums = alMan.getUserAlbums(userId);
        JsonResponse<List<Album>> jr = new JsonResponse<List<Album>>(ResponseConstants.OK, null, albums);
        return SimpleResponseWrapper.getJsonResponse(jr);
    }

    /**
     * Retrieves representation of an instance of
     * com.easytag.web.webservices.AlbumResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of AlbumResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
