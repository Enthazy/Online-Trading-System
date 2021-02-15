package eventhandler;

import boot.HoldsBindings;

/**
 * Allows any class to fire events to be broadcasted to any curious listeners!
 */
public interface HandlesEvents {


    /**
     * Fires an event with the event name that's given, and passes the event the data.
     * @param eventName The name of the event being fired.
     * @param data The data passed to the event.
     */
    void fire(String eventName, Object data);

    /**
     * Fires an event with the event name that's given, without data.
     * @param eventName The name of the event being fired.
     */
    void fire(String eventName);

    /**
     * Binds events to the event handler.
     * @param app The application container.
     */
    void register(HoldsBindings app);

}
