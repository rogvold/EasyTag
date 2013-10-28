package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.User;
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

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("album")
@Stateless
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

    @POST
    @Path("create")
    public String createAlbum(@Context HttpServletRequest req, String data) {
        try {
            System.out.println("ws createAlbum occured");

            System.out.println("data = " + data);
            
            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            System.out.println("userId = " + currentUserId);
            
            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }

            if (data == null) {
                throw new TagException("data is null");
            }

            //TODO(Vitaly): wrap this string with trycatch block throwing TagException
            Album album = new Gson().fromJson(data, Album.class);

            System.out.println("ws createAlbum: a = " + album);
            
            if (album == null) {
                throw new TagException("cannot deserialize album");
            }

            album = alMan.createAlbum(currentUserId, album.getName(), album.getDescription(), album.getTags(), album.getCategories(), null, album.getAvatarSrc());
            
            System.out.println("ws createAlbum: a = " + album);
            
            
            JsonResponse<Album> jr = new JsonResponse<Album>(ResponseConstants.OK, null, album);
            return SimpleResponseWrapper.getJsonResponse(jr);
        } catch (TagException e) {
            return TagExceptionWrapper.wrapException(e);
        }
    }
    
    @GET
    @Path("create")
    public String createSimpleAlbum(@Context HttpServletRequest req, @QueryParam("name") String name) {
        try {
            System.out.println("ws createAlbum occured");

            HttpSession session = req.getSession(false);
            Long currentUserId = SessionUtils.getUserId(session);

            System.out.println("userId = " + currentUserId);
            
            if (currentUserId == null) {
                throw new TagException("you sholud login first", ResponseConstants.NOT_AUTHORIZED_CODE);
            }

            //TODO(Vitaly): wrap this string with trycatch block throwing TagException


            Album album = alMan.createAlbum(currentUserId, name, null, null, null, null, null);
            
            
            
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
