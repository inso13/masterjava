
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
<section>
    <h2>User list</h2>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Flag</th>
        </tr>
        </thead>

        <%--@elvariable id="users" type="java.util.List"--%>
        <c:forEach items="${users}" var="user">
            <jsp:useBean id="user" scope="page" type="ru.javaops.masterjava.xml.schema.User"/>
            <tr>
                <td>${user.value}</td>
                <td>${user.email}</td>
                <td>${user.flag}</td>
            </tr>
        </c:forEach>
    </table>
    <h2> </h2>
    <a href="upload">Press to go back to file select menu</a>
</section>
</body>
</html>
