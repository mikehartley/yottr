<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:insertDefinition name="headerFooterTemplate">
    <tiles:putAttribute name="body">


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
                        <h2 id="search-title">Search</h2>

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


    </tiles:putAttribute>
</tiles:insertDefinition>
