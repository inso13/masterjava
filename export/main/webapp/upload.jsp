
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
File uploading
<body>
<form method="POST" enctype="multipart/form-data" action="upload">
    File to upload: <input type="file" name="upfile"><br/>
    Notes about the file: <input type="text" name="note"><br/>
    <br/>
    <input type="submit" value="Press"> to upload the file!
</form>
</body>
</html>
