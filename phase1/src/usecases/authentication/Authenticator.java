package usecases.authentication;

import entities.User;
import eventhandler.HandlesEvents;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import usecases.authentication.exceptions.DuplicatedUserNameException;
import usecases.authentication.exceptions.InvalidLoginCredentialsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A use case which is checks user accounts and makes changes to it.
 */
public class Authenticator {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final HandlesEvents eventsHandler;

    /**
     * Instance variables
     */
    private boolean isLogin;
    private int currentUserId;


    /**
     * To create an instance of Authenticator which checks user accounts and makes changes
     *
     * @param gateway to access the file
     * @param eventsHandler
     */
    public Authenticator(PersistenceInterface gateway, HandlesEvents eventsHandler) {
        this.gateway = gateway;
        isLogin = false;
        currentUserId = -1;
        this.eventsHandler = eventsHandler;
    }


    /**
     * check the login credentials, return true if and only if the credentials are valid
     *
     * @param username the user uses
     * @param password the user uses
     * @throws IOException                      if there is an IO error
     * @throws InvalidLoginCredentialsException if a user is trying to login with invalid login credentials
     * @return return true if the user is a authenticated user
     */
    public boolean authenticate(String username, String password) throws IOException, InvalidLoginCredentialsException {

        currentUserId = checkCredentials(username, password);
        isLogin = true;
        this.eventsHandler.fire("UserLoggedInEvent", currentUserId);
        return true;

    }


    /**
     * Create a new user account and save it to the file
     *
     * @param username of the new account
     * @param password of the new account
     * @param isAdmin  Whether this user should be an admin.
     * @return True iff the user was successfully created
     * @throws IOException                 IOException
     * @throws PersistenceException        if there is an exception thrown during persisting to database.
     * @throws DuplicatedUserNameException if there already exists a user with the current username
     */
    public boolean register(String username, String password, boolean isAdmin) throws IOException,
            PersistenceException, DuplicatedUserNameException {

        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getName().equals(username)) {
                throw new DuplicatedUserNameException();
            }
        }
        String status = "normal";
        if (isAdmin) status = "admin";
        User newUser = new User(username, password, status);
        List<User> newUsers = new ArrayList<>();
        newUsers.add(newUser);
        List<User> savedUsers = gateway.create(newUsers, User.class);
        this.eventsHandler.fire("UserRegisteredEvent", savedUsers.get(0).getKey());
        return true;
    }


    /**
     * Create a new normal user account and save it to the file
     *
     * @param username of the new account
     * @param password of the new account
     * @return True iff the user was successfully created
     * @throws IOException                 IOException
     * @throws PersistenceException        if there is an exception thrown during persisting to database.
     * @throws DuplicatedUserNameException if there already exists a user with the current username
     */
    public boolean register(String username, String password) throws IOException, PersistenceException, DuplicatedUserNameException {

        List<User> users = gateway.all(User.class);
        if (users.size() == 0) {
            return this.register(username, password, true);
        }
        return this.register(username, password, false);

    }


    /**
     * Logout the current user.
     *
     * @return the id of the user who wants to be logged out
     */
    public int logout() {
        isLogin = false;
        currentUserId = -1;
        return currentUserId;
    }


    // Private method used as helper method, which returns true iff the credentials is valid
    private int checkCredentials(String username, String password) throws IOException, InvalidLoginCredentialsException {
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                return user.getKey();
            }
        }
        throw new InvalidLoginCredentialsException();
    }
}


