package eventhandler.events;

import eventhandler.listeners.*;
import java.util.*;


/**
 * An event that takes place when a user registers.
 */
public class UserRegisteredEvent implements PublishesData {

    /**
     * The userId of the user that just registered.
     */
    private int userId;

    /**
     * Holds all the listeners of this event.
     */
    private final List<HandlesUserRegistered> listeners = new ArrayList<>();


    /**
     * Initializes this class
     * @param wishlistInitializer The listener that initializes a wish list.
     */
    public UserRegisteredEvent(WishlistInitializer wishlistInitializer) {
        listeners.add(wishlistInitializer);
    }


    /**
     * Returns the user id of the user that just registered.
     * @return A user id.
     */
    public Integer getUserId() {
        return this.userId;
    }


    /**
     * Passes data to this event.
     */
    public void passData(Object data) {
        this.userId = (Integer) data;
    }



    /**
     * Notifies listeners to handle this event.
     */
    @Override
    public void fire() {
        for (HandlesUserRegistered listener : this.listeners) {
            listener.handle(this);
        }
    }


}
