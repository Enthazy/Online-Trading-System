package usecases;

import usecases.items.*;
import usecases.meeting.*;
import usecases.trade.*;

/**
 * A facade for trading-related use case classes.
 */
public class TradingFacade {

    /**
     * Class dependencies
     */
    private final ItemEditor itemEditor;
    private final ItemFetcher itemFetcher;
    private final MeetingFactory meetingFactory;
    private final MeetingManager meetingManager;
    private final TradeFactory makeTrades;
    private final TradeManager manageTrades;
    private final TransactionManager manageTransactions;


    /**
     * Initializes this class.
     * @param itemEditor ItemEditor
     * @param itemFetcher ItemFetcher
     * @param meetingFactory MeetingFactory
     * @param meetingManager MeetingManager
     * @param makeTrades ItemEditor
     * @param manageTrades TradeManager
     * @param manageTransactions TransactionManager
     */
    public TradingFacade(ItemEditor itemEditor, ItemFetcher itemFetcher, MeetingFactory meetingFactory, MeetingManager meetingManager,
                         TradeFactory makeTrades, TradeManager manageTrades, TransactionManager manageTransactions) {
        this.itemEditor = itemEditor;
        this.itemFetcher = itemFetcher;
        this.meetingFactory = meetingFactory;
        this.meetingManager = meetingManager;
        this.makeTrades = makeTrades;
        this.manageTrades = manageTrades;
        this.manageTransactions = manageTransactions;
    }

    /**
     * Returns an instance of ItemFetcher.
     * @return Returns an instance of ItemFetcher.
     */
    public ItemFetcher fetchItems() {
        return this.itemFetcher;
    }

    /**
     * Returns an instance of ItemEditor.
     * @return Returns an instance of ItemEditor.
     */
    public ItemEditor editItems() {
        return this.itemEditor;
    }

    /**
     * Returns an instance of MeetingFactory.
     * @return Returns an instance of MeetingFactory.
     */
    public MeetingFactory makeMeetings() {
        return this.meetingFactory;
    }

    /**
     * Returns an instance of MeetingManager.
     * @return Returns an instance of MeetingManager.
     */
    public MeetingManager manageMeetings() {
        return this.meetingManager;
    }

    /**
     * Returns an instance of TradeFactory.
     * @return Returns an instance of TradeFactory.
     */
    public TradeFactory makeTrades() {
        return this.makeTrades;
    }

    /**
     * Returns an instance of TradeManager.
     * @return Returns an instance of TradeManager.
     */
    public TradeManager manageTrades() {
        return this.manageTrades;
    }

    /**
     * Returns an instance of TransactionManager.
     * @return Returns an instance of TransactionManager.
     */
    public TransactionManager manageTransactions() {
        return this.manageTransactions;
    }


}
