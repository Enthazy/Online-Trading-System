package boot;

import controller.Menu;
import controller.MenuBuilder;
import controller.controllers.*;
import controller.presenters.ControllerPresenter;
import controller.presenters.ErrorPresenter;
import controller.presenters.TradeDataPresenter;
import controller.presenters.auth.RegisterIterator;
import persistence.*;
import usecases.*;
import usecases.access.*;
import usecases.authentication.*;
import view.*;
import eventhandler.*;


/**
 * Holds all the objects that are required to make the application run!
 * This is a gigantic dependency injection container. We can easily swap out different concrete
 * implementations of the same interface just by changing the instantiations in this class!
 *
 * Any packages that want to hook into this container should implement ProvideServices interface, and implement the boot method.
 */
public class Container implements HoldsBindings {

    /**
     * Registers all the classes close to the core of the app.
     */
    public void start() {
        this.beforeBuild();
        this.buildCore();
        this.afterBuild();
    }


    /**
     * Builds fundamentals of the application, such as persistence and the view,
     * before any application logic classes are built.
     */
    private void beforeBuild() {
        this.bindings.put("PersistenceInterface", new SerPersistenceGateway());
        this.bindings.put("HandlesEvents", new EventHandler());
        this.bindings.put("View", new TextUI());
    }


    /**
     * Builds the heart of the application. Mostly use-case classes.
     */
    private void buildCore() {
        this.bindings.putAll(new AppServiceProvider().boot(this));
    }


    /**
     * Builds/sets up classes that depend on the core application use-case layers
     */
    private void afterBuild() {
        this.buildControllerLayer();

        //Build the event handler
        this.bindings.putAll(new EventServiceProvider().boot(this));

        //We fire a config event to update all the system configuration from persistence
        HandlesEvents e = this.get("HandlesEvents", HandlesEvents.class);
        e.fire("AppConfigChangedEvent");
    }


    /**
     * Builds the controller/presenter layer of the application
     */
    private void buildControllerLayer() {
        this.bindings.put("RegisterIterator", new RegisterIterator());
        this.bindings.put("LoginIterator", new RegisterIterator());
        this.bindings.put("ControllerPresenter", new ControllerPresenter(this.get("View", View.class)));
        this.bindings.put("ErrorPresenter", new ErrorPresenter(this.get("View", View.class)));

        this.bindings.put("TradeDataPresenter", new TradeDataPresenter(
                this.get("TradingFacade", TradingFacade.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("AdminController", new AdminController(
                this.get("TradingFacade", TradingFacade.class),
                this.get("SystemFacade", SystemFacade.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("MeetingController", new MeetingController(
                this.get("TradingFacade", TradingFacade.class),
                this.get("TradeDataPresenter", TradeDataPresenter.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("TransactionController", new TransactionController(
                this.get("TradingFacade", TradingFacade.class),
                this.get("TradeDataPresenter", TradeDataPresenter.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("UserController", new UserController(
                this.get("TradingFacade", TradingFacade.class),
                this.get("SystemFacade", SystemFacade.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("AuthController", new AuthController(
                this.get("Authenticator", Authenticator.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)));


        this.bindings.put("ControllerFacade", new ControllerFacade(
                this.get("UserController", UserController.class),
                this.get("TransactionController", TransactionController.class),
                this.get("AuthController", AuthController.class),
                this.get("MeetingController", MeetingController.class),
                this.get("AdminController", AdminController.class)
        ));

        this.bindings.put("Menu", new Menu(
                this.get("ControllerFacade", ControllerFacade.class),
                this.get("AccessManager", AccessManager.class),
                this.get("ControllerPresenter", ControllerPresenter.class),
                this.get("ErrorPresenter", ErrorPresenter.class)
        ));

        this.bindings.put("MenuBuilder", new MenuBuilder(
                this.get("Menu", Menu.class)
        ));

    }




}
