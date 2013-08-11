package org.mifos.irc.db;

import com.google.appengine.api.datastore.Text;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PlainLog {

	@Id
	private String key;

	@Column(nullable = false)
	private Text log;

	public PlainLog(String key, Text log) {
		this.key = key;
		this.log = log;
	}

	public void setLog(Text log) {
		this.log = log;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Text getLog() {
		return log;
	}

	public String getKey() {
		return key;
	}
}