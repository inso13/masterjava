package ru.javaops.masterjava.xml.upload;

/**
 * Created by Inso on 18.03.2017.
 */


import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;


public class UploadServlet extends HttpServlet {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("upload.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
// Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

// Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

// Parse the request
        try {
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem item:items)
            {
                try (InputStream is = item.getInputStream())
                     {
                    StaxStreamProcessor processor = new StaxStreamProcessor(is);

                    Set<User> users = new TreeSet<>(USER_COMPARATOR);

                    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                            User user = new User();
                            user.setEmail(processor.getAttribute("email"));
                            user.setFlag(FlagType.fromValue(processor.getAttribute("flag")));
                            user.setValue(processor.getText());
                            users.add(user);
                    }
                    for (User u:users)
                    { System.out.println(u);}
                    req.setAttribute("users", users);
                    req.getRequestDispatcher("userlist.jsp").forward(req, resp);
                    return;
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
