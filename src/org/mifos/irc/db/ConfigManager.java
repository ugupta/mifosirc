package org.mifos.irc.db;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.joda.time.LocalDate;

public class ConfigManager
{

    private static final String START_DATE = "2013-03-15";
    private static final String NEXT_LOG_DATE = "nextLogDate";

    public static String getNextLogDate() throws Exception
    {
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createQuery("select c from " + ConfigStore.class.getName() + " c where c.configKey = '" + "nextLogDate" + "'");
        ConfigStore config = null;
        try
        {
            config = (ConfigStore) q.getSingleResult();
        }
        catch (NoResultException localNoResultException)
        {
        }
        if (config == null)
        {
            config = new ConfigStore(NEXT_LOG_DATE, START_DATE);
            em.persist(config);
            em.close();
        }
        return config.getConfigValue();
    }

    public static void increaseNextLogDate() throws Exception
    {
        EntityManager em = EMF.get().createEntityManager();
        String date = getNextLogDate();
        Query q = em.createQuery("select c from " + ConfigStore.class.getName() + " c where c.configKey = '" + NEXT_LOG_DATE + "'");
        ConfigStore config = (ConfigStore) q.getSingleResult();
        config.setConfigValue(getDatePlusOneDay(date));
        em.merge(config);
        em.close();
    }

    public static String getDatePlusOneDay(String date)
    {
        LocalDate localDate = new LocalDate(date).plusDays(1);
        return localDate.toString();
    }
}