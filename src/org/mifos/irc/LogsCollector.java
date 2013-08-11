package org.mifos.irc;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.mifos.irc.db.ConfigManager;
import org.mifos.irc.db.EMF;
import org.mifos.irc.db.PlainLog;

public class LogsCollector {

	private static final String NO_LOGS_EXIST_FOR = "No logs exist for ";
	public static final String NONE = "NONE";
	private static final Logger log = Logger.getLogger(LogsCollector.class
			.getName());

	public String getNextLogText() throws Exception {
		String sDate = ConfigManager.getNextLogDate();
		String sYear = sDate.substring(0, 4);
		String urlString = "http://ci.mifos.org/irclogs/%23mifos/archive/"
				+ sYear + "/" + sDate + ".log.gz";
		log.log(Level.WARNING, urlString);
		URL url = new URL(urlString);
		HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET);

		URLFetchService service = URLFetchServiceFactory.getURLFetchService();
		HTTPResponse response = service.fetch(request);

		if (response.getResponseCode() != 200) {
			return "NO DATA EXISTS for " + sDate + " with code "
					+ response.getResponseCode();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(new String(response.getContent()));
		return sb.toString();
	}

	public static String nextLog() throws Exception {
		String date = ConfigManager.getNextLogDate();
		if (isBeforeToday(date)) {
			String log = new LogsCollector().getNextLogText();
			if (log.startsWith("NO DATA EXISTS")) {
				ConfigManager.increaseNextLogDate();
				return ConfigManager.getNextLogDate();
			}
			return saveLog(log, date);
		}
		return "NONE";
	}

	public static String saveLog(String log, String date) throws Exception {
		EntityManager em = EMF.get().createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		em.persist(new PlainLog(date, new Text(log)));
		em.close();
		ConfigManager.increaseNextLogDate();
		return ConfigManager.getNextLogDate();
	}

	public static String getLog(Date date) {
		return getLog(date);
	}

	public static String getLog(String date) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select pl from " + PlainLog.class.getName()
				+ " pl where pl.key = '" + date + "'");
		String log = "";
		try {
			PlainLog pl = (PlainLog) q.getSingleResult();
			log = pl.getLog().getValue();
		} catch (NoResultException e) {
			log = NO_LOGS_EXIST_FOR + date;
		}
		return log;
	}

	public static String getFilteredLogsJSON(String date) {
		List<String> logs = getFilteredLog(date);
		StringBuffer sb = new StringBuffer();
		sb.append("{\n\"logs\" : [\"");
		for (String log : logs) {
			sb.append(log).append("\", \"");
		}
		sb.append("last\"],\n");
		LocalDate localDate = new LocalDate(date);
		sb.append("\"nextDate\" : \"").append(localDate.plusDays(1))
				.append("\",\n");
		sb.append("\"prevDate\" : \"").append(localDate.minusDays(1))
				.append("\",\n");
		sb.append("\"currDate\" : \"").append(localDate).append("\"\n");
		sb.append("}");
		return sb.toString();
	}

	public static List<String> getFilteredLog(String date) {
		String log = getLog(date);

		return filter(log);
	}

	private static List<String> filter(String log) {
		StringTokenizer st = new StringTokenizer(log, "\n");
		List<String> filteredLog = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String filter = applyFilter(st.nextToken());
			filter = filter.replace("\n", "").replace("\t", "    ").trim();
			if (!(filter.isEmpty())) {
				filteredLog.add(StringEscapeUtils.escapeHtml(filter));
			}
		}
		return filteredLog;
	}

	private static String applyFilter(String token) {
		if ((token.contains("has joined")) || (token.contains("has quit"))
				|| (token.contains("has left"))
				|| (token.contains("title: IRC log from"))
				|| (token.contains("is now known a"))
				|| (token.contains("ChanServ sets mode"))) {
			return "";
		}
		return token + "\n";
	}

	public static List<String> getLogDates() {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select pl.key from "
				+ PlainLog.class.getName() + " pl order by pl.key");
		return q.getResultList();
	}

	private static boolean isBeforeToday(String date) {
		return new LocalDate(date).isBefore(new LocalDate());
	}
}