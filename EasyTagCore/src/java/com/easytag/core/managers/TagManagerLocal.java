package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTag;
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
    
    public EasyTag createEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height);
    
    
}
