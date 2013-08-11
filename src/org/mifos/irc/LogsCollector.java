package org.mifos.irc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.mifos.irc.db.ConfigManager;
import org.mifos.irc.db.EMF;
import org.mifos.irc.db.PlainLog;

import com.google.appengine.api.datastore.Text;

public class LogsCollector
{

    private static final String NO_LOGS_EXIST_FOR = "No logs exist for ";
    public static final String NONE = "NONE";
    private static final Logger log = Logger.getLogger(LogsCollector.class.getName());
    
    public String getNextLogText() throws Exception {
        return getNextLog(ConfigManager.getNextLogDate());
    }

    private String getNextLog(String sDate) throws Exception
    {
        String sYear = sDate.substring(0, 4);
        String urlString = "http://ci.mifos.org/irclogs/%23mifos/archive/" + sYear + "/" + sDate + ".log.gz";
        log.log(Level.WARNING, urlString);
        URL url = new URL(urlString);

        HttpURLConnection c = (HttpURLConnection) url.openConnection();

        c.setConnectTimeout(5000);
        c.setReadTimeout(10000);
 
        if (c.getResponseCode() != 200)
        {
            return NO_LOGS_EXIST_FOR + sDate + " with code " + c.getResponseCode();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(c.getInputStream()), "UTF-8"));
        
        String inputLine;
        StringBuffer text = new StringBuffer();
     
        while ((inputLine = br.readLine()) != null) {
            text.append(inputLine).append("\n");
        }
        br.close();

        return text.toString();
    }

    public static String nextLog() throws Exception
    {
        String date = ConfigManager.getNextLogDate();
        if (isBeforeToday(date))
        {
            String log = new LogsCollector().getNextLogText();
            if (log.startsWith(NO_LOGS_EXIST_FOR))
            {
                ConfigManager.increaseNextLogDate();
                return ConfigManager.getNextLogDate();
            }
            return saveLog(log, date);
        }
        return "NONE";
    }

    public static String saveLog(String log, String date) throws Exception
    {
        EntityManager em = EMF.get().createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        em.persist(new PlainLog(date, new Text(log)));
        em.close();
        ConfigManager.increaseNextLogDate();
        return ConfigManager.getNextLogDate();
    }

    public static String getLog(String date)
    {
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createQuery("select pl from " + PlainLog.class.getName() + " pl where pl.key = '" + date + "'");
        String log = "";
        try
        {
            PlainLog pl = (PlainLog) q.getSingleResult();
            log = pl.getLog().getValue();
        }
        catch (NoResultException e)
        {
            log = NO_LOGS_EXIST_FOR + date;
        }
        return log;
    }

    public static String getFilteredLogsJSON(LocalDate date)
    {
        List<String> logs = getFilteredLog(date.toString());
        StringBuffer sb = new StringBuffer();
        sb.append("{\n\"logs\" : [\"");
        for (String log : logs)
        {
            sb.append(log).append("\", \"");
        }
        sb.append("last\"],\n");
        sb.append("\"nextDate\" : \"").append(date.plusDays(1)).append("\",\n");
        sb.append("\"prevDate\" : \"").append(date.minusDays(1)).append("\",\n");
        sb.append("\"currDate\" : \"").append(date).append("\"\n");
        sb.append("}");
        return sb.toString();
    }

    public static List<String> getFilteredLog(String date)
    {
        String log = getLog(date);

        return filter(log);
    }

    private static List<String> filter(String log)
    {
        StringTokenizer st = new StringTokenizer(log, "\n");
        List<String> filteredLog = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            String filter = st.nextToken();//applyFilter(st.nextToken());
            filter = filter.replace("\n", "").replace("\t", "    ").trim();
            if (!(filter.isEmpty()))
            {
                filteredLog.add(StringEscapeUtils.escapeHtml(filter));
            }
        }
        return filteredLog;
    }

    private static String applyFilter(String token)
    {
        if ((token.contains("has joined")) || (token.contains("has quit")) || (token.contains("has left")) || (token.contains("title: IRC log from"))
                || (token.contains("is now known a")) || (token.contains("ChanServ sets mode")))
        {
            return "";
        }
        return token + "\n";
    }

    @SuppressWarnings("unchecked")
    public static List<String> getLogDates()
    {
        return EMF.get().createEntityManager().createQuery("select pl.key from " + PlainLog.class.getName() + " pl order by pl.key").getResultList();
    }

    private static boolean isBeforeToday(String date)
    {
        return new LocalDate(date).isBefore(new LocalDate());
    }
}