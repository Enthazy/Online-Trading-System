package usecases.authentication;

/**
 * If a class wants to be updated for a change in the current user,
 * implement this interface, and implement updateUserId method.
 * Every time there is a logout/login event, the userId in your class will be magically updated!
 */
public interface ListensForUser {

    /**
     * Updates a class with the user id of the logged in user.
     * @param userId The unique user id for a particular user.
     */
    void updateUserId(int userId);

}
