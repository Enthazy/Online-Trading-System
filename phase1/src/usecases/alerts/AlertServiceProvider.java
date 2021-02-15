package usecases.alerts;

import boot.HoldsBindings;
import boot.ProvidesServices;
import persistence.PersistenceInterface;
import usecases.rules.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provide the service of alerting, allows the container to load all the alerts package's classes in the main app container.
 */
public class AlertServiceProvider implements ProvidesServices, HoldsBindings {

    /**
     * Holds all the instantiated classes of alerts package.
     *
     * @param app The application container.
     * @return A key value pair of the abstract contract and its concrete implementation.
     */
    @Override
    public Map<String, Object> boot(HoldsBindings app) {
        PersistenceInterface gateway = app.get("PersistenceInterface", PersistenceInterface.class);

        AddInventoryAlert addInv = new AddInventoryAlert(gateway);
        FreezeUserAlert freezeUser = new FreezeUserAlert(app.get("RuleValidator", RuleValidator.class));
        freezeUser.setRules(this.getFreezeRules(app));
        UnfreezeUserAlert unfreeze = new UnfreezeUserAlert(gateway);

        this.bindings.put("AddInventoryAlert", addInv);
        this.bindings.put("FreezeUserAlert", freezeUser);
        this.bindings.put("UnfreezeUserAlert", unfreeze);
        this.bindings.put("AlertManager", new AlertManager(gateway, Arrays.asList(addInv, freezeUser, unfreeze)));

        return this.bindings;

    }


    // Private method used as helper method, it provides the system rules which are needed to check if a user should be
    // considered to be frozen
    private List<SystemRule> getFreezeRules(HoldsBindings app) {
        List<SystemRule> rules = new ArrayList<SystemRule>();
        rules.add(app.get("MaxIncompleteTransactionRule", MaxIncompleteTransactionRule.class));
        rules.add(app.get("MaxTransactionPerWeekRule", MaxTransactionPerWeekRule.class));
        rules.add(app.get("NoMoreBorrowThanLendRule", NoMoreBorrowThanLendRule.class));
        return rules;
    }


}
