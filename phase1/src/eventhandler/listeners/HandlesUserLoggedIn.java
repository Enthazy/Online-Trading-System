package eventhandler.listeners;

import eventhandler.events.UserLoggedInEvent;

public interface HandlesUserLoggedIn {

    /**
     * Handles the event fired
     * @param event UserLoggedInEvent
     */
    void handle(UserLoggedInEvent event);

}
