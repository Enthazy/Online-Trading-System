package usecases.items;

import entities.*;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import java.io.IOException;
import java.util.*;

/**
 * Handles the editing of anything item related, whether it be an item's availability in inventory,
 * or its addition to a particular user's wishlist.
 */
public class ItemEditor {

    private PersistenceInterface gateway;

    /**
     * Initializes this class.
     * @param gateway PersistenceInterface
     */
    public ItemEditor(PersistenceInterface gateway) {
        this.gateway = gateway;
    }

/********************************************************************************************************
 *
 * Inventory Methods
 *
 *********************************************************************************************************/


    /**
     * Adds a new item to inventory.
     * @param itemName The item's name.
     * @param description The item's description.
     * @param ownerId The user id of the owner.
     * @return True if and only if the item was successfully added.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean addItemToInventory(String itemName, String description, int ownerId) throws IOException, PersistenceException {
        Item newItem = new Item(itemName, description, 0);
        return addItemToInventory(newItem, ownerId);

    }

    /**
     * Add a new Item to inventory.
     * Adding a new Item to inventory does not guarantee that it will be visible.
     * To make a Item visible to the public, we have to change its visibility status.
     * @param newItem the item to be added to the inventory
     * @param userId the user id of the intended user
     * @return True if and only if the item was successfully added.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    private boolean addItemToInventory(Item newItem, int userId) throws IOException, PersistenceException {

        newItem.setOwnerId(userId);
        newItem.setHolderId(userId);
        newItem.setVisibility(false);
        List<Item> updateList = new ArrayList<>();
        updateList.add(newItem);
        gateway.create(updateList, Item.class);
        return true;

    }


    /**
     * Approves an inventory so that is visible.
     * @param itemId The unique id of the item.
     * @return True if and only if the item was successfully made visible.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean makeVisible(int itemId)  throws IOException, PersistenceException {
        List<Integer> l = new ArrayList<>();
        l.add(itemId);
        List<Item> items = gateway.get(l, Item.class);

        if(!items.isEmpty()) {
            Item item = items.get(0);
            item.setVisibility(true);
            List<Item> updateList = new ArrayList<>();
            updateList.add(item);
            gateway.update(updateList, Item.class);
            return true;
        }
        return false;
    }


    /**
     * Soft deletes an item, by marking it soft delete property to true.
     * @param itemId The unique id of the item.
     * @return True if and only if the item was successfully soft deleted
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean softDelete(int itemId) throws IOException, PersistenceException {
        List<Integer> l = new ArrayList<>();
        l.add(itemId);
        List<Item> items = gateway.get(l, Item.class);

        if(!items.isEmpty()) {
            Item item = items.get(0);
            item.setSoftDelete(true);
            List<Item> updateList = new ArrayList<>();
            updateList.add(item);
            gateway.update(updateList, Item.class);
            return true;
        }
        return false;
    }



/********************************************************************************************************
 *
 * Wishlist Methods
 *
 *********************************************************************************************************/


    /**
     * Creates a new wishlist for a user.
     * @param userId The unique id of the user
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public void initializeWishlist(int userId) throws IOException, PersistenceException {
        WishList w = new WishList(userId);
        List<WishList> wishLists = new ArrayList<>();
        wishLists.add(w);
        gateway.create(wishLists, WishList.class);
    }


    /**
     * Add new Item to a user's wishlist.
     * @param itemId the unique id of the item which is to be added to wishlist
     * @param userId the userId who owns this wishlist.
     * @return True if and only if the addition was successful.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean addItemToWishlist(int itemId, int userId) throws IOException, PersistenceException {

        List<WishList> wishLists = gateway.all(WishList.class);
        List<WishList> updateWishlist = new ArrayList<>();
        for (WishList w : wishLists){
            if (w.getOwnerId() == userId) {
                //Check for duplicates
                if (w.getWishList().contains(itemId)) return false;
                w.addWishList(itemId);
                updateWishlist.add(w);
                gateway.update(updateWishlist, WishList.class);
            }
        }
        return true;

    }


    /**
     * Remove an Item from a user's wishlist
     * @param itemId the id of the item which is to be removed
     * @param userId the userId whose wishlist from which this item is to be removed.
     * @throws IOException IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public void removeItemFromWishlist(int itemId, int userId) throws IOException, PersistenceException {

        List<WishList> wishLists = gateway.all(WishList.class);
        List<WishList> updateWishlist = new ArrayList<>();
        for (WishList w : wishLists){
            if (w.getOwnerId() == userId) {
                w.removeWishList(itemId);
                updateWishlist.add(w);
                gateway.update(updateWishlist, WishList.class);
            }
        }

    }


}
