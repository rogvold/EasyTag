package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.entity.jpa.Vote;
import com.easytag.core.enums.AlbumStatus;
import com.easytag.core.enums.AlbumType;
import com.easytag.core.enums.PhotoStatus;
import com.easytag.core.enums.VoteType;
import com.easytag.exceptions.TagException;
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
public class AlbumManager implements AlbumManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;
    
    @EJB
    PhotoManagerLocal pMan;

    @Override
    public Album getAlbumById(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return em.find(Album.class, albumId);
    }

    @Override
    public List<Album> getUserAlbums(Long userId) {
        Query q = em.createQuery("select a from Album a"
                + " where a.status <> :status"
                + " and a.creatorId = :userId").setParameter("userId", userId).setParameter("status", AlbumStatus.DELETED);
        List<Album> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return list;
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
        System.out.println("creating new album: a = " + a);
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

    @Override
    public void removeAlbum(Long id) throws TagException {
        Album a = getAlbumById(id);
        if (a == null){
            throw new TagException("Album is not specified");
        }
        
        List<Photo> photos = pMan.getPhotosInAlbum(id);
        for(Photo p:photos){
            p.setStatus(PhotoStatus.DELETED);  
            em.merge(p);
        }
        a.setStatus(AlbumStatus.DELETED);
        
        em.merge(a);
    }

    @Override
    public void likeAlbum(Long userId, Long albumId) {
        if (!isVoted(userId, albumId)) {
            Vote like = new Vote(userId, albumId, VoteType.LIKE);
            em.merge(like);
        }
    }

    @Override
    public void dislikeAlbum(Long userId, Long albumId) {
        if (!isVoted(userId, albumId)) {
            Vote dislike = new Vote(userId, albumId, VoteType.DISLIKE);
            em.merge(dislike);
        }
    }

    @Override
    public void deleteVote(Long userId, Long albumId) {
        Query q = em.createQuery("delete from Votes v where v.userId = :userId and v.albumId = :albumId")
                .setParameter("userId", userId).setParameter("albumId", albumId);
        q.executeUpdate();
    }

    @Override
    public boolean isVoted(Long userId, Long albumId) {
        Query q = em.createQuery("select v from Votes v where v.userId = :userId and v.albumId = :albumId")
                .setParameter("userId", userId).setParameter("albumId", albumId);
        List<Vote> votes = q.getResultList();
        return !(votes == null || votes.isEmpty());
    }

    @Override
    public long getTotalLikes(Long albumId) {
        Query q = em.createQuery("select count(v) from Votes v where v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("albumId", albumId).setParameter("voteType", VoteType.LIKE);
        return (Long) q.getSingleResult();
    }

    @Override
    public long getTotalDislikes(Long albumId) {
        Query q = em.createQuery("select count(v) from Votes v where v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("albumId", albumId).setParameter("voteType", VoteType.DISLIKE);
        return (Long) q.getSingleResult();
    }

    @Override
    public boolean isLiked(Long userId, Long albumId) {
        Query q = em.createQuery("select v from Votes v where v.userId = :userId and v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("userId", userId).setParameter("albumId", albumId).setParameter("voteType", VoteType.LIKE);
        List<Vote> votes = q.getResultList();
        return !(votes == null || votes.isEmpty());
    }
}
