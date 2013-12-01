package com.easytag.web.servlets;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.exceptions.TagException;
import com.easytag.web.utils.OpenIdUtils;
import com.easytag.web.utils.SessionListener;
import com.easytag.web.utils.SessionUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rogvold
 */
public class LoginzaServlet extends HttpServlet {

    @EJB
    UserManagerLocal userMan;
    private transient HttpSession session = null;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println(request.getParameterMap());
            System.out.println(request.getParameterMap());


//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet LoginzaServlet</title>");            
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet LoginzaServlet at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        response.setContentType("text/html");
        //  processRequest(request, response);
        PrintWriter out = response.getWriter();
        try {

            session = request.getSession(false);
            // working with get params

            System.out.println("-----------------------------------------------------------");
            System.out.println("openId -> processRequest");
            System.out.println("--------------------");


            Map params = request.getParameterMap();

            System.out.println("greetings from servlet! params = " + params);

            Iterator i = params.keySet().iterator();
//            String s = "#";
            String s = "#";
            System.out.println("wtf s = " + s);
//            int k = 0;
//            while (i.hasNext()) {
//
//                String key = (String) i.next();
//                if (("code".equals(key) || ("token".equals(key)))) {
//                    continue;
//                }
//                String value = ((String[]) params.get(key))[0];
//                if (k == 0) {
//                    s = s + "" + key + "=" + value;
//                } else {
//                    s = s + "&" + key + "=" + value;
//                }
//                k++;
//            }

//            s = "index.xhtml" + s;
            s = "index.xhtml";

            System.out.println("s = " + s);

            out.println("s = " + s);
            // !! end;

            out.println("<html>" + "<head><title> Receipt </title>");
            System.out.println("<h3>POST PARAMETERS: "
                    + "token : " + request.getParameter("token") + ""
                    + "");

            String json = getJson(request.getParameter("token"));
            out.println("<br/> json is " + json);
            OpenIdUtils utils = new OpenIdUtils();
            out.println("<br/> extracted id is " + utils.extractIdFromJson(json));

            System.out.println("JSON = " + json);

            if (!SessionUtils.isLoggedIn(request)) {
                out.println("is not signed in. trying to make authorisation; json = " + json);
                try {
                    session = SessionUtils.resetSession(request);

                    openIdAuthorisation(json);
                    System.out.println("isSignedIn = " + SessionUtils.isLoggedIn(request));
                } catch (TagException ex) {
                    Logger.getLogger(LoginzaServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
//                if (SessionUtils.isSignedIn()) {
//                    out.println("openIdAuthorisation success... ");
//                }

                response.sendRedirect(s);
            } else {
//                makeBundle(utils.extractIdFromJson(json));
                response.sendRedirect(s);
            }



        } finally {
            out.close();
        }

    }

    private void openIdAuthorisation(String json) throws TagException {
        OpenIdUtils utils = new OpenIdUtils();
//        Map<String, String> map = utils.extractIdFromJson(json);
//        User user = userMan.openIdAuthorisation(utils.extractOpenId(json));

        User user = userMan.openIdAuthorization(utils.extractOpenId(json));
        System.out.println("openId auth: user = " + user);
        System.out.println("openIdAuthorisation: session = " + session);
//       session = (HttpSession) facesContext.getExternalContext().getSession(false);


        SessionListener.setSessionAttribute(session, SessionUtils.USER_ID_SESSION_ATTR, user.getId()); // working with session in servlet...
//        sm.addSession(session.getId(), (user == null) ? null : user.getId());
    }

    public String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception exc) {
        }

        return "";
    }

    public String getJson(String token) {
        System.out.println("getJSON ocuured");
//        if (confMan == null) {
//            System.out.println("confMan is null!!!!");
//        }
//        String ID = confMan.getString("openIdWidgetId");
        String ID = "62716";
//        String secret = confMan.getString("openIdSecretKey");
        String secret = "4e9eb19c1187c3073b925da668ac3c67";

        String sig = getMD5(token + secret);
        String link = "http://loginza.ru/api/authinfo?token=" + token + "&id=" + ID + "&sig=" + sig;

        System.out.println("getJSON - MD5 succesfull");
        System.out.println("link = " + link);

        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            String res = "";
            String inputLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                res += inputLine;
            }

            System.out.println("res - " + res);
            return res;
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
