package org.mifos.irc.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfigStore
{

    @Id
    private String configKey;

    @Column(nullable = false)
    private String configValue;

    public ConfigStore(String configKey, String configValue)
    {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey()
    {
        return configKey;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

}