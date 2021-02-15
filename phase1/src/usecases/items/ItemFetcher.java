package usecases.items;

import entities.*;
import persistence.PersistenceInterface;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Executes a ItemQuery and returns the results in the specified format of the query.
 * Can execute a number of pre-defined filters, such as whether the item is approve or not etc.
 * Can return a list of ids, a list of item names, or a list of Item objects.
 */
public class ItemFetcher {

    /**
     * Class dependencies
     */
    PersistenceInterface gateway;

    /**
     * Initializes this class.
     * @param gateway PersistenceInterface
     */
    public ItemFetcher(PersistenceInterface gateway) {
        this.gateway = gateway;
    }

    /**
     * Stores the result of the current fetch.
     */
    private List<Item> allItems = new ArrayList<Item>();
    private List<Item> currentList = new ArrayList<Item>();
    private List<WishList> wishList = new ArrayList<WishList>();


/********************************************************************************************************
 *
 * Public interface of the item fetcher.
 *
 *********************************************************************************************************/

    /**
     * Builds a new query.
     * @return ItemQueryBuilder
     */
    public ItemQueryBuilder query() {
        return new ItemQueryBuilder(this);
    }


    /**
     * Returns a list of item ids.
     * @param query ItemQueryBuilder
     * @return Returns a list of item ids.
     * @throws IOException IOException
     */
    public List<Integer> fetchIds(ItemQueryBuilder query) throws IOException{
        this.fetchObjects(query);
        List<Integer> ids = new ArrayList<Integer>();
        for (Item item: this.currentList) {
            ids.add(item.getKey());
        }
        return ids;
    }

    /**
     * Returns a list of item names.
     * @param query ItemQueryBuilder
     * @return Returns a list of item ids.
     * @throws IOException IOException
     */
    public List<String> fetchNames(ItemQueryBuilder query) throws IOException{
        this.fetchObjects(query);
        List<String> names = new ArrayList<String>();
        for (Item item: this.currentList) {
            names.add(item.getName());
        }
        return names;
    }


    /**
     * Returns a list of item objects.
     * @param query ItemQueryBuilder
     * @return Returns a list of item objects.
     * @throws IOException IOException
     */
    public List<Item> fetchObjects(ItemQueryBuilder query) throws IOException {
        //Only fetch once
        if(!query.hasBeenFetched()) {
            this.reset();
            this.all();

            //Then we run filter methods
            try {
                this.filterBoolean(query);
                this.filterInteger(query);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                //We do nothing if there's no such filter.
            }
            query.setFetched(true);
        }
        return this.currentList;
    }


/********************************************************************************************************
 *
 * Methods that decide which filters to run
 *
 *********************************************************************************************************/
    private void filterBoolean(ItemQueryBuilder query) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (Map.Entry<String, Boolean> entry : query.getBooleanFilters().entrySet()) {
            if(entry.getValue() != null) {
                this.getClass().getDeclaredMethod(entry.getKey()).invoke(this);
            }
        }
    }


    private void filterInteger(ItemQueryBuilder query) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (Map.Entry<String, Integer> entry : query.getIntegerFilters().entrySet()) {
            if(entry.getValue() != null) {
                this.getClass().getDeclaredMethod(entry.getKey(), Integer.class).invoke(this, entry.getValue());
            }
        }
    }


/********************************************************************************************************
 *
 * All the Integer Filters Available
 *
 *********************************************************************************************************/
    private void findById(Integer itemId) {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getKey() == itemId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void exceptOwnedBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getOwnerId() != userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void onlyOwnedBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getOwnerId() == userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void exceptHeldBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getHolderId() != userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void onlyHeldBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getHolderId() == userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void inWishlistOf(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        WishList wishList = this.getWishlistOf(userId);
        if (wishList != null) {
            for(Item item: this.currentList) {
                if (wishList.getWishList().contains(item.getKey())) filtered.add(item);
            }
        }
        this.currentList = filtered;
    }

    private void notInWishlistOf(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        WishList wishList = this.getWishlistOf(userId);
        //If there is a wishlist, we must only add items not in the wishlist
        if (wishList != null) {
            for(Item item: this.currentList) {
                if (!wishList.getWishList().contains(item.getKey())) filtered.add(item);
            }
            this.currentList = filtered;
        }
        //Otherwise, we do nothing and no filtering has taken place
    }


/********************************************************************************************************
 *
 * All the Boolean Filters Available
 *
 *********************************************************************************************************/
    private void heldByOwner() {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.getOwnerId() == item.getHolderId()) filtered.add(item);
        }
        this.currentList = filtered;
    }


    private void onlyApproved() {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (item.isVisible()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void exceptApproved() {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            if (!item.isVisible()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void ownedByUnfrozenUser() throws IOException {
        List<Item> filtered = new ArrayList<Item>();
        for(Item item: this.currentList) {
            User user = this.getOwner(item.getOwnerId());
            if (user != null && !user.getStatus().equals("frozen")) filtered.add(item);
        }
        this.currentList = filtered;
    }

    private void onlyDeleted() {
        List<Item> filtered = new ArrayList<>();
        for(Item item: this.currentList) {
            if (item.isSoftDeleted()) filtered.add(item);
        }
        this.currentList = filtered;
    }


    private void notDeleted() {
        List<Item> filtered = new ArrayList<>();
        for(Item item: this.currentList) {
            if (!item.isSoftDeleted()) filtered.add(item);
        }
        this.currentList = filtered;
    }


/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/
    private void reset() {
        this.allItems = new ArrayList<Item>();
        this.currentList = new ArrayList<Item>();
        this.wishList = new ArrayList<WishList>();
    }

    private void all() throws IOException{
        this.allItems = gateway.all(Item.class);
        this.currentList.addAll(this.allItems);
        this.wishList = gateway.all(WishList.class);
    }

    private User getOwner(int userId) throws IOException {
        List<User> users = gateway.get(new ArrayList<>(Collections.singletonList(userId)), User.class);
        if (!users.isEmpty()) return users.get(0);
        return null;
    }


    private WishList getWishlistOf(Integer userId) {
        for(WishList wishList: this.wishList) {
            if (wishList.getOwnerId() == userId) return wishList;
        }
        return null;
    }



}
