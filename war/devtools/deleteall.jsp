<%
        org.mifos.irc.db.EMF.get().createEntityManager().createQuery("delete from org.mifos.irc.db.PlainLog p").executeUpdate();
        org.mifos.irc.db.EMF.get().createEntityManager().createQuery("delete from org.mifos.irc.db.ConfigStore c").executeUpdate();
%>