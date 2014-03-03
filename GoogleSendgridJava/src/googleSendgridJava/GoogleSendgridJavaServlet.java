package googleSendgridJava;

// import Sendgrid class in order to send emails 
import googleSendgridJava.Sendgrid;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;

public class GoogleSendgridJavaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/html");

        UserService userService = UserServiceFactory.getUserService();
        // if the user is not logged in display login form
        if (!userService.isUserLoggedIn()) {
            // redirect to index after login 
            resp.sendRedirect(userService.createLoginURL("/"));
        } else {
            try {
                // initialize Sendgrid class
                // please replace "<sendgrid_username>" and "<sendgrid_password>" with your SendGrid credentials
                Sendgrid mail = new Sendgrid("<sendgrid_username>","<sendgrid_password>");
                // set to address, from address, subject, the html/text content and send the email 
                mail.setTo(req.getParameter("emailto"))
                    // update the <from_address> with your email address
                    .setFrom("<from_address>")
                    .setSubject(req.getParameter("subject"))
                    .setText(req.getParameter("content"))
                    .setHtml("")
                    .send();

                // check the response and display proper message
                if (mail.getServerResponse() == "success") {
                    req.setAttribute("message", "Your request was successfully processed.");
                    req.setAttribute("myclass", "success");
                } else {
                    req.setAttribute("message", "Request failed  - " + mail.getServerResponse());
                    req.setAttribute("myclass", "error");
                }
                req.getRequestDispatcher("success.jsp").forward(req, resp);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // redirect to index if the user is trying to access directly this page
        resp.sendRedirect("/");
    }
}
