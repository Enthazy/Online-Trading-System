package controller.controllers;

public class ControllerFacade {

    /**
     * Class dependencies
     */
    private final UserController user;
    private final TransactionController transaction;
    private final AuthController auth;
    private final MeetingController meeting;
    private final AdminController admin;

    /**
     * Initializes the class
     * @param user UserController
     * @param transaction TransactionController
     * @param auth AuthController
     * @param meeting MeetingController
     * @param admin AdminController
     */
    public ControllerFacade(UserController user, TransactionController transaction, AuthController auth,
                            MeetingController meeting, AdminController admin) {
        this.user = user;
        this.transaction = transaction;
        this.auth = auth;
        this.meeting = meeting;
        this.admin = admin;
    }

    /**
     * Returns an instance of UserController
     * @return Returns an instance of UserController
     */
    public UserController user() {
        return this.user;
    }

    /**
     * Returns an instance of TransactionController
     * @return Returns an instance of TransactionController
     */
    public TransactionController transaction() {
        return this.transaction;
    }

    /**
     * Returns an instance of AuthController
     * @return Returns an instance of AuthController
     */
    public AuthController auth() {
        return this.auth;
    }

    /**
     * Returns an instance of MeetingController
     * @return Returns an instance of MeetingController
     */
    public MeetingController meeting() {
        return this.meeting;
    }

    /**
     * Returns an instance of AdminController
     * @return Returns an instance of AdminController
     */
    public AdminController admin() {
        return this.admin;
    }


}
