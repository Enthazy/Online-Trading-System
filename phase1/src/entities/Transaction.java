package entities;

import persistence.Persistable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a Transaction, including a list of trades
 * All transaction can be consider as oneWay transaction or twoWay transaction
 * The different is that oneWay Transaction only has one lender and one borrower, but twoWay Transaction has two people
 * exchange there items
 *
 * @version July 2, 2020
 */
public class Transaction implements Persistable {
    /**
     * The id of this Transaction
     */
    private int transId;

    /**
     * Whether this transaction is one way or two way
     */
    private boolean oneWay;

    /**
     * the list of trade id in this transaction
     */
    private List<Integer> tradeList;


    /**
     * the list of meeting id in this transaction
     */
    private List<Integer> meetingList;

    /**
     * Whether this meeting is soft deleted or not
     */
    private boolean softDelete;

    /**
     * Default constructor with input id
     * Create a new transaction with input id, trade list, user list, and whether it is one way or two way
     *
     * @param id          the id of this transaction
     * @param tradeList   the list of trade
     * @param meetingList the list of meeting
     */
    public Transaction(int id, List<Integer> tradeList, List<Integer> meetingList) {
        this.meetingList = meetingList;
        this.transId = id;
        this.tradeList = tradeList;
        if (tradeList.size() == 1) {
            this.oneWay = true;
        } else {
            this.oneWay = false;
        }

        this.softDelete = false;
    }

    /**
     * Default constructor without input id
     * Create a new transaction with trade list, user list, and whether it is one way or two way
     * set the default id to 0
     *
     * @param tradeList   the list of trade
     * @param meetingList the list of meeting
     */
    public Transaction(List<Integer> tradeList, List<Integer> meetingList) {
        this.transId = 0;
        this.tradeList = tradeList;
        this.meetingList = meetingList;
        if (tradeList.size() == 1) {
            this.oneWay = true;
        } else {
            this.oneWay = false;
        }
    }

    /**
     * return whether this transaction is one-way or not
     *
     * @return whether this transaction is one-way or two-way
     */
    public boolean isOneWay() {
        return this.oneWay;
    }

    /**
     * Get the list of all trades in this transaction
     *
     * @return the list of trades
     */
    public List<Integer> getTradeList() {
        return tradeList;
    }

    /**
     * Get the list of all user id in this transaction
     *
     * @return the list of meeting
     */
    public List<Integer> getMeetingList() {
        return meetingList;
    }

    /**
     * A method to get the key to this Transaction, which is the transId
     *
     * @return the id of this transaction
     */
    @Override
    public int getKey() {
        return this.transId;
    }

    /**
     * set a new transId
     *
     * @param id the new id of transaction
     */
    @Override
    public void setKey(int id) {
        this.transId = id;
    }

    /**
     * Simply return "transId"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "transId";
    }

    /**
     * return an Arraylist of all property
     *
     * @return list of the names of attributes
     */
    @Override
    public List<String> getColumns() {
        List col = new ArrayList();
        col.add("transId");
        col.add("oneWay");
        col.add("tradeList");
        col.add("meetingList");
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
        return "Transaction{" +
                "transId=" + transId +
                ", oneWay=" + oneWay +
                ", tradeList=" + tradeList +
                ", meetingList=" + meetingList +
                ", softDelete=" + softDelete +
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
    public boolean getSoftDelete() {
        return this.softDelete;
    }
}
