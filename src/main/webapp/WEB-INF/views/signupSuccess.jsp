<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page session="false" %>
<html>
    <head>
        <title>YOTTR :: User Saved Successfully - Welcome!</title>
    </head>
    <body>
        <h3>
            Hello ${user.firstName}, welcome to Yottr.
        </h3>

        <br>
        <div>Your username is ${user.username} which you will need to log in.</div>
        <br>
        <div>Happy sailing!</div>
        <hr>
        <br>

        <a href='login.htm'>Log in</a>
    </body>
</html>
