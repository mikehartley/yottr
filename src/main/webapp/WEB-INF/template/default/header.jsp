<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<%--
  ~ Copyright (c) 2014. Mike Hartley Solutions Ltd
  ~ All rights reserved.
  --%>

<header class="navbar navbar-inverse navbar-fixed-top wet-asphalt" role="banner">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value='/'/>"><h1>Yottr</h1></a>
        </div>
        <sec:authorize access="hasRole('ADMIN')">
            <div>
                <ul>
                    <li>[ADMIN info] Built: <spring:message code="build.date"/> : Logged in as <sec:authentication property="principal.username" /></li>
                </ul>
            </div>
        </sec:authorize>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <sec:authorize access="hasRole('ADMIN')">
                    <li><a href="<c:url value='/admin/users'/>">Admin</a></li>
                </sec:authorize>

                <li class="active"><a href="<c:url value='/'/>">Home</a></li>

                <sec:authorize access="isAnonymous()">
                    <li><a href="<c:url value='/signup'/>">Register</a></li>
                </sec:authorize>

                <sec:authorize access="hasRole('FREE')">
                    <li><a href="<c:url value='/s/listings/new'/>">Add Boat</a></li>
                    <li><a href="<c:url value='/s/listings/all'/>">View All</a></li>
                </sec:authorize>

                <li><a href="<c:url value='/'/>">About</a></li>
                <li><a href="<c:url value='/#'/>">Contact</a></li>

                <sec:authorize access="isAuthenticated()">
                    <li><a href="<c:url value='/logout'/>" id="logout-link">Logout</a></li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</header>
<!--/header-->