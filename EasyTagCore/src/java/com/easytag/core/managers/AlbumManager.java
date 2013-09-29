package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.enums.AlbumStatus;
import com.easytag.core.enums.AlbumType;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rogvold
 */
@Stateless
public class AlbumManager implements AlbumManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;

    @Override
    public Album getAlbumById(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return em.find(Album.class, albumId);
    }

    @Override
    public List<Album> getUserAlbums(Long userId) {
        Query q = em.createQuery("select a from Album a where a.creatorId = :userId").setParameter("userId", userId);
        List<Album> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return null;
    }

    private Album getAlbumByUserIdAndName(Long userId, String name) throws TagException {
        if (userId == null) {
            throw new TagException("getAlbumByUserIdAndName: userId is null");
        }
        if (name == null || name.isEmpty()) {
            throw new TagException("getAlbumByUserIdAndName: name is null");
        }

        Query q = em.createQuery("select a from Album a where a.creatorId = :creatorId and a.name = :name").setParameter("creatorId", userId).setParameter("name", name);
        List<Album> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public Album createAlbum(Long creatorId, String name, String description, String tags, String categories, Long parentId, String avatarSrc) throws TagException {
        if (getAlbumByUserIdAndName(creatorId, name) != null) {
            throw new TagException("you have already created album with name " + name);
        }
        Album a = new Album(creatorId, name, description, tags, categories, parentId, AlbumStatus.NEW, AlbumType.PRIVATE, avatarSrc);
        return em.merge(a);
    }

    @Override
    public Album updateAlbum(Album album) throws TagException {
        if (album == null) {
            return null;
        }
        if (album.getId() == null) {
            throw new TagException("updateAlbum: id is not specified");
        }
        return em.merge(album);
    }
}
