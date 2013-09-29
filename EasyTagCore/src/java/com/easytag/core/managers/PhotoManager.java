package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Photo;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rogvold
 */
@Stateless
public class PhotoManager implements PhotoManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;

    @Override
    public Photo getPhotoById(Long photoId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Photo> getPhotosInAlbum(Long albumId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getPhotosAmount(Long albumId) throws TagException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Photo addPhoto(Long userId, Long albumId, String name, String description, String tags, Long fileId) throws TagException {
        //Check rights here
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Photo> addPhotos(Long userId, Long albumId, List<Long> fileIdList) throws TagException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
