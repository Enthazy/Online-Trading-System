package usecases.trade;

import entities.*;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import usecases.trade.exceptions.TransactionDoesNotExistException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A use case for Transaction, which deals with all operations relating to Transaction including instantiation.
 */
public class TransactionManager {
    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * instantiate an instance of TransactionManager
     *
     * @param gateway to access data stored in file
     */
    public TransactionManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }


    /**
     * To instantiate a transaction
     *
     * @param tradeIds   of the trade involved in the transaction (can get from TradeFactory)
     * @param meetingIds of the meetings involved in the transaction (can get from meetingFactory)
     * @return the id of the created transaction
     * @throws IOException          if there is an IO error
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public int initiateTransaction(List<Integer> tradeIds, List<Integer> meetingIds) throws IOException,
            PersistenceException {

        Transaction transaction = new Transaction(tradeIds, meetingIds);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        List<Transaction> obtainedTransactions = gateway.create(transactions, Transaction.class);
        return obtainedTransactions.get(0).getKey();
    }


    /**
     * To get a record of all transactions
     *
     * @return a List of transaction ids of transactions
     * @throws IOException if there is an IO error
     */
    public List<Integer> getTransactions() throws IOException {
        List<Integer> allTransactions = new ArrayList<>();
        List<Transaction> transactions = gateway.all(Transaction.class);
        for (Transaction transaction : transactions) {
            allTransactions.add(transaction.getKey());
        }
        return allTransactions;
    }


    /**
     * To get a record of all transactions of a user
     *
     * @param userId of the intended user
     * @return a List of Transactions of the intended user
     * @throws IOException if there is an IO error
     */
    public List<Integer> getTransactionsOf(int userId) throws IOException {

        List<Integer> intendedTransactions = new ArrayList<>();

        List<Transaction> transactions = gateway.get(getTransactions(), Transaction.class);
        for (Transaction transaction : transactions) {
            if (getUsers(transaction).contains(userId)) {
                intendedTransactions.add(transaction.getKey());
            }
        }
        return intendedTransactions;
    }


    /**
     * To get a record of all open transactions of a user, which are on-going and not yet complete transactions
     *
     * @param userId of the intended user
     * @return a List of Transactions of the intended user
     * @throws IOException if there is an IO error
     */
    public List<Integer> getOpenTransactionsOf(int userId) throws IOException {
        ArrayList<Integer> intendedTransactions = new ArrayList<>();

        List<Transaction> transactions = gateway.get(getTransactions(), Transaction.class);
        for (Transaction transaction : transactions) {
            if (getUsers(transaction).contains(userId) && onGoing(transaction) && !isComplete(transaction)) {
                intendedTransactions.add(transaction.getKey());
            }
        }
        return intendedTransactions;
    }


    /**
     * To get a record of incomplete transactions of a user, incomplete transaction refers to transaction which is not
     * complete after the suggested due date.
     *
     * @param userId of the intended user
     * @return a List of Transactions of the intended user
     * @throws IOException if there is an IO error
     */
    public List<Integer> getIncompleteTransactionsOf(int userId) throws IOException {
        ArrayList<Integer> intendedTransactions = new ArrayList<>();

        List<Transaction> transactions = gateway.get(getTransactions(), Transaction.class);
        for (Transaction transaction : transactions) {
            if (getUsers(transaction).contains(userId) && !isComplete(transaction) && !onGoing(transaction)) {
                intendedTransactions.add(transaction.getKey());
            }
        }
        return intendedTransactions;
    }


    /**
     * To get a record of transactions instantiated in this week of this user.
     *
     * @param userId of the intended user
     * @return a List of Transactions of the intended user
     * @throws IOException if there is an IO error
     */
    public List<Integer> getWeeklyTransactionsOf(int userId) throws IOException {
        ArrayList<Integer> intendedTransactions = new ArrayList<>();

        List<Transaction> transactions = gateway.get(getTransactionsOf(userId), Transaction.class);
        for (Transaction transaction : transactions) {
            if (thisWeek(transaction)) {
                intendedTransactions.add(transaction.getKey());
            }
        }
        return intendedTransactions;
    }


    /**
     * to obtain the meeting ids of the transaction
     *
     * @param transactionId of the transaction
     * @return the ids of the meeting in the transaction
     * @throws IOException                      if there is an IO error
     * @throws TransactionDoesNotExistException if transaction of the input id does not exist in database
     */
    public List<Integer> getMeetings(int transactionId) throws IOException, TransactionDoesNotExistException {
        Transaction transaction = readTransaction(transactionId);
        return transaction.getMeetingList();

    }


    /**
     * to obtain the trade ids of the transaction
     *
     * @param transactionId of the transaction
     * @return the ids of the trade in the transaction
     * @throws IOException                      if there is an IO error
     * @throws TransactionDoesNotExistException if transaction of the input id does not exist in database
     */
    public List<Integer> getTrades(int transactionId) throws IOException, TransactionDoesNotExistException {
        Transaction transaction = readTransaction(transactionId);
        return transaction.getTradeList();

    }


    /**
     * to get the frequent trading partners of a user
     *
     * @param userId          of the user
     * @param mostNumPartners is the number of trade partners wanted to obtain
     * @return the ids of the trading partners
     * @throws IOException if there is an IO error
     */
    public List<Integer> getFrequentTradingPartner(int userId, int mostNumPartners) throws IOException {


        // First set up maps where the keys are the ids of the trading partner of the user and the values are the
        // number of times the user trades with the partner
        Map<Integer, Integer> partnerToTimes = partnerToTimes(userId);

        // Then we sort the keys of the map according to the corresponding values and return the first few of them
        return frequentPartners(partnerToTimes, mostNumPartners);
    }


    /**
     * To get the frequent trading partners of a user, and returns usernames
     *
     * @param userId          of the user
     * @param mostNumPartners is the number of trade partners wanted to obtain
     * @return usernames of the trading partners
     * @throws IOException if there is an IO error
     */
    public List<String> getFrequentTradingPartnerUsernames(int userId, int mostNumPartners) throws IOException {

        List<Integer> partnerIds = this.getFrequentTradingPartner(userId, mostNumPartners);
        List<String> usernames = new ArrayList<>();
        List<User> users = gateway.get(partnerIds, User.class);
        for (User user : users) {
            usernames.add(user.getName());
        }
        return usernames;
    }


    /**
     * To update the system that an exchange of item has taken place in real life as planned in a meeting
     *
     * @param meetingId of the meeting
     * @throws IOException                      if there is an IO error
     * @throws TransactionDoesNotExistException if a Transaction which does not exist is requested to access
     * @throws PersistenceException             An exception that is thrown during persisting to database.
     */
    public void performMeeting(int meetingId) throws IOException, TransactionDoesNotExistException,
            PersistenceException {

        // Find the transaction that the meeting of the meeting id is in
        List<Integer> transactions = getTransactions();
        ArrayList<Integer> meetingIds = new ArrayList<>();
        int transactionId = -1;
        for (int id : transactions) {
            if (getMeetings(id).contains(meetingId)) {
                transactionId = id;
                meetingIds.addAll(getMeetings(id));
                break;
            }
        }

        //Get all the meetings in the transaction find
        List<Meeting> meetings = gateway.get(meetingIds, Meeting.class);

        //handle permanent transaction, which only has one meeting
        if (meetingIds.size() < 2 && meetingClear(meetings.get(0))) {
            finishTransaction(transactionId);

            //handle temporary transaction which has two meetings
        } else if (meetingClear(meetings.get(0)) && !meetingClear(meetings.get(1))) {
            startTempTransaction(transactionId);

        } else if (meetingClear(meetings.get(0)) && meetingClear(meetings.get(1))) {
            finishTransaction(transactionId);
        }
    }


//Private functions used as helper methods

    /*
     * to indicate that a temporary transaction has been started in real-life by users
     * @param transactionId of the transaction
     * @throws IOException if the data cannot be read from file
     * @throws TransactionDoesNotExistException if the transaction with the input id does not exists
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    private void startTempTransaction(int transactionId) throws IOException, TransactionDoesNotExistException,
            PersistenceException {

        List<Trade> trades = gateway.get(getTrades(transactionId), Trade.class);
        // this is a temporary transaction
        for (Trade trade : trades) {
            List<Item> items = gateway.get(trade.getItemList(), Item.class);
            for (Item item : items) {

                // change holder of the item
                item.changeHolderId(trade.getBorrowerId(), trade.getLenderId());
            }
            gateway.update(items, Item.class);
        }
    }


    /*
     * To indicate that the transaction has been finished in real-life by users
     * @param transactionId of the transaction
     * @throws IOException if the data cannot be read from file
     * @throws TransactionDoesNotExistException if the transaction with the input id does not exixts
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    private void finishTransaction(int transactionId) throws IOException, TransactionDoesNotExistException,
            PersistenceException {

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(transactionId);
        Transaction transaction = gateway.get(ids, Transaction.class).get(0);
        List<Trade> trades = gateway.get(getTrades(transactionId), Trade.class);

        for (Trade trade : trades) {
            List<Item> items = gateway.get(trade.getItemList(), Item.class);
            trade.setComplete(true);
            for (Item item : items) {
                // soft-delete the item if the transaction is permanent
                item.setSoftDelete(transaction.getMeetingList().size() < 2);
                // change holder of the item
                item.changeHolderId(trade.getBorrowerId(), trade.getLenderId());
                // change owner of the item
                item.changeOwnerId(trade.getBorrowerId(), trade.getLenderId());
            }
            gateway.update(items, Item.class);
        }
        gateway.update(trades, Trade.class);
    }


    //Return true iff the transaction is still on going, i.e., not passes the date of the latest meeting
    private boolean onGoing(Transaction transaction) throws IOException {

        List<Meeting> meetings = gateway.get(transaction.getMeetingList(), Meeting.class);
        LocalDate latestDate = meetings.get(0).getTime();
        for (Meeting meeting : meetings) {
            if (meeting.getTime().isAfter(latestDate)) {
                latestDate = meeting.getTime();
            }
        }
        return latestDate.isAfter(LocalDate.now());
    }


    //Return true iff the transaction is complete, i.e., all meetings and trades inside are all complete
    private boolean isComplete(Transaction transaction) throws IOException {
        List<Trade> trades = gateway.get(transaction.getTradeList(), Trade.class);
        for (Trade trade : trades) {
            if (!trade.isComplete()) {
                return false;
            }
        }

        List<Meeting> meetings = gateway.get(transaction.getMeetingList(), Meeting.class);
        for (Meeting meeting : meetings) {
            if (!meeting.isComplete()) {
                return false;
            }
        }
        return true;
    }

    //Return true iff the transaction is created in this week (this week is dynamically determined)
    private boolean thisWeek(Transaction transaction) throws IOException {
        List<Meeting> meetings = gateway.get(transaction.getMeetingList(), Meeting.class);
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        for (Meeting meeting : meetings) {
            if ((meeting.getTime().isAfter(startDate) && meeting.getTime().isBefore(endDate)) ||
                    meeting.getTime().equals(startDate) || meeting.getTime().equals(endDate)) {
                return true;
            }
        }
        return false;
    }

    //Return the transaction object based on transaction id
    private Transaction readTransaction(int transactionId) throws IOException, TransactionDoesNotExistException {
        ArrayList<Integer> transactionIds = new ArrayList<>();
        transactionIds.add(transactionId);
        List<Transaction> transactions = gateway.get(transactionIds, Transaction.class);

        if (transactions.size() != 0) {
            return transactions.get(0);
        } else {
            throw new TransactionDoesNotExistException();
        }
    }

    //Return the ids of the user involved in the meeting
    private List<Integer> getUsers(Transaction transaction) throws IOException {
        ArrayList<Integer> userIds = new ArrayList<>();
        List<Trade> trades = gateway.get(transaction.getTradeList(), Trade.class);
        userIds.add(trades.get(0).getBorrowerId());
        userIds.add(trades.get(0).getLenderId());

        return userIds;
    }

    //Return the trading partners of a user in a transaction
    private List<Integer> getPartners(Transaction transaction, int userId) throws IOException {
        ArrayList<Integer> userIds = new ArrayList<>();
        List<Trade> trades = gateway.get(transaction.getTradeList(), Trade.class);
        if (trades.get(0).getBorrowerId() == userId) {
            userIds.add(trades.get(0).getLenderId());
        } else {
            userIds.add(trades.get(0).getBorrowerId());
        }

        return userIds;
    }


    // Return true iff the meeting is agreed and completed
    private boolean meetingClear(Meeting meeting) {
        return meeting.isComplete() && meeting.isAgree();
    }

    // Return map where the keys are the ids of the trading partner of the user and the values are the
    // number of times the user trades with the partner
    private Map<Integer, Integer> partnerToTimes(int userId) throws IOException {
        Map<Integer, Integer> partnerToTimes = new HashMap<>();
        List<Integer> myTransactions = getTransactionsOf(userId);

        List<Transaction> transactions = gateway.get(myTransactions, Transaction.class);
        for (Transaction transaction : transactions) {
            int num = 1;
            if (!transaction.isOneWay()) {
                num++;
            }
            List<Integer> partners = getPartners(transaction, userId);
            for (int partner : partners) {
                if (!partnerToTimes.containsKey(partner)) {
                    partnerToTimes.put(partner, num);
                } else {
                    partnerToTimes.replace(partner, partnerToTimes.get(partner) + num);
                }
            }
        }

        return partnerToTimes;
    }

    // sort the keys of the map according to the corresponding values and return the first few of them
    private List<Integer> frequentPartners(Map<Integer, Integer> partnerToTimes, int mostNumPartners) {
        ArrayList<Integer> frequentPartners = new ArrayList<>();

        // If the total number of trading partners is smaller than requested, just return all.
        if (partnerToTimes.keySet().size() <= mostNumPartners) {
            frequentPartners.addAll(partnerToTimes.keySet());
            return frequentPartners;
        }

        // we sort to find the first few
        while (frequentPartners.size() < mostNumPartners) {
            int frequency = -1;
            int frequentPartner = -1;
            for (int k : partnerToTimes.keySet()) {
                if (partnerToTimes.get(k) > frequency) {
                    frequentPartner = k;
                    frequency = partnerToTimes.get(k);
                }
            }
            frequentPartners.add(frequentPartner);
            partnerToTimes.remove(frequentPartner);
        }

        return frequentPartners;
    }
}



















