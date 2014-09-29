<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  ~ Copyright (c) 2014. Mike Hartley Solutions Ltd
  ~ All rights reserved.
  --%>

<!DOCTYPE html>
<spring:url value="" var="yottr"/>
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
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value='/resources/images/ico/apple-touch-icon-144-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value='/resources/images/ico/apple-touch-icon-114-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value='/resources/images/ico/apple-touch-icon-72-precomposed.png'/>">
    <link rel="apple-touch-icon-precomposed" href="<c:url value='/resources/images/ico/apple-touch-icon-57-precomposed.png'/>">
</head><!--/head-->
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
                <a class="navbar-brand" href="index.html"><h1>Yottr</h1></a>
            </div>
            <div class="collapse navbar-collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="${yottr}/admin/users">Admin</a></li>
                    <li class="active"><a href="${yottr}">Home</a></li>
                    <li><a href="${yottr}/signup">Register</a></li>
                    <li><a href="${yottr}/s/listings/new">Add Boat</a></li>
                    <li><a href="${yottr}/s/listings/all">View All</a></li>
                    <li><a href="${yottr}/#">About</a></li>
                    <li><a href="${yottr}/#">Contact</a></li>
                </ul>
            </div>
        </div>
    </header><!--/header-->

    <%--<section id="title" class="emerald">--%>
        <%--<div class="container">--%>
            <%--<div class="row">--%>
                <%--<div class="col-sm-6">--%>
                    <%--<h1>About Us</h1>--%>
                    <%--<p>Pellentesque habitant morbi tristique senectus et netus et malesuada</p>--%>
                <%--</div>--%>
                <%--<div class="col-sm-6">--%>
                    <%--<ul class="breadcrumb pull-right">--%>
                        <%--<li><a href="index.html">Home</a></li>--%>
                        <%--<li class="active">About Us</li>--%>
                    <%--</ul>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</section><!--/#title-->--%>

    <section id="about-us" class="container">
        <div class="row">
            <div class="col-sm-6">
                <h2>Welcome</h2>
                <p>Welcome to Yottr, a new and exciting web application that can help you find crew for your boat,
		or find you a boat to go sailing on. </p>
		<p>If you are a boat owner looking for crew then you can register and post an advert for free. This will
		enable potential crew who are looking for your kind of sailing to easily find you.</p>
		<p>If you don't have a boat, would like to sail but would like to avoid paying the kind of money it costs
		to go with a commercial outfit then this is your site. Right now it's completely free to register, search, 
		and get in contact with people looking for crew!</p>
            </div><!--/.col-sm-6-->
            <div class="col-sm-6">
                <h2>Search</h2>
                <p>Search boxes go here</p>
            </div><!--/.col-sm-6-->
        </div><!--/.row-->

        <div class="gap"></div>
        <h1 class="center">Sails up, motor off, have fun!</h1>
        <div class="gap"></div>
    </section><!--/#about-us-->

    <section id="bottom" class="wet-asphalt">
        <div class="container">
            <div class="row">
                <div class="col-md-3 col-sm-6">
                    <h4>About Us</h4>
                    <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante.</p>
                    <p>Pellentesque habitant morbi tristique senectus.</p>
                </div><!--/.col-md-3-->

                <div class="col-md-3 col-sm-6">
                    <h4>Really useful stuff</h4>
                    <div>
                        <ul class="arrow">
                            <li><a href="#">The Company</a></li>
                            <li><a href="#">Our Team</a></li>
                            <li><a href="#">Our Partners</a></li>
                            <li><a href="#">Our Services</a></li>
                            <li><a href="#">Faq</a></li>
                            <li><a href="#">Conatct Us</a></li>
                            <li><a href="#">Privacy Policy</a></li>
                            <li><a href="#">Terms of Use</a></li>
                            <li><a href="#">Copyright</a></li>
                        </ul>
                    </div>
                </div><!--/.col-md-3-->

                <div class="col-md-3 col-sm-6">
                    <h4>Latest Blog</h4>
                    <div>
                        <div class="media">
                            <div class="pull-left">
                                <img src="images/blog/thumb1.jpg" alt="">
                            </div>
                            <div class="media-body">
                                <span class="media-heading"><a href="#">Pellentesque habitant morbi tristique senectus</a></span>
	                                <small class="muted">Posted 17 Aug 2013</small>
                            </div>
                        </div>
                        <div class="media">
                            <div class="pull-left">
                                <img src="images/blog/thumb2.jpg" alt="">
                            </div>
                            <div class="media-body">
                                <span class="media-heading"><a href="#">Pellentesque habitant morbi tristique senectus</a></span>
                                <small class="muted">Posted 13 Sep 2013</small>
                            </div>
                        </div>
                        <div class="media">
                            <div class="pull-left">
                                <img src="images/blog/thumb3.jpg" alt="">
                            </div>
                            <div class="media-body">
                                <span class="media-heading"><a href="#">Pellentesque habitant morbi tristique senectus</a></span>
                                <small class="muted">Posted 11 Jul 2013</small>
                            </div>
                        </div>
                    </div>  
                </div><!--/.col-md-3-->

                <div class="col-md-3 col-sm-6">
                    <h4>Address</h4>
                    <address>
                        <strong>Twitter, Inc.</strong><br>
                        795 Folsom Ave, Suite 600<br>
                        San Francisco, CA 94107<br>
                        <abbr title="Phone">P:</abbr> (123) 456-7890
                    </address>
                </div> <!--/.col-md-3-->
            </div>
        </div>
    </section><!--/#bottom-->

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
                        <li><a id="gototop" class="gototop" href="#"><i class="icon-chevron-up"></i></a></li><!--#gototop-->
                    </ul>
                </div>
            </div>
        </div>
    </footer><!--/#footer-->

    <script src="<c:url value='/resources/js/jquery.js'/>"></script>
    <script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
    <script src="<c:url value='/resources/js/jquery.prettyPhoto.js'/>"></script>
    <script src="<c:url value='/resources/js/main.js'/>"></script>
</body>
</html>
