package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTag;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface TagManagerLocal {
    
    public EasyTag getEasyTagById(Long tagId);
    
    public List<EasyTag> getEasyTagsInPhoto(Long photoId);
    
    public EasyTag createEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height) throws TagException;
    
    public void assertTagExistance(Long photoId, Double x, Double y, Double width, Double height) throws TagException;
    
    public void removeEasyTag(Long userId, Long easyTagId) throws TagException;
    
    public EasyTag modifyEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height) throws TagException;
    
    public List<EasyTag> findEasyTagByName(String name) throws TagException;
    
    public List<EasyTag> fingEasyTagByDescription(String description) throws TagException;
    
    //TODO find by name, description
}
