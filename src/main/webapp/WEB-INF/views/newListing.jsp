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

    <h2>Enter boat details:</h2>

	<springForm:form method="POST" commandName="boat" action="new.htm">
		<table>
			<tr>
				<td>Reference:</td>
				<td><springForm:input path="reference" /></td>
				<td><springForm:errors path="reference" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Manufacturer:</td>
				<td><springForm:input path="manufacturer" /></td>
				<td><springForm:errors path="manufacturer" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Model:</td>
				<td><springForm:input path="model" /></td>
				<td><springForm:errors path="model" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Length:</td>
				<td><springForm:input path="length" /></td>
				<td><springForm:errors path="length" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Hull type:</td>
				<td><springForm:select path="hullType">
						<springForm:option value="" label="Select hull type:" />
						<springForm:option value="MONO" label="Monohull" />
						<springForm:option value="MULTI" label="Multihull" />
					</springForm:select></td>
				<td><springForm:errors path="hullType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Describe what you are looking for:</td>
				<td><springForm:input path="desc"/></td>
				<td><springForm:errors path="desc" cssClass="error" /></td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" value="Save boat details"></td>
			</tr>
		</table>

	</springForm:form>

</body>
</html>