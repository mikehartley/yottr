<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="springForm" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--
  ~ Copyright (c) 2014. Mike Hartley Solutions Ltd
  ~ All rights reserved.
  --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="For boats looking for crew, and crew looking for boats.">
    <title>Yottr</title>
    <link href="<c:url value='/resources/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/resources/css/font-awesome.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/resources/css/prettyPhoto.css'/>" rel="stylesheet">
    <link href="<c:url value='/resources/css/animate.css'/>" rel="stylesheet">
    <link href="<c:url value='/resources/css/main.css'/>" rel="stylesheet">
    <!--[if lt IE 9]>
    <script src="<c:url value='/resources/js/html5shiv.js'/>"></script>
    <script src="<c:url value='/resources/js/respond.min.js'/>"></script>
    <![endif]-->
    <link rel="shortcut icon" href="<c:url value='/resources/images/ico/favicon.ico'/>>">
    <link rel="apple-touch-icon-precomposed" sizes="144x144"
          href="<c:url value='/resources/images/ico/apple-touch-icon-144-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed" sizes="114x114"
          href="<c:url value='/resources/images/ico/apple-touch-icon-114-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed" sizes="72x72"
          href="<c:url value='/resources/images/ico/apple-touch-icon-72-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed"
          href="<c:url value='/resources/images/ico/apple-touch-icon-57-precomposed.png'/>">
</head>
<!--/head-->
<body>
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
                    <li><a href="<c:url value='/logout'/>">Logout</a></li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</header>
<!--/header-->

<section id="about-us" class="container">
    <div class="row">
        <div class="col-sm-6">
            <h2>Welcome</h2>

            <p>Welcome to Yottr, a new and exciting web application that can help you find crew for your boat,
                or find you a boat to go sailing on. </p>

            <p>If you are a boat owner looking for crew then you can register and post an advert for free. This will
                enable potential crew who are looking for your kind of sailing to easily find you.</p>

            <p>If you don't have a boat, would like to sail but would like to avoid paying the kind of money it costs
                to go with a commercial outfit then this is your site. Right now it's completely free to register,
                search,
                and get in contact with people looking for crew!</p>
        </div>
        <!--/.col-sm-6-->
        <div class="col-sm-6">
            <sec:authorize access="isAnonymous()">
                <h2>Sign In</h2>

                <c:if test="${param.error != null}">
                    <div class="status alert alert-danger" id="error-msg">
                        Invalid username and password.
                    </div>
                </c:if>
                <c:if test="${param.logout != null}">
                    <div class="status alert alert-success" id="logged-out-msg">
                        You have been logged out.
                    </div>
                </c:if>
                <c:url var="loginUrl" value="/index"></c:url>
                <form action="${loginUrl}" id="main-sign-in-form" name="f" method="POST">
                    <sec:csrfInput />
                    <div class="row">
                        <div class="col-sm-5">
                            <div class="form-group">
                                <input type="text" name="username" id="username" class="form-control" required="required" placeholder="Username">
                            </div>
                            <div class="form-group">
                                <input type="password" name="password" id="password" class="form-control" required="required" placeholder="Password">
                            </div>
                            <div class="form-group">
                                <input type="submit" name="submit" id="sign-in-button" class="btn btn-primary btn-sm" value="Go"/>
                            </div>
                        </div>
                    </div>
                </form>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <h2>Search</h2>

                <p>This is where you'll be able to seach from.</p>
            </sec:authorize>
        </div>
        <!--/.col-sm-6-->
    </div>
    <!--/.row-->

    <div class="gap"></div>
    <%--<h1 class="center">Sails up, motor off, have fun!</h1>--%>

    <%--<div class="gap"></div>--%>
</section>
<!--/#about-us-->

<section id="bottom" class="wet-asphalt">
    <div class="container">
        <div class="row">
            <div class="col-md-3 col-sm-6">
                <h4>About</h4>

                <p>Yottr has been a long time in the making.</p>

                <p>Many years ago I thought it would be a great idea if there was a website for people who want
                    to go sailing but either don't have enough crew, or don't have a boat.</p>

                <p>I couldn't find one I liked so I decided to build my own.</p>

                <p>This is it...</p>
            </div>
            <!--/.col-md-3-->

            <div class="col-md-3 col-sm-6">
                <h4>Useful Links</h4>

                <div>
                    <ul class="arrow">
                        <li><a href="#">Weather</a></li>
                        <li><a href="#">Tides</a></li>
                        <li><a href="#">The Good Marina Guide</a></li>
                        <li><a href="#">Chandlers</a></li>
                        <li><a href="#">Schools and Courses</a></li>
                        <li><a href="#">Skippers for Hire</a></li>
                        <li><a href="#">Charter</a></li>
                        <li><a href="#">RYA</a></li>
                        <li><a href="#">RNLI Lifeboats</a></li>
                        <li><a href="#">ARC</a></li>
                        <li><a href="#">Ocean Youth Trust</a></li>
                    </ul>
                </div>
            </div>
            <!--/.col-md-3-->

            <div class="col-md-3 col-sm-6">
                <h4>Latest Listings</h4>

                <div>
                    <div class="media">
                        <div class="pull-left">
                            <img src="/resources/images/blog/thumb1.jpg" alt="">
                        </div>
                        <div class="media-body">
                            <span class="media-heading"><a href="#">Weekend sailing around the Solent</a></span>
                            <small class="muted">Posted 17 Aug 2013</small>
                        </div>
                    </div>
                    <div class="media">
                        <div class="pull-left">
                            <img src="/resources/images/blog/thumb2.jpg" alt="">
                        </div>
                        <div class="media-body">
                            <span class="media-heading"><a href="#">Racing to Spain</a></span>
                            <small class="muted">Posted 13 Sep 2013</small>
                        </div>
                    </div>
                    <div class="media">
                        <div class="pull-left">
                            <img src="/resources/images/blog/thumb3.jpg" alt="">
                        </div>
                        <div class="media-body">
                            <span class="media-heading"><a href="#">Cross channel adventure</a></span>
                            <small class="muted">Posted 11 Jul 2013</small>
                        </div>
                    </div>
                </div>
            </div>
            <!--/.col-md-3-->

            <div class="col-md-3 col-sm-6">
                <h4>This space is could be yours</h4>
                <p>All this space, just waiting to be used...</p>
                <p>Perhaps you would like to advertise here?</p>
                <p>If so then get in touch.</p>
            </div>
            <!--/.col-md-3-->
        </div>
    </div>
</section>
<!--/#bottom-->

<footer id="footer" class="midnight-blue">
    <div class="container">
        <div class="row">
            <div class="col-sm-6">
                &copy; 2014 Yottr. All Rights Reserved.
            </div>
            <div class="col-sm-6">
                <ul class="pull-right">
                    <li><a href="#">Home</a></li>
                    <li><a href="#">About Us</a></li>
                    <li><a href="#">Faq</a></li>
                    <li><a href="#">Contact Us</a></li>
                    <li><a href="#">Terms &amp; Conditions</a></li>
                    <li><a href="#">Privacy Policy</a></li>
                    <li><a id="gototop" class="gototop" href="#"><i class="icon-chevron-up"></i></a></li>
                    <!--#gototop-->
                </ul>
            </div>
        </div>
    </div>
</footer>
<!--/#footer-->

<script src="<c:url value='/resources/js/jquery.js'/>"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
<script src="<c:url value='/resources/js/jquery.prettyPhoto.js'/>"></script>
<script src="<c:url value='/resources/js/main.js'/>"></script>
</body>
</html>
