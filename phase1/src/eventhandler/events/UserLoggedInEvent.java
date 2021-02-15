package eventhandler.events;

import eventhandler.listeners.*;
import java.util.*;


/**
 * An event that takes place when a user logs in.
 */
public class UserLoggedInEvent implements PublishesData {

    /**
     * The userId of the user that just logged in.
     */
    private int userId;

    /**
     * Holds all the listeners of this event.
     */
    private final List<HandlesUserLoggedIn> listeners = new ArrayList<>();


    /**
     * Initializes this class.
     * @param userUpdater The listener that updates whichever class that needs to know the current user id.
     */
    public UserLoggedInEvent(UserUpdater userUpdater) {
        listeners.add(userUpdater);
    }


    /**
     * Returns the user id of the user that just logged in.
     * @return A user id.
     */
    public Integer getUserId() {
        return this.userId;
    }


    /**
     * Note that we might throw a ClassCastException (extends RunTimeException)
     * which indicates the data type passed to the event is incorrect!
     *
     * @param data
     */
    @Override
    public void passData(Object data) {
        this.userId = (Integer) data;
    }


    /**
     * Notifies users to handle this event.
     */
    @Override
    public void fire() {
        for (HandlesUserLoggedIn listener : this.listeners) {
            listener.handle(this);
        }
    }


}
