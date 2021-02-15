package usecases.access;

import java.io.IOException;

public interface ProvidesAccess {

    /**
     * Checks if the user is an administrator.
     *
     * @return True iff the user is an administrator.
     * @throws IOException if there is a IO error.
     */
    boolean isAdmin() throws IOException;

    /**
     * Checks if the user's account is frozen.
     *
     * @return True iff the user's account is frozen.
     * @throws IOException if there is a IO error.
     */
    boolean isFrozen() throws IOException;

    /**
     * A method checking if the user can lend
     *
     * @return True iff the user lend
     * @throws IOException if there is a IO error.
     */
    boolean canLend() throws IOException;

    /**
     * A method checking if the user can borrow
     *
     * @return True iff the user borrow
     * @throws IOException if there is a IO error.
     */
    boolean canBorrow() throws IOException;

}
