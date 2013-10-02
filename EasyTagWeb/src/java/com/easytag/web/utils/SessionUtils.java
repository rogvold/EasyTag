package com.easytag.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author danon
 */
public class SessionUtils {

    public static boolean isSignedIn() {
        if (SessionListener.getSessionAttribute(SessionListener.USER_ID_ATTRIBUTE_NAME, false) != null) {
            return true;
        }
        return false;
    }

    public static Long getUserId() {
        Long uId = ((Long) SessionListener.getSessionAttribute(SessionListener.USER_ID_ATTRIBUTE_NAME, true));
        return uId;
    }
    public static final String USER_ID_SESSION_ATTR = "userId";
//    private static final Logger log = Logger.getLogger(SessionUtils.class);

    public static <T> T getSessionAttribute(Class<T> clazz, final HttpSession session, String name) {
        try {
            if (isSessionValid(session)) {
                synchronized (session) {
                    return (T) session.getAttribute(name);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception whyle accesssing session attribute. ex = " + ex.getMessage());

        }
        return null;
    }

    public static void setSessionAttribute(final HttpSession session, String name, Object value) {
        try {
            if (isSessionValid(session)) {
                synchronized (session) {
                    session.setAttribute(name, value);
                    System.out.println("setSessionAttribute(): " + name + " is set to " + value);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception whyle accesssing session attribute. ex = " + ex.getMessage());

        }
    }

    public static boolean isSessionValid(final HttpSession session) {
        if (session == null) {
            return false;
        }
        try {
            synchronized (session) {
                long sd = session.getCreationTime();
            }
        } catch (IllegalStateException ex) {
            return false;
        }
        return true;
    }

    public static HttpSession getSession(HttpServletRequest request, boolean create) {
        if (request == null) {
            return null;
        }
        return request.getSession(create);
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getSessionAttribute(Long.class, getSession(request, false), USER_ID_SESSION_ATTR) != null;
    }

    public static Long getUserId(HttpSession session) {
        return getSessionAttribute(Long.class, session, USER_ID_SESSION_ATTR);
    }

    public static void setUserId(HttpSession session, Long userId) {
        setSessionAttribute(session, USER_ID_SESSION_ATTR, userId);
    }
}
