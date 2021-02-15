package persistence;

import java.io.Serializable;
import java.util.List;


/**
 * I need all entities that wish to be saved to implement this and Serializable (or .ser won't work)
 * This will make all the entities able to be saved as a:
 * .ser file, in a .csv file, .json file or in relational database
 */
public interface Persistable extends Serializable {

    /**
     * Retrieves the unique key associated with this Entity
     * For example, if this is the User Class, and the unique user Id is stored in "public int userId",
     * then this method should return the integer 5 (or whatever integer it is).
     *
     * @return an integer representing the unique id of this entity
     */
    int getKey();


    /**
     * Sets the unique primary key associated with this Entity.
     *
     * @param id an integer representing the unique id of this entity.
     */
    void setKey(int id);


    /**
     * Retrieves the name of the unique key associated with this Entity
     * For example, if this is the User Class, and the unique user Id is stored in "public int userId",
     * then this method should return the STRING "userId"
     *
     * @return a string which is the instance variable name in which the unique key is stored.
     */
    String getPrimaryKeyName();


    /**
     * Lists all the properties that we want to be saved
     * For example, if we want to save 'username', 'password', then that's what this method should return
     * @return
     */
    List<String> getColumns();



}
