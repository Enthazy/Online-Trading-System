package controller.controllers;

import controller.presenters.*;
import controller.presenters.transaction.*;
import entities.*;
import persistence.exceptions.*;
import usecases.*;
import usecases.items.*;
import usecases.meeting.exceptions.*;
import usecases.trade.exceptions.*;
import java.io.*;
import java.time.*;
import java.util.*;

public class TransactionController extends AbstractBaseController {

    /**
     * Class Dependencies
     */
    private final TradingFacade tradingFacade;
    private final TradeDataPresenter tradeDataPresenter;

    /**
     * Initializes this class
     * @param tradingFacade TradingFacade
     * @param tradeDataPresenter TradeDataPresenter
     * @param controllerPresenter ControllerPresenter
     * @param errorPresenter ErrorPresenter
     */
    public TransactionController(TradingFacade tradingFacade, TradeDataPresenter tradeDataPresenter, ControllerPresenter controllerPresenter,
                                 ErrorPresenter errorPresenter) {
        super(controllerPresenter, errorPresenter);
        this.tradingFacade = tradingFacade;
        this.tradeDataPresenter = tradeDataPresenter;
    }


    /**
     * Holds how many frequent partners to display.
     */
    public int frequentPartnerCount = 3;


    /**
     * Holds the item that a user wants to trade
     */
    private Item selectedItem;


    /**
     * View inventory to start a trade.
     */
    public void viewInventoryWithTrading() {

        try {

            ItemQueryBuilder itemQueryBuilder = tradingFacade.fetchItems().query().notDeleted().onlyApproved()
                                                                                    .exceptOwnedBy(this.userId)
                                                                                    .heldByOwner()
                                                                                    .ownedByUnfrozenUser();

            if(!itemQueryBuilder.getNames().isEmpty()) {
                controllerPresenter.get("ChooseFromInventoryPrompt");
                controllerPresenter.displayList(itemQueryBuilder.getNames());
                controllerPresenter.get("SelectOptionMessage");
                int option = this.readOption(itemQueryBuilder.getNames());
                if (option == -1) this.menu.navigateTo("transaction");
                else if (option == 0) {
                    controllerPresenter.get("EnterValidMessage");
                    viewInventoryWithTrading();
                }
                else {
                    this.selectedItem = itemQueryBuilder.getObjects().get(option-1);
                    this.menu.navigateTo("startNewTransaction");
                }
            }
            else {
                this.controllerPresenter.get("noAvailableItems");
                this.menu.navigateTo("transaction");
            }
        } catch (IOException e) {
            this.errorPresenter.displayIOException();
        }

    }


    /**
     * Handles the user choice of a one-way transaction.
     */
    public void oneWay() {
        try {
            startTransaction();
            tradingFacade.makeTrades().oneWay();
        } catch (TooManyItemListsException e) {
            this.errorPresenter.programError();
        }

    }


    /**
     * Handles the user choice of a two-way transaction.
     */
    public void twoWay() {
        try {
            int theOther = startTransaction();
            int item2 = selectItemWishlist(theOther);
            if (item2 == -2 || item2 == -1) {
                controllerPresenter.get("noWishListOneWayTradeOnly");
                this.menu.navigateTo("startNewTransaction");
            }
            else {
                List<Integer> items2 = new ArrayList<>(Collections.singletonList(item2));
                tradingFacade.makeTrades().twoWay().fillItems(items2);
            }
        } catch (TooManyItemListsException e) {
            this.errorPresenter.displayTooManyItemListsException();
        }
    }


    /**
     * Handles the user choice of a permanent transaction.
     */
    public void permanent() {
        try {
            List<String> input = this.gatherInput(new PermanentMeetingIterator());
            LocalDate date = LocalDate.parse(input.get(1));
            tradingFacade.makeMeetings().permanent().fillTime(date).fillLocation(input.get(0)).setCurrentSuggestionMaker(userId);
            initTransaction(tradingFacade.makeTrades().getTradeId(), tradingFacade.makeMeetings().getMeetingId());
            this.menu.navigateTo("inventory");
        } catch (TooManyTimesException | TooManyLocationsException e) {
            this.errorPresenter.programError();
        } catch (IOException | PersistenceException e) {
            this.errorPresenter.displayIOException();
        } catch (TransactionDoesNotExistException e) {
            this.errorPresenter.displayTransactionDoesNotExistException();
        }
    }


    /**
     * Handles the user choice of a temporary transaction.
     */
    public void temporary() {

        try {
            List<String> input = this.gatherInput(new TempMeetingIterator());
            LocalDate date = LocalDate.parse(input.get(2));

            tradingFacade.makeTrades();
            tradingFacade.makeMeetings().temporary().fillTime(date).
                    fillLocation(input.get(0)).fillLocation(input.get(1)).setCurrentSuggestionMaker(userId);

            initTransaction(tradingFacade.makeTrades().getTradeId(), tradingFacade.makeMeetings().getMeetingId());
            this.menu.navigateTo("inventory");

        }  catch (TooManyLocationsException | TooManyTimesException e) {
            this.errorPresenter.programError();
        }  catch (IOException | PersistenceException e) {
            this.errorPresenter.displayIOException();
        } catch (TransactionDoesNotExistException e) {
            this.errorPresenter.displayTransactionDoesNotExistException();
        }

    }


    /**
     * Present all open transactions of the user.
     */
    public void openTransactions() {
        try {
            List<Integer> trans = tradingFacade.manageTransactions().getOpenTransactionsOf(userId);
            if(trans.size() !=0) tradeDataPresenter.displayTransaction(trans);
            else controllerPresenter.get("NoOpenTransaction");
        } catch (IOException | TransactionDoesNotExistException e) {
            this.errorPresenter.displayTransactionDoesNotExistException();
        }
    }


    /**
     * Present all the transactions of the user.
     */
    public void transHistory(){
        try {
            List<Integer> trans = tradingFacade.manageTransactions().getTransactionsOf(userId);
            if(trans.size() !=0) tradeDataPresenter.displayTransaction(trans);
            else controllerPresenter.get("NoTransactionHistory");
        } catch (IOException | TransactionDoesNotExistException e) {
            this.errorPresenter.displayTransactionDoesNotExistException();
        }
    }


    /**
     * Present top three most frequent trading partners.
     */
    public void frequentPartners() {
        try {
            List<String> users = tradingFacade.manageTransactions().
                    getFrequentTradingPartnerUsernames(userId, this.frequentPartnerCount);
            if(users.size() != 0) {
                controllerPresenter.display("Usernames are shown below.");
                int curr = 0;
                while (curr < users.size()) {
                    String name = users.get(curr);
                    controllerPresenter.display("NO." + (curr +1) + ": " + name + ".");
                    curr++;
                }
            }
            else controllerPresenter.get("NoTrade");
        } catch (IOException e) {
            this.errorPresenter.displayIOException();
        }
    }





/********************************************************************************************************
 *
 * Helper methods
 *
 *********************************************************************************************************/

    private int startTransaction() throws TooManyItemListsException {
        tradingFacade.makeTrades().reset();
        tradingFacade.makeMeetings().reset();
        List<Integer> items = new ArrayList<>(Collections.singletonList(selectedItem.getKey()));
        tradingFacade.makeTrades().fillLenderId(selectedItem.getOwnerId()).fillBorrowId(userId).fillItems(items);
        return selectedItem.getOwnerId();
    }

    private void initTransaction(List<Integer> tradeIds, List<Integer> meetingIds) throws IOException,
            PersistenceException, TransactionDoesNotExistException {
        int transID = tradingFacade.manageTransactions().initiateTransaction(tradeIds, meetingIds);
        List<Integer> trans = new ArrayList<>();
        trans.add(transID);
        tradeDataPresenter.displayTransaction(trans);

    }


    /**
     * Take a userId
     * Display its Wishlist
     * Let Client select an item
     * return the itemId
     * @param userId target wishlist
     * @return -2: No wishlist, -1: Back to upper menu (end the selection),0: refresh, otherwise: itemId
     */
    private int selectItemWishlist(int userId) {

        try {

            ItemQueryBuilder items = tradingFacade.fetchItems().query().onlyApproved().notDeleted()
                    .onlyOwnedBy(this.userId)
                    .inWishlistOf(userId)
                    .heldByOwner()
                    .ownedByUnfrozenUser();

            if(!items.getObjects().isEmpty()) {
                controllerPresenter.displayList(items.getNames());
                controllerPresenter.get("TwoWayTrade");
                int option = this.readOption(items.getNames());
                if (option == -1) return -1; //Go up a level
                else if (option == 0) return selectItemWishlist(userId); //Invalid selection
                else return items.getIds().get(option-1);
            }
        }
        catch (IOException e) {
            errorPresenter.displayIOException();
            return selectItemWishlist(userId);
        }

        return -2; //Means the wishlist does not exist

    }


}
