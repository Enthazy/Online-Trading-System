package usecases.access;

import entities.User;
import persistence.PersistenceInterface;
import usecases.authentication.ListensForUser;
import usecases.rules.NoMoreBorrowThanLendRule;
import usecases.rules.RuleDoesNotExistException;
import usecases.rules.RuleValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Managers account's access to various functions in the system
 */
public class AccessManager implements ProvidesAccess, ListensForUser {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final RuleValidator validator;

    /**
     * Instance variables
     */
    private int userId;


    /**
     * To create an instance of AccessManager
     *
     * @param gateway   PersistenceInterface which gives access to the stored data
     * @param validator RuleValidator which checks the system rules
     */
    public AccessManager(PersistenceInterface gateway, RuleValidator validator) {
        this.validator = validator;
        this.gateway = gateway;
    }


    /**
     * Updates the current logged in user id.
     *
     * @param userId The unique id representing a user.
     */
    @Override
    public void updateUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Checks if the user is an administrator.
     *
     * @return True iff the user is an administrator.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isAdmin() throws IOException {
        return checkStatus(userId, "admin");
    }

    /**
     * Checks if the user's account is frozen.
     *
     * @return True iff the user's account is frozen.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isFrozen() throws IOException {
        return checkStatus(userId, "frozen");
    }

    /**
     * A method checking if the user can lend
     *
     * @return True iff the user can lend
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean canLend() throws IOException {
        return checkStatus(userId, "normal") | checkStatus(userId, "admin");
    }

    /**
     * A method checking if the user can borrow
     *
     * @return True iff the user can borrow
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean canBorrow() throws IOException {
        NoMoreBorrowThanLendRule rule = new NoMoreBorrowThanLendRule();

        try {
            if (validator.violate(rule, userId)) {
                return false;
            }
        } catch (RuleDoesNotExistException e) {
            return canLend();
        }

        return canLend();
    }


    //Private method used as a helper method
    //This checks the status of a user
    private boolean checkStatus(int userId, String status) throws IOException {
        List<Integer> ids = new ArrayList<>(1);
        ids.add(userId);
        List<User> users = gateway.get(ids, User.class);
        if (users.size() != 0) {
            return users.get(0).getStatus().equals(status);
        }
        return false;
    }

}
