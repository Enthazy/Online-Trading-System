package boot;

import java.util.Map;

/**
 * Packages must interact with the main dependency injection container by implementing a ProvidesServices interface,
 * which allows the container to load all the package's classes in the main app container.
 */
public interface ProvidesServices {

    /**
     * Holds all the instantiated classes of a particular package.
     * @param app The application container.
     * @return A key value pair of the abstract contract and its concrete implementation.
     */
    Map<String, Object> boot(HoldsBindings app);

}
