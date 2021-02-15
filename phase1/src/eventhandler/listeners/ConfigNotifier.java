package eventhandler.listeners;

import eventhandler.events.AppConfigChangedEvent;
import usecases.config.*;
import java.util.*;

public class ConfigNotifier implements HandlesAppConfigChanged {


    private final List<ListensForConfig> listensForConfig = new ArrayList<ListensForConfig>();


    private final ConfigManager configManager;


    /**
     * Initializes this class.
     * @param configManager The config manager dependency.
     */
    public ConfigNotifier(ConfigManager configManager) {
        this.configManager = configManager;
    }


    /**
     * Everyone who needs to be updated
     * @param l
     */
    public void addListener(ListensForConfig l) {
        this.listensForConfig.add(l);
    }


    /**
     * Every class in the application that implements ListensForConfig will receive a new version of the config params.
     * @param event An event that takes place when the configuration of the system changes.
     */
    @Override
    public void handle(AppConfigChangedEvent event) {
        for (ListensForConfig l: listensForConfig) {
            l.updateConfig(configManager.all());
        }
    }


}
