package boot;

import java.util.*;

/**
 * Any package should have a service provider class that implements this
 * interface, in order to have bindings retrieved.
 */
public interface HoldsBindings {

    /**
     * Holds all concrete types for the app
     */
    Map<String, Object> bindings = new HashMap<String, Object>();

    default <T> T get(String name, Class<T> type) {
        if(this.bindings.get(name) == null) {
            System.out.println("Warning: No class found " + name);
        }
        return type.cast(this.bindings.get(name));
    }


}
