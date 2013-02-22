<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>We Make Email Delivery Easy | SendGrid jsp</title>
    <link type="text/css" rel="stylesheet" href="main.css" />
<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
%>
        <meta http-equiv="refresh" content="0; url=<%= userService.createLoginURL(request.getRequestURI()) %>">
<% 
    }
%>
  </head>
  <body>
    <div class="header">
      <div class="header-top">
        <img src="sendgrid_logo.png" style="width: 150px;" />
      </div>
    </div>
    <div class="content">
      <div class="form">
        <form action="/googlesendgridjava" method="post">
          <div class="form-input"><label>To:</label> <input name="emailto"/></div>
          <div class="form-input"><label>Subject: </label><input name="subject"/></div>
          <div class="form-input"><label>Content: </label><textarea name="content" rows="10" cols="60"></textarea></div>
          <div><input type="submit" value="Send" class="buttton" /></div>
        </form>
      </div>
    </div>
  </body>
</html>