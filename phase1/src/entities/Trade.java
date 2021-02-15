package entities;

import persistence.Persistable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a trade
 * The trade include a list of items, the id of lender and borrower, and whether it is completed or not
 * this trade can be considered as temporary or permanent
 *
 * @version July 02, 2020
 */

public class Trade implements Persistable {
    /**
     * The id of lender
     */
    private int lenderId;

    /**
     * The id of borrower
     */
    private int borrowerId;

    /**
     * The list of items in this trade
     */
    private List<Integer> itemList;


    /**
     * Whether this trade complete or not
     */
    private boolean complete;

    /**
     * The id of this trade
     */
    private int tradeId;


    /**
     * Whether this meeting is soft deleted or not
     */
    private boolean softDelete;

    /**
     * Default constructor for trade with input trade id
     * Create a trade with input information with duration
     *
     * @param tradeId     the id of thi trade
     * @param lenderId    the id of lender
     * @param borrowerId  the id of the borrower
     * @param itemList    the item list of this trade
     */
    public Trade(int tradeId, int lenderId, int borrowerId, List<Integer> itemList) {
        this.tradeId = tradeId;
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.itemList = itemList;
        this.complete = false;
        this.softDelete = false;
    }

    /**
     * Constructor for trade without input trade id
     * Create a trade with input information with duration
     *
     * @param lenderId    the id of lender
     * @param borrowerId  the id of the borrower
     * @param itemList    the item list of this trade
     */
    public Trade(int lenderId, int borrowerId, List<Integer> itemList) {
        this.tradeId = 0;
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.itemList = itemList;
        this.complete = false;
        this.softDelete = false;
    }


    /**
     * get the id of this trade
     *
     * @return the id of this trade
     */
    public int getTradeId() {
        return tradeId;
    }

    /**
     * Whether it is complete or not
     *
     * @return whether this trade is complete or not
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Get borrower's id
     *
     * @return the id of the borrower
     */
    public int getBorrowerId() {
        return borrowerId;
    }

    /**
     * Get lender's id
     *
     * @return the id of the lender
     */
    public int getLenderId() {
        return lenderId;
    }

    /**
     * Get the list of items
     *
     * @return the list of all items in this trade
     */
    public List<Integer> getItemList() {
        return itemList;
    }

    /**
     * change the complete status
     *
     * @param status set the status true or boolean
     */
    public void setComplete(boolean status) {
        this.complete = status;
    }

    /**
     * Reset the id of borrower
     *
     * @param borrowerId the id of the borrower
     */
    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    /**
     * Reset the id of lender
     *
     * @param lenderId the id of the lender
     */
    public void setLenderId(int lenderId) {
        this.lenderId = lenderId;
    }


    /**
     * A method to get the key to this trade, which is the tradeId
     *
     * @return the id of this trade
     */
    @Override
    public int getKey() {
        return tradeId;
    }


    /**
     * set a new tradeId
     *
     * @param id the new id of trade
     */
    @Override
    public void setKey(int id) {
        this.tradeId = id;
    }

    /**
     * Simply return "tradeId"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "tradeId";
    }

    /**
     * return an Arraylist as all property
     *
     * @return list of the names of attributes
     */
    @Override
    public List<String> getColumns() {
        List<String> col = new ArrayList<>();
        col.add("lenderId");
        col.add("borrowerId");
        col.add("itemList");
        col.add("complete");
        col.add("tradeId");
        col.add("softDelete");
        return col;
    }

    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Trade{" +
                "lenderId=" + lenderId +
                ", borrowerId=" + borrowerId +
                ", itemList=" + itemList +
                ", complete=" + complete +
                ", tradeId=" + tradeId +
                ", softDelete=" + softDelete +
                '}';
    }

    /**
     * set the attribute to soft delete
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
    public boolean getSoftDelete() {
        return this.softDelete;
    }

}

