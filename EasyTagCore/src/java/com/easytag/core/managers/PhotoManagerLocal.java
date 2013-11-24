package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.Photo;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface PhotoManagerLocal {
    
    public Photo getPhotoById(Long photoId);
    public List<Photo> getPhotosInAlbum(Long albumId);
    
    public Integer getPhotosAmount(Long albumId) throws TagException;
    

    public Photo addPhoto(Long userId, Long albumId, String name, String description, String tags, Long fileId, Long height, Long width) throws TagException;
    
    public List<Photo> addPhotos(Long userId, Long albumId, List<Long> fileIdList) throws TagException;
    
    public Album getAlbumByPhotoId(Long photoId) throws TagException;
    
    public void generatePreview(Photo p) throws TagException;
    
    //TODO find by tag
    public List<Photo> findPhotosByTagName(String query);
    
    //TODO deletePhoto
}
