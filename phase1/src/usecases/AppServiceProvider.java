package usecases;

import boot.*;
import eventhandler.HandlesEvents;
import persistence.PersistenceInterface;
import usecases.access.AccessManager;
import usecases.alerts.*;
import usecases.authentication.Authenticator;
import usecases.config.ConfigManager;
import usecases.items.*;
import usecases.meeting.*;
import usecases.rules.*;
import usecases.trade.*;
import usecases.users.UserManager;
import java.util.*;


/**
 * Builds objects necessary to run the core application.
 */
public class AppServiceProvider implements ProvidesServices, HoldsBindings {


    /**
     * Registers use-cases classes into a map and returns this map.
     * @param app The application container.
     * @return A map of keys and their concrete implementations.
     */
    @Override
    public Map<String, Object> boot(HoldsBindings app) {
        PersistenceInterface gateway = app.get("PersistenceInterface",  PersistenceInterface.class);
        HandlesEvents handlesEvents = app.get("HandlesEvents",  HandlesEvents.class);

        this.bindings.put("MeetingManager", new MeetingManager(gateway));
        this.bindings.put("TransactionManager", new TransactionManager(gateway));
        this.bindings.put("UserManager", new UserManager(gateway));
        this.bindings.put("TradeManager", new TradeManager(gateway));
        this.bindings.put("ItemEditor", new ItemEditor(gateway));
        this.bindings.put("ItemFetcher", new ItemFetcher(gateway));
        this.bindings.put("TradeFactory", new TradeFactory(gateway));
        this.bindings.put("MeetingFactory", new MeetingFactory(gateway));
        this.bindings.put("Authenticator", new Authenticator(gateway, handlesEvents));
        this.bindings.put("ConfigManager", new ConfigManager(gateway, handlesEvents));

        this.bindings.putAll(new RulesServiceProvider().boot(app));
        this.bindings.putAll(new AlertServiceProvider().boot(app));
        this.bindings.put("AccessManager", new AccessManager(gateway,app.get("RuleValidator", RuleValidator.class)));

        SystemFacade systemFacade = new SystemFacade(
                this.get("AlertManager", AlertManager.class),
                this.get("AccessManager", AccessManager.class),
                this.get("Authenticator", Authenticator.class),
                this.get("ConfigManager", ConfigManager.class),
                this.get("RuleValidator", RuleValidator.class),
                this.get("UserManager", UserManager.class)
        );
        this.bindings.put("SystemFacade", systemFacade);

        TradingFacade tradingFacade = new TradingFacade(
                this.get("ItemEditor", ItemEditor.class),
                this.get("ItemFetcher", ItemFetcher.class),
                this.get("MeetingFactory", MeetingFactory.class),
                this.get("MeetingManager", MeetingManager.class),
                this.get("TradeFactory", TradeFactory.class),
                this.get("TradeManager", TradeManager.class),
                this.get("TransactionManager", TransactionManager.class)
        );
        this.bindings.put("TradingFacade", tradingFacade);

        return bindings;

    };




}
