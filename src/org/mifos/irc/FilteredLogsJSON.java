package org.mifos.irc;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.LocalDate;

public class FilteredLogsJSON extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String date = req.getParameter("date");
		LocalDate localDate = new LocalDate();
		if ((date != null) && (!(date.equals("")))) {
			localDate = new LocalDate(date);
		}
		String json = LogsCollector.getFilteredLogsJSON(localDate.toString());

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().print(json);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}
}