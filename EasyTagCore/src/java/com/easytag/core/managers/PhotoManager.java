package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.enums.PhotoStatus;
import com.easytag.exceptions.TagException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rogvold
 */
@Stateless
public class PhotoManager implements PhotoManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;
    
    @EJB
    AlbumManagerLocal aMan;

    @Override
    public Photo getPhotoById(Long photoId) {
        if (photoId == null) {
            return null;
        }
        return em.find(Photo.class, photoId);
    }

    @Override
    public List<Photo> getPhotosInAlbum(Long albumId) {
        Query q = em.createQuery("select p from Photo p where p.albumId = :albumId").setParameter("albumId", albumId);
        List<Photo> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public Integer getPhotosAmount(Long albumId) throws TagException {
        Query q = em.createQuery("select count(p) from Photo p where p.albumId = :albumId").setParameter("albumId", albumId);
        return ((Long) q.getSingleResult()).intValue();
    }

    @Override
    public Photo addPhoto(Long userId, Long albumId, String name, String description, String tags, Long fileId) throws TagException {
        
        if (albumId == null) {
            throw new TagException("Album Id is not specified"); 
        }
        if (userId == null) {
            throw new TagException("User Id is not specified"); 
        }
        Album album = em.find(Album.class, albumId);
        
        if (!userId.equals(album.getCreatorId())){
            throw new TagException("User is not creator");
        }
        
        if (fileId == null) {
            throw new TagException("File Id is not specified");
        }
        //TODO check file existance
        
        Photo photo =  new Photo(name, description, tags,
                PhotoStatus.NEW, getPhotosAmount(albumId) + 1, albumId, fileId);
        
        return em.merge(photo);
    }

    @Override
    public List<Photo> addPhotos(Long userId, Long albumId, List<Long> fileIdList) throws TagException {
        
        if (albumId == null) {
            throw new TagException("Album Id is not specified"); 
        }
        if (userId == null) {
            throw new TagException("User Id is not specified"); 
        }
        Album album = em.find(Album.class, albumId);
        
        if (!userId.equals(album.getCreatorId())){
            throw new TagException("User is not creator");
        }
        
        for(Long id:fileIdList){
            if (id == null) {
                throw new TagException("addPhotos: File Id is not specified");
            }
            //TODO check file existance
        }
        
        List<Photo> lp = new ArrayList<Photo>();
        Integer number = getPhotosAmount(albumId);
        for(Long id:fileIdList){
            number++;
            Photo photo =  new Photo(null, null, null, PhotoStatus.NEW,  number, albumId, id);
            em.merge(photo);
            lp.add(photo);
        }
        return lp;        
    }

    @Override
    public Album getAlbumByPhotoId(Long photoId) throws TagException {     
        Photo p = getPhotoById(photoId);
        if (p == null){
            throw new TagException("Photo is not specified");
        }
        return aMan.getAlbumById(p.getAlbumId());
    }
}
