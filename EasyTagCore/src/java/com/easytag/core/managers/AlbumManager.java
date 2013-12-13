package com.easytag.core.managers;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.entity.jpa.Vote;
import com.easytag.core.enums.AlbumStatus;
import com.easytag.core.enums.AlbumType;
import com.easytag.core.enums.VoteType;
import com.easytag.exceptions.TagException;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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

    public static final Logger log = LogManager.getLogger(AlbumManager.class.getName());
    
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
        Album album = getAlbumByUserIdAndName(creatorId, name);
        if (album != null) {
            if (album.getStatus() != AlbumStatus.DELETED) {
                throw new TagException("you have already created album with name " + name);
            }
            album.setStatus(AlbumStatus.IN_PROGRESS);
            List<Photo> photos = pMan.getPhotosInAlbum(album.getId(), true);
            for (Photo photo : photos) {
                pMan.restorePhoto(photo.getId());
                log.trace("restoring photo: p = " + photo);
            }
            log.trace("restoring album: a = " + album);
        } else {
            album = new Album(creatorId, name, description, tags, categories, parentId, AlbumStatus.NEW, AlbumType.PRIVATE, avatarSrc);
            log.trace("creating new album: a = " + album);
        }
        return em.merge(album);

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
        if (a == null) {
            throw new TagException("Album is not specified");
        }

        List<Photo> photos = pMan.getPhotosInAlbum(id, false);
        for (Photo p : photos) {
            pMan.deletePhoto(p.getId());
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
        Query q = em.createQuery("delete from Vote v where v.userId = :userId and v.albumId = :albumId")
                .setParameter("userId", userId).setParameter("albumId", albumId);
        q.executeUpdate();
    }

    @Override
    public boolean isVoted(Long userId, Long albumId) {
        Query q = em.createQuery("select v from Vote v where v.userId = :userId and v.albumId = :albumId")
                .setParameter("userId", userId).setParameter("albumId", albumId);
        List<Vote> votes = q.getResultList();
        return !(votes == null || votes.isEmpty());
    }

    @Override
    public long getTotalLikes(Long albumId) {
        Query q = em.createQuery("select count(v) from Vote v where v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("albumId", albumId).setParameter("voteType", VoteType.LIKE);
        return (Long) q.getSingleResult();
    }

    @Override
    public long getTotalDislikes(Long albumId) {
        Query q = em.createQuery("select count(v) from Vote v where v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("albumId", albumId).setParameter("voteType", VoteType.DISLIKE);
        return (Long) q.getSingleResult();
    }

    @Override
    public boolean isLiked(Long userId, Long albumId) {
        Query q = em.createQuery("select v from Vote v where v.userId = :userId and v.albumId = :albumId and v.voteType = :voteType")
                .setParameter("userId", userId).setParameter("albumId", albumId).setParameter("voteType", VoteType.LIKE);
        List<Vote> votes = q.getResultList();
        return !(votes == null || votes.isEmpty());
    }
}
