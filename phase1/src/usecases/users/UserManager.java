package usecases.users;

import persistence.PersistenceInterface;
import persistence.exceptions.*;
import entities.User;
import java.io.IOException;
import java.util.*;

/**
 * Represents a use case for user, which deals with all the changes made to a user upon request.
 */
public class UserManager {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * To create an instance of UserManager
     * @param gateway to access the stored data
     */
    public UserManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }


    /**
     * Gets the username provided a user id.
     * @param userId the unique user id associated with a user.
     * @return A string representing the username
     * @throws IOException IOException.
     */
    public String getUsername(int userId) throws IOException {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(userId);
        List<User> results = this.gateway.get(userIds, User.class);
        return results.get(0).getName();
    }


    /**
     * Returns a list of users that are not administrators.
     * @return A list of user objects that are not administrators.
     * @throws IOException IOException
     */
    public List<User> allNonAdmin() throws IOException {
        List<User>  users = gateway.all(User.class);
        List<User> nonAdmin = new ArrayList<>();
        for (User user: users) {
            if (!user.getStatus().equals("admin")) {
                nonAdmin.add(user);
            }
        }
        return nonAdmin;
    }


    /**
     * Returns a list of usernames that are not administrators.
     * @return A list of usernames that are not administrators.
     * @throws IOException IOException
     */
    public List<String> allNonAdminUsernames() throws IOException {
        List<User> usersObj = this.allNonAdmin();
        List<String> userNames = new ArrayList<String>();
        for (User user: usersObj) {
            userNames.add(user.getName());
        }
        return userNames;
    }


    /**
     * Promotes a user to administrator status.
     * @param userId a unique id representing a user
     * @return True if and only if the update succeeded.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean promote(int userId) throws IOException, PersistenceException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("admin");
        return gateway.update(users, User.class);
    }


    /**
     * Change the password of Entities.User.
     * @param newPassword the new password which the user wants to change to
     * @param userId the user id of the intended user
     * @return true if and only if the password is successfully changed
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean changePassword(String newPassword, int userId) throws IOException, PersistenceException {
        List<User> users = getUserList(userId);
       try {
           users.get(0).setPassword(newPassword);
           return gateway.update(users, User.class);
       }
       catch(IndexOutOfBoundsException e) {
            throw new EntryDoesNotExistException();
       }
    }


    /**
     * Freezes a User's account.
     * @param userId the user id of the intended user
     * @return true if and only if the user has been successfully frozen
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean freezeUser(int userId) throws IOException, PersistenceException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("frozen");
        return gateway.update(users, User.class);
    }


    /**
     * Unfreeze a User's account.
     * @param userId the user id of the intended user
     * @return true if and only if the user is successfully unfrozen
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean unFreezeUser(int userId) throws IOException, PersistenceException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("normal");
        return gateway.update(users, User.class);
    }


    /**
     * Sets a user's status to "request unfreeze" indicating that the user has requested his or her account to be unfrozen.
     * @param userId the user id of the intended user
     * @return true if and only if the user's request is logged
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean requestUnfreeze(int userId) throws IOException, PersistenceException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("requestUnfreeze");
        return gateway.update(users, User.class);
    }



    //Private method used as a helper method
    private List<User> getUserList (int userId) throws IOException {
        List<Integer> intendedUsers = new ArrayList<>();
        intendedUsers.add(userId);
        return gateway.get(intendedUsers, User.class);
    }


}
