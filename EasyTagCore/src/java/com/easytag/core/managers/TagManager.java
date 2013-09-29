package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTag;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rogvold
 */
@Stateless
public class TagManager implements TagManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;

    @Override
    public EasyTag getEasyTagById(Long tagId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<EasyTag> getEasyTagsInPhoto(Long photoId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EasyTag createEasyTag(Long userId, Long photoId, String name, String description, Double x, Double y, Double width, Double height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
