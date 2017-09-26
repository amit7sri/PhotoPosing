/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package photoposing.com.amko0l.backend;

import java.io.IOException;
import java.net.URLDecoder;


import javax.servlet.http.*;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if (name == null) {
            resp.getWriter().println("Error:Image URL not found");
        }

        CallVisionServer visionServer = new CallVisionServer();

        //Amit
        String respondlandmark = visionServer.detectLandMarks(name);
        String responseface = visionServer.detectFaces(name);
        //String responseface = visionServer.detectFaces(URLDecoder.decode(name));
        String responsesimilar = visionServer.detectSimilar(name);
        //String responsesimilar = visionServer.detectSimilar(URLDecoder.decode(name));

        resp.getWriter().println(respondlandmark);
        resp.getWriter().println(responseface);
        resp.getWriter().println(responsesimilar);
    }
}
