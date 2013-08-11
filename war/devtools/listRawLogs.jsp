<%@page import="java.util.List"%>
<%@page import="org.mifos.irc.LogsCollector"%>
<%
	List<String> list = LogsCollector.getLogDates();
	if (list.isEmpty()) {
		out.println("No logs");
		return;
	}

	for (String date : LogsCollector.getLogDates()) {
		out.println("<a href=viewRawLog.jsp?date=" + date + ">" + date
				+ "</a><br/><br/>");
	}
%>