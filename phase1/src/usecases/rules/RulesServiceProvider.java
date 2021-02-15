package usecases.rules;

import boot.HoldsBindings;
import boot.ProvidesServices;
import persistence.PersistenceInterface;
import usecases.trade.TransactionManager;

import java.util.Map;

/**
 * Provide the service of rule checking, allows the container to load all the rules package's classes in the main app container.
 */
public class RulesServiceProvider implements ProvidesServices, HoldsBindings {

    /**
     * Holds all the instantiated classes of rules package.
     *
     * @param app The application container.
     * @return A key value pair of the abstract contract and its concrete implementation.
     */
    @Override
    public Map<String, Object> boot(HoldsBindings app) {
        PersistenceInterface gateway = app.get("PersistenceInterface", PersistenceInterface.class);

        this.bindings.put("MaxIncompleteTransactionRule", new MaxIncompleteTransactionRule());
        this.bindings.put("MaxTransactionPerWeekRule", new MaxTransactionPerWeekRule());
        this.bindings.put("NoMoreBorrowThanLendRule", new NoMoreBorrowThanLendRule());

        this.bindings.put("RuleValidator", new RuleValidator(
                app.get("TransactionManager", TransactionManager.class), gateway)
        );

        return bindings;
    }


}
