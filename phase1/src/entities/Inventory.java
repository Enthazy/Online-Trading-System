package entities;

import persistence.Persistable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents the Inventory
 * Every user has a list of items they have
 * they can use this
 *
 * @version July 02, 2020
 */
public class Inventory implements Persistable {
    /**
     * The list of item in this inventory, stored as item id
     */
    private List<Integer> inventory;

    /**
     * The user of this wish list
     */
    private int id;

    /**
     * Whether this Inventory is soft deleted or not
     */
    private boolean softDelete;

    /**
     * Default constructor
     * Create an user's Inventory
     *
     * @param id an integer recording to User's primary key
     */
    public Inventory(int id) {
        this.id = id;
        this.inventory = new ArrayList<>();
        this.softDelete = false;
    }

    /**
     * Constructor without input id
     * set id to 0
     */
    public Inventory() {
        this.id = 0;
        this.inventory = new ArrayList<>();
        this.softDelete = false;
    }

    /**
     * Get the Inventory
     *
     * @return the list of items in Inventory
     */
    public List<Integer> getInventroy() {
        return inventory;
    }

    /**
     * add a new item to inventory
     *
     * @param id an integer will be saved in Inventory as the id of item
     */
    public void addList(int id) {
        inventory.add(id);
    }

    /**
     * add a list of items to inventory
     *
     * @param lst the list of all integers will be saved in Inventory as the ids of items
     */
    public void addList(List<Integer> lst) {
        inventory.addAll(lst);
    }

    /**
     * remove an item from wish list
     * @param id the ID of the item that you wish to remove from the inventory
     */
    public void removeList(int id) {
        inventory.remove(id);
    }

    /**
     * Return the key of this inventory, which is id
     *
     * @return the id of Inventory
     */
    @Override
    public int getKey() {
        return this.id;
    }

    /**
     * remove an item from wish list
     *
     * @param id the new id of Inventory
     */
    @Override
    public void setKey(int id) {
        this.id = id;
    }

    /**
     * get the name of key, which is "id"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    /**
     * return list of all names of attributes in this class
     *
     * @return list of the names of attributes
     */
    @Override
    public List<String> getColumns() {
        List col = new ArrayList();
        col.add("inventory");
        col.add("id");
        return col;
    }

    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Inventory{" +
                "inventory=" + inventory +
                ", id=" + id +
                '}';
    }

    /**
     * set the variable to soft delete
     *
     * @param softDelete the boolean for new softDelete attribute
     */
    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete;
    }

    /**
     * get the variable to soft delete
     *
     * @return the boolean of softDelete attribute
     */
    public boolean getSoftDelete() {
        return this.softDelete;
    }
}


