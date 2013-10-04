package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface AlbumManagerLocal {
    
    public Album getAlbumById(Long albumId);
    public List<Album> getUserAlbums(Long userId);
    
    public Album createAlbum(Long creatorId, String name, String description, String tags, String categories, Long parentId, String avatarSrc) throws TagException;
    public Album updateAlbum(Album album) throws TagException;
    
    //TODO remove Album
    
}
