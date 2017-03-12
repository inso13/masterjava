package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.GroupType;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Котик on 12.03.2017.
 */
public class StaxApp {
    private static boolean nameFlag;
    private static boolean descFlag;
    private static boolean fullNameFlag;

    public static void main(String[] args) throws IOException, XMLStreamException {
        BufferedReader strReader = new BufferedReader(new InputStreamReader(System.in));
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();

            List<Project> projectList = new ArrayList<>();
            List<User> userList = new ArrayList<>();
            Project project = null;
            User user=null;
            int event = reader.getEventType();
            // обходим весь XML файл
            while (true) {
                // проходим по типам событий
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        // в зависимости от имени тега отмечаем нужный фалг
                        if (reader.getLocalName().equals("Project")) {
                            project = new Project();
                            project.setGroups();
                        } else if (reader.getLocalName().equals("name")) {
                            nameFlag = true;
                        } else if (reader.getLocalName().equals("description")) {
                            descFlag = true;
                        } else if (reader.getLocalName().equals("groups")) {
                            Project.Groups group = new Project.Groups();
                            group.setGroupType(GroupType.fromValue(reader.getAttributeValue(0)));
                            group.setName(reader.getAttributeValue(1));
                            group.setId(reader.getAttributeValue(2));
                            project.addGroup(group);
                        }
                        else if (reader.getLocalName().equals("User")) {
                            user = new User();
                            user.setGroups();
                            user.setFlag(FlagType.fromValue(reader.getAttributeValue(0)));
                            String groups = reader.getAttributeValue(3);
                            String[] gr = groups.split(" ");
                            for (String s:gr)
                            {
                                user.addGroup(s);
                            }
                        }
                        else if (reader.getLocalName().equals("fullName")) {
                            fullNameFlag = true;
                        }
                        break;
                    // сохраняем данные XML-элемента,
                    // флаг которого равен true в объект Student
                    case XMLStreamConstants.CHARACTERS:
                        if (nameFlag) {
                            project.setName(reader.getText());
                            nameFlag = false;
                        } else if (descFlag) {
                            project.setDescription(reader.getText());
                            descFlag = false;
                        } else if (fullNameFlag) {
                            user.setFullName(reader.getText());
                            fullNameFlag=false;
                        }
                        break;
                    // если цикл дошел до закрывающего элемента узла Student, то сохраняем объект в список
                    case XMLStreamConstants.END_ELEMENT:
                        if (reader.getLocalName().equals("Project")) {
                            projectList.add(project);
                        }
                        else if (reader.getLocalName().equals("User"))
                        {
                            userList.add(user);
                        }
                        break;
                }
                // если больше элементов нет, то заканчиваем обход файла
                if (!reader.hasNext())
                    break;

                // переход к следующему событию
                event = reader.next();
            }

        String str;
            while (!(str=strReader.readLine()).equals("exit"))
            {
                boolean found = false;
                for (Project p:projectList)
                {
                    if (p.getName().equals(str))
                    {
                        found=true;
                        List<Project.Groups> groups = p.getGroups();
                        SortedSet<String> projectUsers = new TreeSet<>();
                        for (Project.Groups group:groups)
                        {for (User u:userList)
                        {
                            List<Object> userGroups = u.getGroups();
                            for (Object uGroup:userGroups) {
                                if (uGroup.toString().equals(group.getName()))
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
}
