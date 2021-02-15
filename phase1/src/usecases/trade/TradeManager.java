package usecases.trade;

import entities.Trade;
import persistence.PersistenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A use case for Trade, which deals with all operations relating to Trade except instantiation.
 */
public class TradeManager {
    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;


    /**
     * Instantiate a TradeManager
     *
     * @param gateway to access the file
     */
    public TradeManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }


    /**
     * To get id of the lender of the trade
     *
     * @param tradeId of the trade
     * @return id of the lender of the trade
     * @throws IOException if there is an IO error
     */
    public int getLenderId(int tradeId) throws IOException {
        Trade trade = getTrade(tradeId);
        return trade.getLenderId();
    }


    /**
     * To get id of the borrow of the trade
     *
     * @param tradeId of the trade
     * @return id of the borrow of the trade
     * @throws IOException if there is an IO error
     */
    public int getBorrowerId(int tradeId) throws IOException {
        Trade trade = getTrade(tradeId);
        return trade.getBorrowerId();
    }


    /**
     * To get id of the items of the trade
     *
     * @param tradeId of the trade
     * @return id of the items of the trade
     * @throws IOException if there is an IO error
     */
    public List<Integer> getItemIds(int tradeId) throws IOException {
        Trade trade = getTrade(tradeId);
        return trade.getItemList();
    }


    //Private method used as helper method, which returns trade object based on trade id
    private Trade getTrade(int tradeId) throws IOException {
        List<Integer> ids = new ArrayList<>();
        ids.add(tradeId);
        return gateway.get(ids, Trade.class).get(0);
    }
}
