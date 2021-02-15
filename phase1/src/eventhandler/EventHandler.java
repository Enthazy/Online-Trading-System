package eventhandler;

import java.util.*;
import eventhandler.events.*;
import boot.*;

/**
 * Application wide events that need to be known by many parties.
 *
 * For example, if a user successfully registers, many parts of an app want to know, including:
 * If we want to send out a welcome email, the EmailHandler might want to know.
 * If we want to update our Reporter that tabulates registration statistics, so the Reporter might want to know.
 * If this app allows user to store files, then we need to create a "cloud drive" for the user, so the storage allocation part the app needs to know.
 * If this is a social media app, then we also might want to notify his/her friends based on the user's contacts,
 *   so that connections can be built quickly, so the "Recommendation" part of the app might need to know
 */

public class EventHandler implements HandlesEvents {


    /**
     * Contains class name and instance of event.
     */
    private final Map<String, Publishes> knownEvents = new HashMap<String, Publishes>();

    /**
     * Contains class name and instance of event with data.
     */
    private final Map<String, PublishesData> knownEventsWithData = new HashMap<String, PublishesData>();


    /**
     * Register all the events that can be fired.
     * @param app The application container.
     */
    public void register(HoldsBindings app) {
        this.knownEventsWithData.put("UserLoggedInEvent", app.get("UserLoggedInEvent", UserLoggedInEvent.class));
        this.knownEventsWithData.put("UserRegisteredEvent", app.get("UserRegisteredEvent", UserRegisteredEvent.class));
        this.knownEvents.put("AppConfigChangedEvent", app.get("AppConfigChangedEvent", AppConfigChangedEvent.class));
    }


    /**
     * Fires an event with the event name that's given, and passes the event the data.
     * @param eventName The name of the event being fired.
     * @param data The data passed to the event.
     */
    public void fire(String eventName, Object data) {
        for (Map.Entry<String, PublishesData> entry : this.knownEventsWithData.entrySet()) {
            if(entry.getKey().equals(eventName)) {
                PublishesData event = entry.getValue();
                event.passData(data);
                event.fire();
            }
        }
    }


    /**
     * Fires an event with an event name that's given with no data.
     * @param eventName The name of the event being fired.
     */
    public void fire(String eventName) {
        for (Map.Entry<String, Publishes> entry : this.knownEvents.entrySet()) {
            if(entry.getKey().equals(eventName)) {
                Publishes event = entry.getValue();
                event.fire();
            }
        }
    }



}

