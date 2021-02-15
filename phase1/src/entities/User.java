package entities;

import persistence.Persistable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents an user account
 *
 * @version July 02, 2020
 */
public class User implements Persistable {
    /**
     * User's id which is unique
     */
    private int userId;

    /**
     * User's name
     */
    private String name;

    /**
     * The password which can be used to login
     */
    private String password;

    /**
     * User's status, including normal, frozen, admin, requestUnfreeze
     */
    private String status;

    /**
     * Whether this meeting is soft deleted or not
     */
    private boolean softDelete;

    /**
     * Default constructor with input id
     * Create an user with input information, and the status is normal
     * This constructor will NOT generate wishlist and inventory
     *
     * @param id       the id of this user
     * @param name     the name of user
     * @param password the password of this user which used to login
     * @param status   the status of this user, the beginning status should be "admin" or "normal"
     */
    public User(int id, String name, String password, String status) {
        this.userId = id;
        this.name = name;
        this.password = password;
        this.status = status;
        this.softDelete = false;
    }

    /**
     * Default constructor without input id
     * Create an user with input information, and the status is normal
     * Set the default id to 0, it will be changed later
     * This constructor will NOT generate wishlist and inventory
     *
     * @param name     the name of user
     * @param password the password of this user which used to login
     * @param status   the status of this user, the beginning status should be "admin" or "normal"
     */
    public User(String name, String password, String status) {
        this.userId = 0;
        this.name = name;
        this.password = password;
        this.status = status;
        this.softDelete = false;
    }

    /**
     * The methods to get the name of this user
     *
     * @return the name of user
     */
    public String getName() {
        return name;
    }

    /**
     * The methods to get the password
     *
     * @return the password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * The methods to get the status
     *
     * @return the status of this user
     */
    public String getStatus() {
        return status;
    }


    /**
     * The methods to set the new name to this user
     *
     * @param name the new name for this user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The methods to set the new password to this user
     *
     * @param password the new password for this user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * request for unfreeze account only if this user's status is froze
     * change the status of this user to "requestUnfreeze"
     */
    public void requestUnfreeze() {
        if (status.equals("frozen")) {
            status = "requestUnfreeze";
        }
    }


    /**
     * To set the method and return true only if the set succeed
     * The status can only switch to the status in status list
     *
     * @param status the new status for this User
     * @return true
     */
    public boolean setStatus(String status) {
        this.status = status;
        return true;
    }


    /**
     * A method to get the key to this user, which is the id
     *
     * @return the id of this user
     */
    @Override
    public int getKey() {
        return this.userId;
    }

    /**
     * A method to change the id of this user
     *
     * @param id set new id to user
     */
    @Override
    public void setKey(int id) {
        this.userId = id;
    }

    /**
     * return "userId"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "userId";
    }

    /**
     * return an Arraylist of all property
     *
     * @return list of the names of attributes
     */
    @Override
    public List<String> getColumns() {
        List<String> col = new ArrayList<>();
        col.add("userId");
        col.add("name");
        col.add("password");
        col.add("status");
        return col;
    }

    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    /**
     * set the variable to soft delete
     *
     * @param softDelete set new boolean to softDelete
     */
    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete;
    }

    /**
     * get whether this item is soft deleted or not
     *
     * @return whether this item is soft deleted or not
     */
    public boolean isSoftDeleted() {
        return this.softDelete;
    }
}

