<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page session="false" %>
<html>
    <head>
        <title>YOTTR :: Boats boats boats!</title>
    </head>
    <body>
        <h3>
            All Boats
        </h3>

        <c:forEach var="boat" items="${boats}">
            <strong>Reference: </strong>${boat.reference}<br>
            <strong>Manufacturer: </strong>${boat.manufacturer}<br>
            <strong>Model: </strong>${boat.model}<br>
            <strong>Length: </strong>${boat.length}<br>
            <strong>Hull type: </strong>${boat.hullType}<br>
            <strong>Description: </strong>${boat.description}<br>
            <hr>
        </c:forEach>

        <a href='/yottr/j_spring_security_logout'>Logout</a><br><br>

        <a href='/yottr/'>Home</a><br>
        &copy;YOTTR 2014
    </body>
</html>
