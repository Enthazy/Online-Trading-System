package usecases.rules;

import entities.Trade;
import persistence.PersistenceInterface;
import usecases.trade.TransactionManager;
import usecases.trade.exceptions.TransactionDoesNotExistException;

import java.io.IOException;
import java.util.List;


/**
 * Checks whether a user violates one of the rules
 */
public class RuleValidator {

    /**
     * Class dependencies
     */
    private TransactionManager transactionManager;
    private PersistenceInterface gateway;

    /**
     * Constructor to create an instance of RuleValidator
     *
     * @param transactionManager is an instance of TransactionManager
     * @param gateway to access stored data
     */
    public RuleValidator(TransactionManager transactionManager, PersistenceInterface gateway) {
        this.transactionManager = transactionManager;
        this.gateway = gateway;
    }

    /**
     * Check whether the input system rule is violate by the input user
     *
     * @param rule   which is one of the system rule needs to be checked
     * @param userId if the id of the user who needs to be checked
     * @return true if and only if the input user violates the input rule
     * @throws IOException               if there is an IO error
     * @throws RuleDoesNotExistException if the input system rule does not exist
     */
    public boolean violate(SystemRule rule, int userId) throws IOException, RuleDoesNotExistException {

        switch (rule.getRule()) {
            case "NoMoreBorrowThanLend":
                return moreBorrowThanLend(userId, rule.restriction());

            case "MaxTransactionPerWeek":
                return tooManyTransactionsPerWeek(userId, rule.restriction());

            case "MaxIncompleteTransaction":
                return tooManyIncompleteTransactions(userId, rule.restriction());
        }
        throw new RuleDoesNotExistException();
    }


    // private methods used as helper methods

    //Return true iff the user conducts too many transaction per week
    private boolean tooManyTransactionsPerWeek(int userId, int maxTransactionAllow) throws IOException {
        return transactionManager.getWeeklyTransactionsOf(userId).size() >= maxTransactionAllow;
    }

    /**
     * condition for Too many incomplete transactions
     * @param userId id of the user
     * @param maxIncompleteTransactionAllow the value of maximum of incomplete transaction that is allowed
     * @return true iff the user has too many incomplete transactions
     * @throws IOException
     */
    public boolean tooManyIncompleteTransactions(int userId, int maxIncompleteTransactionAllow) throws IOException {
        return transactionManager.getIncompleteTransactionsOf(userId).size() >= maxIncompleteTransactionAllow;
    }


    /**
     * condition to check in the user borrowed more than lend
     * @param userId
     * @param threshold
     * @return true iff the user borrows more items than lends
     * @throws IOException
     */
    public boolean moreBorrowThanLend(int userId, int threshold) throws IOException {
        try {

            int borrow = 0;
            int lend = 0;
            List<Integer> transactionIds = transactionManager.getTransactionsOf(userId);

            for (int transactionId : transactionIds) {
                if (transactionManager.getTrades(transactionId).size() == 1) {
                    List<Trade> trades = gateway.get(transactionManager.getTrades(transactionId), Trade.class);
                    if (trades.get(0).getBorrowerId() == userId) {
                        borrow++;
                    } else {
                        lend++;
                    }
                }
            }
            return borrow - lend > threshold;
        } catch (TransactionDoesNotExistException e) {
            return false;
        }
    }
}
