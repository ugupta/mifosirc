package org.mifos.irc;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

public class LogsJsonServlet extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String date = req.getParameter("date");

        LocalDate localDate = StringUtils.isBlank(date) ? new LocalDate() : new LocalDate(date);

        String json = LogsCollector.getFilteredLogsJSON(localDate);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(json);
    }
}