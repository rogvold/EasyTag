package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTag;
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
public class TagManager implements TagManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;

    @EJB
    PhotoManagerLocal pMan;
    
    @Override
    public EasyTag getEasyTagById(Long tagId) {
        if (tagId == null) {
            return null;
        }
        return em.find(EasyTag.class, tagId);
    }

    @Override
    public List<EasyTag> getEasyTagsInPhoto(Long photoId) {
        Query q = em.createQuery("select t from EasyTag t where t.photoId = :photoId").setParameter("photoId", photoId);
        List<EasyTag> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public EasyTag createEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height) throws TagException{
        if ((x == null) || (y == null) || (width == 0) || (height == 0)){
            throw new TagException("x, y, width or heigth is not specified");
        }
        if(pMan.getPhotoById(photoId) == null){
            throw new TagException("There is no photo having such id");
        }    
        
        if (userId == null) {
            throw new TagException("User Id is not specified"); 
        }
        if(!userId.equals(pMan.getAlbumByPhotoId(photoId).getCreatorId())){
            throw new TagException("User is not creator");
        }
        
        assertTagExistance(photoId, x, y, width, height);        
        EasyTag tag = new EasyTag(photoId, name, description, x, y, width, height);
        return em.merge(tag);
        
    }

    @Override
    public void assertTagExistance(Long photoId, Double x, Double y, Double width, Double height) throws TagException{
        List<EasyTag> tagList = getEasyTagsInPhoto(photoId);
        for(EasyTag tag:tagList){
            if((((tag.getX() == x) && (tag.getY() == y)) && (tag.getWidth() == width)) &&
               (tag.getHeight() == height)){
                throw new TagException("There is a tag in such place");
            }
        }
    }

    @Override
    public void removeEasyTag(Long userId, Long easyTagId) throws TagException {
        if (userId == null) {
            throw new TagException("User Id is not specified"); 
        }
        
        EasyTag tag = getEasyTagById(easyTagId);
        
        if(!userId.equals(pMan.getAlbumByPhotoId(tag.getId()).getCreatorId())){
            throw new TagException("User is not creator");
        }     
        
        em.remove(tag);
    }

    @Override
    public EasyTag modifyEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height) throws TagException {
        if ((x == null) || (y == null) || (width == 0) || (height == 0)){
            throw new TagException("x, y, width or heigth is not specified");
        }
        if(pMan.getPhotoById(photoId) == null){
            throw new TagException("There is no photo having such id");
        }    
        
        if (userId == null) {
            throw new TagException("User Id is not specified"); 
        }
        if(!userId.equals(pMan.getAlbumByPhotoId(photoId).getCreatorId())){
            throw new TagException("User is not creator");
        }
        
        // create - assert )
        EasyTag tag = new EasyTag(photoId, name, description, x, y, width, height);
        return em.merge(tag);
    }

    @Override
    public List<EasyTag> findEasyTagByName(String name) throws TagException {
        Query q = em.createNativeQuery("select t from EasyTag t "
                + "where t.name like :name").setParameter("name", "\'%" 
                + name + "%\'");
        List<EasyTag> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public List<EasyTag> fingEasyTagByDescription(String description) throws TagException {
        Query q = em.createNativeQuery("select t from EasyTag t "
                + "where t.description like :description").setParameter("description", "\'%" 
                + description + "%\'");
        List<EasyTag> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }
}
