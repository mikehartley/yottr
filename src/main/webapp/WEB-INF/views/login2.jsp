<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%--
  ~ Copyright (c) 2014. Mike Hartley Solutions Ltd
  ~ All rights reserved.
  --%>

<html>

    <head>
        <title>Login Page</title>
    </head>
    <body>
        <h3>Quick Login Page</h3>
        <c:url var="loginUrl" value="/login2"></c:url>
        <form action="${loginUrl}" method="POST" name="f">
            <table>
                <c:if test="${param.error != null}">
                    <div class="status alert alert-error">
                        Invalid username and password.
                    </div>
                </c:if>
                <c:if test="${param.logout != null}">
                    <div class="status alert alert-success">
                        You have been logged out.
                    </div>
                </c:if>
                <tr>
                    <td>User ID:</td>
                    <td><input type='text' name='username' /></td>
                </tr>
                <tr>
                    <td>Password:</td>
                    <td><input type='password' name='password' /></td>
                </tr>
                <tr>
                    <td colspan='2'><input name="submit" type="submit"
                        value="Login" /></td>
                </tr>
                <tr>
                    <td colspan='2'><h3><a href='signup'>Join for free</a></h3></td>
                </tr>
            </table>

            <br>
            &copy;YOTTR 2014
        </form>
    </body>
</html>