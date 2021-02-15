package eventhandler;

import boot.*;
import eventhandler.events.*;
import eventhandler.listeners.*;
import usecases.authentication.ListensForUser;
import usecases.config.*;
import usecases.items.ItemEditor;
import java.util.*;

public class EventServiceProvider implements ProvidesServices, HoldsBindings {


    /**
     * Registers application-wide events in to a map and returns this map.
     * @param app The application container
     * @return A map of keys and their concrete implementations.
     */
    @Override
    public Map<String, Object> boot(HoldsBindings app) {

        //Builds UserLoggedInEvent and its listeners
        UserUpdater cul = new UserUpdater();
        this.bindings.put("UserUpdater", cul);

        for (Map.Entry<String, Object> entry : this.bindings.entrySet()) {
            if(entry.getValue() instanceof ListensForUser) {
                cul.addListener((ListensForUser) entry.getValue());
            }
        }
        this.bindings.put("UserLoggedInEvent", new UserLoggedInEvent(cul));

        //Builds UserRegisteredEvent and its listeners
        WishlistInitializer cwl = new WishlistInitializer(app.get("ItemEditor", ItemEditor.class));
        this.bindings.put("WishlistInitializer", cwl);
        this.bindings.put("UserRegisteredEvent", new UserRegisteredEvent(cwl));

        //Builds ConfigNotifier and its listeners
        this.bindings.put("ConfigNotifier", new ConfigNotifier(app.get("ConfigManager", ConfigManager.class)));
        for (Map.Entry<String, Object> entry : this.bindings.entrySet()) {
            if(entry.getValue() instanceof ListensForConfig) {
                app.get("ConfigNotifier", ConfigNotifier.class).addListener((ListensForConfig) entry.getValue());
            }
        }
        this.bindings.put("AppConfigChangedEvent", new AppConfigChangedEvent(app.get("ConfigNotifier", ConfigNotifier.class)));

        HandlesEvents e = app.get("HandlesEvents", EventHandler.class);
        e.register(app);

        return this.bindings;

    }




}
