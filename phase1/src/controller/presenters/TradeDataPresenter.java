package controller.presenters;

import controller.exceptions.NoOpenTransactionException;
import entities.Item;
import usecases.TradingFacade;
import usecases.authentication.ListensForUser;
import usecases.trade.exceptions.TransactionDoesNotExistException;
import java.io.*;
import java.time.LocalDate;
import java.util.*;


/**
 * Presents transaction data. Implements ListensForUser so that when the user changes,
 * this class is notified of the change, and can update the userId.
 *
 * @author jessie666444
 */
public class TradeDataPresenter implements ListensForUser {

    /**
     * Class Dependencies
     */
    private final TradingFacade tradingFacade;
    private final ControllerPresenter cp;
    private final ErrorPresenter ep;


    /**
     * Initializes this class.
     * @param tradingFacade TradingFacade
     * @param cp ControllerPresenter
     * @param ep ErrorPresenter
     */
    public TradeDataPresenter(TradingFacade tradingFacade, ControllerPresenter cp, ErrorPresenter ep) {
        this.tradingFacade = tradingFacade;
        this.cp = cp;
        this.ep = ep;
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


    /**
     * Holds the user id of the current logged in user.
     */
    private int userId;


/********************************************************************************************************
 *
 * Public methods
 *
 *********************************************************************************************************/

    /**
     * Updates the user id to the currently logged in user.
     * @param userId The unique key representing a user.
     */
    @Override
    public void updateUserId(int userId) {
        this.userId = userId;
    }


    /**
     * Select a meeting to perform an action.
     * @return -2 to return to the upper menu, otherwise return the meeting ID.
     */
    public int selectMeeting(){
        try{
            openTransactionForSelect();
        } catch (TransactionDoesNotExistException e) {
            ep.displayTransactionDoesNotExistException();
            return -2;
        } catch (IOException e) {
            ep.displayIOException();
            return -2;
        } catch (NoOpenTransactionException e) {
            ep.displayNoOpenTransactionException();
            return -2;
        }
        int meeting = -1;
        cp.get("YourOpenTransaction");
        try {
            String input = br.readLine();
            if(input.equals("exit")) return -2;
            return Integer.parseInt(input);
        } catch (IOException e) {
            ep.displayIOException();
            return -2;
        } catch(NumberFormatException e) {
            cp.get("EnterValidMeetingId");
            return this.selectMeeting();
        }

    }

    /**
     * Display all the corresponding transactions in the <transId>.
     * @param transId An Arraylist of transaction IDs.
     * @throws IOException
     * @throws TransactionDoesNotExistException
     */
    public void displayTransaction(List<Integer> transId) throws IOException, TransactionDoesNotExistException {
        List<Integer> meetings;
        List<Integer> trades;
        int curr = 0;
        while(curr < transId.size()){
            int tranId = transId.get(curr);
            meetings = tradingFacade.manageTransactions().getMeetings(tranId);
            trades = tradingFacade.manageTransactions().getTrades(tranId);
            cp.displayTransactionPresenter(tranId);
            displayMeeting(meetings);
            displayTrade(trades);
            cp.get("DotLine");
            curr++;
        }
    }


/********************************************************************************************************
 *
 * Helper methods.
 *
 *********************************************************************************************************/

    private void openTransactionForSelect() throws IOException, TransactionDoesNotExistException,
            NoOpenTransactionException {
        List<Integer> trans = tradingFacade.manageTransactions().getOpenTransactionsOf(userId);
        if(trans.isEmpty()) throw new NoOpenTransactionException();
        displayTransaction(trans);
    }


    private void displayTrade(List<Integer> trades) throws IOException {
        int curr = 0;
        while(curr < trades.size()){
            int tID = trades.get(curr);
            int lender = tradingFacade.manageTrades().getLenderId(tID);
            int borrower = tradingFacade.manageTrades().getBorrowerId(tID);
            List<Integer> items = tradingFacade.manageTrades().getItemIds(tID);
            Item item = tradingFacade.fetchItems().query().findById(items.get(0)).getObject();
            cp.displayTradePresenter(tID, lender, borrower, item.getName(), item.getDescription());
            curr++;
        }
    }

    private void displayMeeting(List<Integer> meetings) throws IOException {
        int curr = 0;
        while(curr < meetings.size()){
            int mID = meetings.get(curr);
            LocalDate date = tradingFacade.manageMeetings().getMeetingDate(mID);
            String location = tradingFacade.manageMeetings().getMeetingLocation(mID);
            cp.displayMeetingPresenter(mID, date, location);
            curr++;
        }
    }


}
