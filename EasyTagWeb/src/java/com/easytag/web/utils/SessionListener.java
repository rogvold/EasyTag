package com.easytag.web.utils;

import com.easytag.core.managers.Application;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple session listener implementation. Seems to be thread-safe.
 * <p/>
 * @author Danon
 */
public class SessionListener implements HttpSessionListener {
    
    public static final Logger log = LogManager.getLogger(SessionListener.class.getName());
    
    private static int totalActiveSessions;
 
    public static int getTotalActiveSession() {
        return totalActiveSessions;
    }

    @Override
    public void sessionCreated(HttpSessionEvent evt) {
        totalActiveSessions++;
        System.out.println("sessionCreated - totalActiveSessions = " + totalActiveSessions);
        HttpSession session = evt.getSession();
        Long userId = SessionUtils.getUserId(session);
        if (userId == null) {
            SessionUtils.setUserId(session, Application.getGuestUserId());
        }
        System.out.println("currentUserId = " + SessionUtils.getUserId(session));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        totalActiveSessions--;
        System.out.println("sessionDestroyed - totalActiveSessions = " + totalActiveSessions);
    }
}