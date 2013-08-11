/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package org.mifos.irc;

import javax.persistence.EntityManager;
import org.mifos.irc.db.ConfigStore;
import org.mifos.irc.db.EMF;
import org.mifos.irc.db.PlainLog;

public class DeleteAll {
	public static void delete() {
		EntityManager em = EMF.get().createEntityManager();
		em.createQuery("delete from " + PlainLog.class.getName())
				.executeUpdate();
		em.createQuery("delete from " + ConfigStore.class.getName())
				.executeUpdate();
	}
}