<%@page import="org.mifos.irc.LogsCollector"%>
<%
	String date = request.getParameter("date");
	if (date != null && !date.equals("")) {
		response.setContentType("text/plain");
		out.print(LogsCollector.getLog(date));
	} else {
		out.print("No date arg");
	}
%>