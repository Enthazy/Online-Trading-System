package eventhandler.listeners;

import java.util.*;
import eventhandler.events.UserLoggedInEvent;
import usecases.authentication.ListensForUser;

/**
 * Notifies any part of the application that needs to know who the current user is.
 */
public class UserUpdater implements HandlesUserLoggedIn {

    private final List<ListensForUser> listensForUser = new ArrayList<>();

    /**
     * Everyone who needs to be updated
     * @param l
     */
    public void addListener(ListensForUser l) {
        this.listensForUser.add(l);
    }


    /**
     * Every class in the application that implements ListensForUser will receive the user id of the new user.
     * @param event An event that takes place when a user logs in.
     */
    @Override
    public void handle(UserLoggedInEvent event) {
        for (ListensForUser l: listensForUser) {
            l.updateUserId(event.getUserId());
        }
    }



}
