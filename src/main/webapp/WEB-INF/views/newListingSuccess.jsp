<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page session="false" %>
<html>
    <head>
        <title>YOTTR :: Boat Saved Successfully</title>
    </head>
    <body>
        <h3>
            Boat Saved Successfully.
        </h3>

        <strong>Date posted: </strong>${boat.datePosted}<br>
        <strong>Date relevant to: </strong>${boat.dateRelevantTo}<br>
        <strong>Reference: </strong>${boat.reference}<br>
        <strong>Manufacturer: </strong>${boat.manufacturer}<br>
        <strong>Model: </strong>${boat.model}<br>
        <strong>Length: </strong>${boat.length}
        <c:choose>
            <c:when test="${boat.unitsImperial}">Feet</c:when>
            <c:otherwise>Metres</c:otherwise>
        </c:choose><br>
        <strong>Hull type: </strong>${boat.hullType}<br>
        <strong>Minimum required RYA sail cruising qualification: </strong>${boat.minimumRequiredLevel.displayName}<br>
        <strong>Sailing style: </strong>${boat.sailingStyle.displayName}<br>

        <hr>

        <strong>Description</strong><br>
        ${boat.description}<br>
        <hr>
        <br>

        <a href='all'>All boats</a>

        <br>
        <a href='/yottr/'>Home</a><br>
        &copy;YOTTR 2014
    </body>
</html>
