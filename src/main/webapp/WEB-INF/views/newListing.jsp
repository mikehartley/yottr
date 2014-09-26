<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>YOTTR :: Save boat details</title>
        <style>
        .error {
            color: #ff0000;
            font-style: italic;
            font-weight: bold;
        }
        </style>
    </head>
    <body>

        <h2>Enter boat details:</h2>

        <form:form method="POST" commandName="boat" action="new">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <table>
                <tr>
                    <td>Manufacturer:</td>
                    <td><form:input path="manufacturer" /></td>
                    <td><form:errors path="manufacturer" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Model:</td>
                    <td><form:input path="model" /></td>
                    <td><form:errors path="model" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Length:</td>
                    <td><form:input path="length" /> <form:radiobutton path="unitsImperial" value="true"/>Feet <form:radiobutton path="unitsImperial" value="false"/>Meters</td>
                    <td><form:errors path="length" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Hull type:</td>
                    <td><form:select path="hullType">
                            <form:option value="MONO" label="Monohull" />
                            <form:option value="MULTI" label="Multihull" />
                        </form:select></td>
                    <td><form:errors path="hullType" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Describe what you are looking for:</td>
                    <td><form:input path="description"/></td>
                    <td><form:errors path="description" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Sailing style:</td>
                    <td><form:select path="sailingStyle">
                            <form:option value="" label="Select sailing style:" />
                            <form:option value="CRUISING" label="Cruising" />
                            <form:option value="RACING" label="Racing" />
                            <form:option value="ALL" label="Both cruising and racing" />
                        </form:select></td>
                    <td><form:errors path="sailingStyle" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Date which this advert is relevant to (optional):</td>
                    <td><form:input path="dateRelevantTo"/></td> 
                    <td><form:errors path="dateRelevantTo" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>Minimum required RYA sail cruising qualification (or equivalent):</td>
                    <td><form:select path="minimumRequiredLevelByRank">
                            <form:option value="0"   label="None" />
                            <form:option value="100" label="Start Yachting" />
                            <form:option value="200" label="Competent Crew" />
                            <form:option value="300" label="Day Skipper" />
                            <form:option value="400" label="Coastal Skipper" />
                            <form:option value="500" label="Yachtmaster Coastal" />
                            <form:option value="600" label="Yachtmaster Offshore" />
                            <form:option value="700" label="Yachtmaster Ocean" />
                        </form:select></td>
                    <td><form:errors path="minimumRequiredLevelByRank" cssClass="error" /></td>
                </tr>
                <tr>
                    <td colspan="3"><input type="submit" value="Save boat details"></td>
                </tr>
            </table>

        </form:form>

        <br>
        <a href='/yottr/'>Home</a><br>
        &copy;YOTTR 2014
    </body>
</html>