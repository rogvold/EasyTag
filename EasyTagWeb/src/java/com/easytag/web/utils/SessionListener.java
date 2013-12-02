package com.easytag.web.utils;

import java.util.*;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;

/**
 * A simple session listener implementation. Seems to be thread-safe.
 * <p/>
 * @author Danon
 */
public class SessionListener implements HttpSessionListener {

    public static final String USER_ID_ATTRIBUTE_NAME="userId";
    
    private static final Map<String, HttpSession> map = Collections.synchronizedMap(new HashMap<String, HttpSession>(500));
    
    public static final org.apache.logging.log4j.Logger log = LogManager.getLogger(SessionListener.class.getName());

    public static boolean isOnline(Long userId) throws Exception{
        if (map == null) {
            return false;
        }
        Set<Map.Entry<String, HttpSession>> entrySet = map.entrySet();
        if (entrySet == null) {
            return false;
        }
        for (Map.Entry<String, HttpSession> entry : entrySet) {
            HttpSession session = (HttpSession) entry.getValue();
            if (session == null) {
                continue;
            }
            if (!isSessionValid(session)) {
                continue;
            }
            Long uId = (Long) session.getAttribute(USER_ID_ATTRIBUTE_NAME);
            if (uId == null && userId == null) {
                return true;
            }
            if (userId == null) {
                continue;
            }
            if (uId != null && uId.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public static int getSessionsCount() {

        return map.size();
    }

    public static Map<String, HttpSession> getAllSessions() {
        return new HashMap(map);
    }

    public static int getOnlineAmount() {
        int k = 0;
        Set<Long> set = new HashSet();
        Set<Map.Entry<String, HttpSession>> entrySet = map.entrySet();
        if (entrySet == null) {
            return 0;
        }
        for (Map.Entry<String, HttpSession> entry : entrySet) {
            HttpSession session = (HttpSession) entry.getValue();
            if (!isSessionValid(session)) {
                continue;
            }
            Long uId = (Long) session.getAttribute(USER_ID_ATTRIBUTE_NAME);
            if (uId == null) {
                continue;
            }
            set.add(uId);
        }
        k = set.size();
        return k;
    }

    public static List<Long> getOnlineUsers() {
        int k = 0;
//        Set<String> set = new HashSet();
        List<Long> list = new ArrayList();
        Set<Map.Entry<String, HttpSession>> entrySet = map.entrySet();
        if (entrySet == null) {
            return null;
        }
        for (Map.Entry<String, HttpSession> entry : entrySet) {
            HttpSession session = (HttpSession) entry.getValue();
            if (!isSessionValid(session)) {
                continue;
            }
            Long uId = (Long) session.getAttribute(USER_ID_ATTRIBUTE_NAME);
            if (uId == null) {
                continue;
            }
            list.add(uId);
        }
        return list;
    }

    public static boolean isRequestedSessionValid() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return false;
        }
        ExternalContext ext = fc.getExternalContext();
        if (ext == null) {
            return false;
        }
        HttpServletRequest request = (HttpServletRequest) ext.getRequest();
        if (request == null) {
            return false;
        }
        return request.isRequestedSessionIdValid();
    }

    public static boolean isSessionValid(HttpSession session) {
        if (session == null) {
            return false;
        }
        try {
            session.getCreationTime();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    public static Object getSessionAttribute(String attrName, boolean createSession) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return null;
        }
        ExternalContext ctx = fc.getExternalContext();
        if (ctx == null) {
            return null;
        }
        HttpSession session = (HttpSession) ctx.getSession(createSession);
        if (isRequestedSessionValid()) {
            try {
                return session.getAttribute(attrName);
            } catch (IllegalStateException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Object getSessionAttribute(HttpSession session, String attrName) {
        if (isSessionValid(session)) {
            try {
                return session.getAttribute(attrName);
            } catch (IllegalStateException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static HttpSession getCurrentSession(boolean create) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return null;
        }
        ExternalContext ext = fc.getExternalContext();
        if (ext == null) {
            return null;
        }
        return (HttpSession) ext.getSession(create);
    }

    public static void setSessionAttribute(HttpSession session, String attrName, Object value) {
        try {
            if (session == null || !isSessionValid(session)) {
                return;
            }
            log.trace("setting session attribute:");
            log.trace("attrName = " + attrName);
            log.trace("value = " + value);
            session.setAttribute(attrName, value);
        } catch (Exception ex) {
            System.err.println("WARN: setSessionAttribute() failed!\n" + ex);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}