<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springForm"%>
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

    <h1>Sign-up</h1>
    <h2>Please enter your details:</h2>

	<springForm:form method="POST" commandName="user" action="signup.htm">

		<table>
			<tr>
				<td>Username:</td>
				<td><springForm:input path="username" /></td>
				<td><springForm:errors path="username" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><springForm:input path="password" /></td>
				<td><springForm:errors path="password" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Title:</td>
				<td><springForm:input path="title" /></td>
				<td><springForm:errors path="title" cssClass="error" /></td>
			</tr>
			<tr>
				<td>First name:</td>
				<td><springForm:input path="firstName" /></td>
				<td><springForm:errors path="firstName" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Last name (Family name):</td>
				<td><springForm:input path="lastName" /></td>
				<td><springForm:errors path="lastName" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Email:</td>
				<td><springForm:input path="email" /></td>
				<td><springForm:errors path="email" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Mobile:</td>
				<td><springForm:input path="mobile" /></td>
				<td><springForm:errors path="mobile" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Your Location:</td>
				<td><springForm:select path="country">
						<springForm:option value="" label="Please choose:" />
						<springForm:option value="UK" label="UK" />
						<springForm:option value="OTHER" label="Other" />
					</springForm:select></td>
				<td><springForm:errors path="country" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Postcode:</td>
				<td><springForm:input path="postcode"/></td>
				<td><springForm:errors path="postcode" cssClass="error" /></td>
			</tr>
			<tr>
				<td>A bit about yourself:</td>
				<td><springForm:input path="aboutMe"/></td>
				<td><springForm:errors path="aboutMe" cssClass="error" /></td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" value="Save your details"></td>
			</tr>
		</table>

	</springForm:form>

</body>
</html>