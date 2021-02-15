package entities;

import persistence.Persistable;

import java.util.List;

public class Config implements Persistable {

    /**
     * Primary key of config
     */
    private int id;

    /**
     * The name of this config
     */
    private String configName;

    /**
     * The value of this config
     */
    private String configValue;

    /**
     * Return the key of this config, which is id
     *
     * @return the id of Inventory
     */
    @Override
    public int getKey() {
        return id;
    }

    /**
     * remove an item from wish list
     *
     * @param id the new id of config
     */
    @Override
    public void setKey(int id) {
        this.id = id;
    }

    /**
     * get the name of key, which is "id"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    /**
     * return null
     *
     * @return nulll
     */
    @Override
    public List<String> getColumns() {
        return null;
    }

    /**
     * return the name of this config
     *
     * @return configName
     */
    public String getConfigName() {
        return this.configName;
    }

    /**
     * set new config name and return this Config
     *
     * @param configName set new name for this config
     * @return this config
     */
    public Config setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    /**
     * return the value of this config
     *
     * @return configVa;ie
     */
    public String getConfigValue() {
        return this.configValue;
    }

    /**
     * set new config value and return this Config
     *
     * @param configValue set new value for config
     * @return this config
     */
    public Config setConfigValue(String configValue) {
        this.configValue = configValue;
        return this;
    }

    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", configName='" + configName + '\'' +
                ", configValue='" + configValue + '\'' +
                '}';
    }
}
