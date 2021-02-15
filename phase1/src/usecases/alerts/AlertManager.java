package usecases.alerts;

import entities.Item;
import entities.User;
import persistence.PersistenceInterface;
import usecases.rules.RuleDoesNotExistException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A use case which deals with all the operations relating to various alerts for an admin
 */

public class AlertManager {
    /**
     * Classes dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * Instance variables
     */
    private final Map<String, SystemAlert> alertList = new HashMap<>();


    /**
     * To instantiate an AlertManager which provides alert in the given list
     *
     * @param gateway to access the file
     * @param alerts  which AlertManager require to provide
     */
    public AlertManager(PersistenceInterface gateway, List<SystemAlert> alerts) {
        this.gateway = gateway;
        for (SystemAlert alert : alerts) {
            alertList.put(alert.getAlert(), alert);
        }
    }


    /**
     * To get the id of users who request to be unfrozen
     *
     * @return a List of user ids
     * @throws IOException if there is a IO error.
     */
    public List<Integer> getUnfreezeRequests() throws IOException {
        UnfreezeUserAlert ufa = (UnfreezeUserAlert) alertList.get("UnfreezeUserAlert");
        List<Integer> requests = new ArrayList<>();
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            int userId = user.getKey();
            if (ufa.needAlert(userId)) {
                requests.add(userId);
            }
        }

        return requests;
    }


    /**
     * To get the username of users who request to be unfrozen
     *
     * @return a List of usernames
     * @throws IOException if there is a IO error.
     */
    public List<String> getUnfreezeRequestsStr() throws IOException {
        List<String> names = new ArrayList<>();
        List<Integer> ids = getUnfreezeRequests();
        List<User> users = gateway.get(ids, User.class);
        for (User user : users) {
            names.add(user.getName());
        }
        return names;
    }


    /**
     * To get the id of Items which are requested by a user to be added to inventory
     *
     * @return a List of item ids
     * @throws IOException if there is a IO error.
     */
    public List<Integer> getAddItemRequests() throws IOException {
        AddInventoryAlert aia = (AddInventoryAlert) alertList.get("AddInventoryAlert");
        List<Integer> requests = new ArrayList<>();
        List<Item> items = gateway.all(Item.class);
        for (Item item : items) {
            int itemId = item.getKey();
            if (aia.needAlert(itemId)) {
                requests.add(itemId);
            }
        }

        return requests;
    }


    /**
     * To get the name of Items which are requested by a user to be added to inventory
     *
     * @return a List of item names
     * @throws IOException if there is a IO error.
     */
    public List<String> getAddItemRequestsStr() throws IOException {
        List<Item> items = gateway.get(getAddItemRequests(), Item.class);
        List<String> names = new ArrayList<>();
        for (Item item : items) {
            names.add(item.getName());
        }
        return names;
    }


    /**
     * To get the id of users who needs to be frozen
     *
     * @return a List of user ids
     * @throws IOException if there is a IO error.
     * @throws RuleDoesNotExistException if the rule does not exist
     */
    public List<Integer> getFreezeSuggestions()
            throws IOException, RuleDoesNotExistException {
        FreezeUserAlert fua = (FreezeUserAlert) alertList.get("FreezeUserAlert");
        List<Integer> suggestion = new ArrayList<>();
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            int userId = user.getKey();
            if (fua.needAlert(userId) && !user.getStatus().equals("frozen")) {
                suggestion.add(userId);
            }
        }
        return suggestion;
    }


    /**
     * To get the username of users who needs to be frozen
     *
     * @return a List of usernames
     * @throws IOException if there is a IO error.
     * @throws RuleDoesNotExistException if the rule does not exist
     */
    public List<String> getFreezeSuggestionsStr() throws IOException, RuleDoesNotExistException {
        List<String> names = new ArrayList<>();
        List<Integer> ids = getFreezeSuggestions();
        List<User> users = gateway.get(ids, User.class);
        for (User user : users) {
            if (!user.getStatus().equals("frozen")) {
                names.add(user.getName());
            }
        }
        return names;
    }
}
