package ru.javaops.masterjava;

import com.google.common.io.Resources;
import jdk.internal.org.xml.sax.SAXException;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 */
public class Main {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);
    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) throws IOException, JAXBException, org.xml.sax.SAXException {
       // System.out.format("Hello MasterJava!");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        String str;


        while (!(str=reader.readLine()).equals("exit"))
        {
            boolean found = false;
            List<Project> list = payload.getProjects().getProject();
            for (Project project:list)
            {
                if (project.getName().equals(str))
                {
                    found=true;
                    List<User> users = payload.getUsers().getUser();
                    List<Project.Groups> groups = project.getGroups();
                    SortedSet<String> projectUsers = new TreeSet<>();
                    for (Project.Groups group:groups)
                    {for (User u:users)
                        {
                            List<Object> userGroups = u.getGroups();
                            for (Object uGroup:userGroups) {
                                Project.Groups ugr = (Project.Groups)uGroup;
                                if (ugr.getName().equals(group.getName()))
                                {
                                    projectUsers.add(u.getFullName());
                                }
                            }
                        }}
                    System.out.println(projectUsers);
                }
            }
            if (!found)System.out.println("No such project");
        }
    }
}
