package eventhandler.events;

import eventhandler.listeners.*;
import java.util.*;


/**
 * An event that takes place when the configuration of the system changes.
 */
public class AppConfigChangedEvent implements Publishes {

    /**
     * Holds all the listeners of this event.
     */
    private final List<HandlesAppConfigChanged> listeners = new ArrayList<>();

    /**
     * Initializes this class.
     * @param appConfigListener ConfigNotifier
     */
    public AppConfigChangedEvent(ConfigNotifier appConfigListener) {
        listeners.add(appConfigListener);
    }


    /**
     * Notifies users to handle this event.
     */
    @Override
    public void fire() {
        for (HandlesAppConfigChanged listener : this.listeners) {
            listener.handle(this);
        }
    }



}
