<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springForm"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>YOTTR :: Admin</title>
        <style>
        .error {
            color: #ff0000;
            font-style: italic;
            font-weight: bold;
        }
        </style>
    </head>
    <body>

        <h2>YOTTR - Admin</h2>
        <hr>
        <c:forEach var="user" items="${users}">
            <table>
                <tr><td>Username</td><td>${user.username}</td></tr>
                <tr><td>Password</td><td>${user.password}</td></tr>
                <tr><td>Email</td><td>${user.email}</td></tr>
                <tr><td>First Name</td><td>${user.firstName}</td></tr>
                <tr><td>Last Name</td><td>${user.lastName}</td></tr>
                <tr><td>About</td><td>${user.aboutMe}</td></tr>
                <tr><td>Authorities</td><td>${user.authorities}</td></tr>
                <tr><td>Account enabled</td><td>${user.enabled}</td></tr>
                <tr><td>Account non-locked</td><td>${user.accountNonLocked}</td></tr>
                <tr><td>Boat adverts</td><td>TODO (max 3) <click to see></td></tr>
            </table>
            EDIT | <a href='/yottr/admin/user/${user.id}/enabled/flip.htm'>FLIP ENABLED</a> | <a href='/yottr/admin/user/${user.id}/delete.htm'>DELETE</a><br>
            <hr>
        </c:forEach>
        <hr>

        <a href='/yottr/j_spring_security_logout'>Logout</a><br><br>

        <a href='/yottr/'>Home</a><br>
        &copy;YOTTR 2014
    </body>
</html>