package com.easytag.web.webservices;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.EasyTagFile;
import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.managers.AlbumManagerLocal;
import com.easytag.core.managers.FileManagerLocal;
import com.easytag.core.managers.PhotoManagerLocal;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @EJB
    PhotoManagerLocal phMan;
    @EJB
    FileManagerLocal fiMan;

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
    public String removeAlbumById(@Context HttpServletRequest req, @QueryParam("albumId") Long albumId) {
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

    @GET
    @Path("/{albumId}/like")
    public String like(@Context HttpServletRequest req, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            alMan.likeAlbum(userId, albumId);
            long likes = alMan.getTotalLikes(albumId);
            long dislikes = alMan.getTotalDislikes(albumId);
            String result = "{\"likes\": \"" + likes + "\", \"dislikes\": \"" + dislikes +"\"}";
            return result;
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }

    @GET
    @Path("/{albumId}/dislike")
    public String dislike(@Context HttpServletRequest req, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            alMan.dislikeAlbum(userId, albumId);
            long likes = alMan.getTotalLikes(albumId);
            long dislikes = alMan.getTotalDislikes(albumId);
            String result = "{\"likes\": \"" + likes + "\", \"dislikes\": \"" + dislikes + "\"}";
            return result;
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }

    @GET
    @Path("/{albumId}/cancelLike")
    public String cancelLike(@Context HttpServletRequest req, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            alMan.deleteVote(userId, albumId);
            long likes = alMan.getTotalLikes(albumId);
            long dislikes = alMan.getTotalDislikes(albumId);
            String result = "{\"likes\": \"" + likes + "\", \"dislikes\": \"" + dislikes + "\"}";
            return result;
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }

    @GET
    @Path("/{albumId}/isVoted")
    public String isVoted(@Context HttpServletRequest req, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            if (alMan.isVoted(userId, albumId)) {
                if (alMan.isLiked(userId, albumId)) {
                    return "{\"voted\": \"like\"}";
                }
                return "{\"voted\": \"dislike\"}";
            }
            return "{\"voted\": \"none\"}";
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }

    @GET
    @Path("/{albumId}/getLikes")
    public String getLikes(@Context HttpServletRequest req, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(req, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            long likes = alMan.getTotalLikes(albumId);
            long dislikes = alMan.getTotalDislikes(albumId);
            String result = "{\"likes\": \"" + likes + "\", \"dislikes\": \"" + dislikes + "\"}";
            return result;
        } catch (TagException ex) {
            return TagExceptionWrapper.wrapException(ex);
        }
    }
    
    @GET
    @Path("/{albumId}/download")
    public Response download(@Context HttpServletRequest request, @PathParam("albumId") long albumId) {
        try {
            HttpSession session = SessionUtils.getSession(request, false);
            Long userId = SessionUtils.getUserId(session);
            if (userId == null) {
                throw new TagException("Access denied.", ResponseConstants.NOT_AUTHORIZED_CODE);
            }
            Album album = alMan.getAlbumById(albumId);
            List<Photo> photos = phMan.getPhotosInAlbum(albumId, false);
            List<EasyTagFile> photoFiles = new ArrayList<EasyTagFile>(photos.size());
            for (Photo photo : photos) {
                EasyTagFile easyTagFile = fiMan.findFileById(photo.getFileId());
                photoFiles.add(easyTagFile);
            }
            File zipFile = File.createTempFile("aloha", "amigo.zip");
            zipFiles(zipFile, photoFiles);
            String zipFileName = (album.getName()==null ? "album-"+albumId : "album-"+album.getName()) + ".zip";
            EasyTagFile file = fiMan.addFile(userId, zipFileName, zipFile.getAbsolutePath(), "application/zip");
            return Response.ok(SimpleResponseWrapper.getJsonResponse(new JsonResponse(ResponseConstants.OK, null, file)))
                    .build();
        } catch (TagException ex) {
            ex.printStackTrace();
            return Response.serverError().build();
        } catch (IOException ex) {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }

    public static final void zipFiles(File zip, List<EasyTagFile> files) throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zip));
            Set<String> fileNames = new HashSet<String>(files.size());
            for (EasyTagFile file : files) {
                zipFile(zos, file, fileNames);
            }
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static String extractNameWithoutExtension(String name) {
        if (name.lastIndexOf(".") >= 0) {
            return name.substring(0, name.lastIndexOf("."));
        } else return name;
    } 
    
    private static String extractExtension(String name) {
        if (name.lastIndexOf(".") >= 0) {
            return name.substring(name.lastIndexOf("."));
        } else return name;
    }

    private static void zipFile(ZipOutputStream zos, EasyTagFile file, Set<String> fileNames) throws IOException {
        String name = file.getOriginalName().toLowerCase();
        if (fileNames.contains(name.toLowerCase())) {
            int i = 2;
            String testName = (extractNameWithoutExtension(name) + " " + i + extractExtension(name)).toLowerCase(); 
            while (fileNames.contains(testName)) {
                i++;
            }
            name = testName;
        }
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        fileNames.add(name);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(file.getCurrentPath()));
            byte[] byteBuffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = fis.read(byteBuffer)) != -1) {
                zos.write(byteBuffer, 0, bytesRead);
            }
            zos.flush();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        zos.closeEntry();
        zos.flush();
    }
}
