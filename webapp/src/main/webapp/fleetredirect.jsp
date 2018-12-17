
<%
	String redirectURL = "/fleet/authenticateUser?user="
			+ session.getAttribute("userId") + "&slmsCode="
			+ session.getAttribute("slmsCode");
	System.out.println("SessionUserId:" + session.getAttribute("userId") + " "
			+ "SessionSlmsCode:" + session.getAttribute("slmsCode"));
	response.sendRedirect(redirectURL);
%>
