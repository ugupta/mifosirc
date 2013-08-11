<%@page import="java.util.List"%>
<%@page import="org.mifos.irc.LogsCollector"%>
<%@page import="org.joda.time.LocalDate"%>
<%
	response.setContentType("application/json");
	String date = request.getParameter("date");
	LocalDate localDate = new LocalDate();
	if (date != null && !date.equals("")) {
		localDate = new LocalDate(date);
	}
	String json = LogsCollector.getFilteredLogsJSON(localDate.toString());
	out.print(json);
%>
