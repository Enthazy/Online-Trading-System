package usecases;

import usecases.access.AccessManager;
import usecases.alerts.AlertManager;
import usecases.authentication.Authenticator;
import usecases.config.ConfigManager;
import usecases.rules.RuleValidator;
import usecases.users.UserManager;

/**
 * A facade for user/system-related use case classes.
 */
public class SystemFacade {

    /**
     * Class dependencies
     */
    private final AlertManager alert;
    private final AccessManager access;
    private final Authenticator auth;
    private final ConfigManager config;
    private final RuleValidator rules;
    private final UserManager users;

    /**
     * Initializes this class.
     * @param alert AlertManager
     * @param access AccessManager
     * @param auth Authenticator
     * @param config ConfigManager
     * @param rules RuleValidator
     * @param users UserManager
     */
    public SystemFacade(AlertManager alert, AccessManager access, Authenticator auth, ConfigManager config, RuleValidator rules, UserManager users) {
        this.alert = alert;
        this.access = access;
        this.auth = auth;
        this.config = config;
        this.rules = rules;
        this.users = users;

    }

    /**
     * Returns an instance of AlertManager.
     * @return Returns an instance of AlertManager.
     */
    public AlertManager alert() {
        return this.alert;
    }

    /**
     * Returns an instance of AccessManager.
     * @return Returns an instance of AccessManager.
     */
    public AccessManager access() {
        return this.access;
    }

    /**
     * Returns an instance of Authenticator.
     * @return Returns an instance of Authenticator.
     */
    public Authenticator auth() {
        return this.auth;
    }

    /**
     * Returns an instance of ConfigManager.
     * @return Returns an instance of ConfigManager.
     */
    public ConfigManager config() {
        return this.config;
    }

    /**
     * Returns an instance of RuleValidator.
     * @return Returns an instance of RuleValidator.
     */
    public RuleValidator rules() {
        return this.rules;
    }

    /**
     * Returns an instance of UserManager.
     * @return Returns an instance of UserManager.
     */
    public UserManager users() {
        return this.users;
    }


}


