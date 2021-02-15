package controller.controllers;

import controller.presenters.*;
import controller.presenters.transaction.*;
import persistence.exceptions.*;
import usecases.TradingFacade;
import usecases.meeting.exceptions.*;
import usecases.trade.exceptions.TransactionDoesNotExistException;
import java.io.*;
import java.time.*;
import java.util.*;

public class MeetingController extends AbstractBaseController {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final TradeDataPresenter tradeDataPresenter;

    /**
     * Initializes the class.
     * @param tradingFacade TradingFacade
     * @param tradeDataPresenter TradeDataPresenter
     * @param controllerPresenter ControllerPresenter
     * @param errorPresenter ErrorPresenter
     */
    public MeetingController(TradingFacade tradingFacade, TradeDataPresenter tradeDataPresenter, ControllerPresenter controllerPresenter,
                                 ErrorPresenter errorPresenter) {
        super(controllerPresenter, errorPresenter);
        this.tradingFacade = tradingFacade;
        this.tradeDataPresenter = tradeDataPresenter;
    }

    /**
     * Edits a meeting proposal.
     */
    public void editMeeting() {
        int meetingID = tradeDataPresenter.selectMeeting();
        try{
            if (meetingID == -2) {
                this.menu.navigateTo("transaction");
            } else if(meetingID == -1){
                controllerPresenter.get("SomethingWentWrong");
                this.menu.navigateTo("meeting");
            }
            else if (!meetingExist(meetingID)) {
                controllerPresenter.get("WrongMeetingId");
                this.menu.navigateTo("meeting");
            } else if(tradingFacade.manageMeetings().getCurrentSuggestionMaker(meetingID) == userId){
                controllerPresenter.get("WaitForOther");
                this.menu.navigateTo("meeting");
            }
            else {
                List<String> input = this.gatherInput(new PermanentMeetingIterator());
                LocalDate date = LocalDate.parse(input.get(1));
                String location = input.get(0);
                if (tradingFacade.manageMeetings().userEditMeetingSuggestion(userId, meetingID, location, date)){
                    controllerPresenter.get("EditSuccess");
                } else {
                    controllerPresenter.get("SecondMeetingEditSuccess");
                }
            }
        } catch (IOException e) {
            errorPresenter.displayIOException();
        } catch (TooManyEditionsException e) {
            errorPresenter.displayTooManyEditionsException();
        } catch (TransactionDoesNotExistException e) {
            errorPresenter.displayTransactionDoesNotExistException();
        } catch (EditAgreedMeetingException e){
            errorPresenter.displayEditAgreedMeetingException();
        }
    }


    /**
     * Agrees to a meeting request.
     */
    public void agreeMeeting(){
        int meetingID = tradeDataPresenter.selectMeeting();
        try {
            if (meetingID == -2) {
                this.menu.navigateTo("transaction");
            } else if(meetingID == -1){
                controllerPresenter.get("SomethingWentWrong");
                this.menu.navigateTo("meeting");
            } else if (!meetingExist(meetingID)) {
                controllerPresenter.get("WrongMeetingId");
                this.menu.navigateTo("meeting");
            }else if(tradingFacade.manageMeetings().getCurrentSuggestionMaker(meetingID) == userId){
                controllerPresenter.get("ConfirmMeeting");
                this.menu.navigateTo("meeting");
            }
            else {
                tradingFacade.manageMeetings().toAgree(meetingID);
                controllerPresenter.get("AgreeMeeting");
            }
        } catch (IOException e) {
            errorPresenter.displayIOException();
        } catch (TransactionDoesNotExistException e) {
            errorPresenter.displayTransactionDoesNotExistException();
        }
    }

    /**
     * Confirms a meeting.
     */
    public void confirmMeeting() {

        //incomplete transaction: any meeting not confirmed after the meeting date, then the transaction is incomplete.
        int meetingID = tradeDataPresenter.selectMeeting();
        try {
            if (meetingID == -2) {
                this.menu.navigateTo("transaction");
            } else if(meetingID == -1){
                controllerPresenter.get("SomethingWentWrong");
                this.menu.navigateTo("meeting");
            } else if (!meetingExist(meetingID)) {
                controllerPresenter.get("WrongMeetingId");
                this.menu.navigateTo("meeting");
            }else if(! tradingFacade.manageMeetings().checkIsAgree(meetingID)){
                controllerPresenter.get("NotAgreed");
                this.menu.navigateTo("meeting");
            } else if(tradingFacade.manageMeetings().checkIsConducted(meetingID, userId)){
                controllerPresenter.get("ConfirmedBefore");
                this.menu.navigateTo("meeting");
            }
            else {
                tradingFacade.manageMeetings().toMarkConducted(meetingID, userId);
                controllerPresenter.get("ConfirmMeeting");
                // once a user has confirm to a meeting, he/she confirms that the necessary change of item is made
                tradingFacade.manageTransactions().performMeeting(meetingID);
            }
        } catch (IOException e) {
            errorPresenter.displayIOException();

            //caused by perform meeting
        } catch (PersistenceException e) {
            errorPresenter.displayPersistenceException();
        } catch (TransactionDoesNotExistException e) {
            errorPresenter.displayTransactionDoesNotExistException();
        }
    }


    private boolean meetingExist(int meeting) throws IOException, TransactionDoesNotExistException {
        List<Integer> openTransaction = tradingFacade.manageTransactions().getOpenTransactionsOf(userId);
        List<Integer> meetings = new ArrayList<>();
        int curr = 0;
        while(curr < openTransaction.size()) {
            int trans = openTransaction.get(curr);
            List<Integer> temp = tradingFacade.manageTransactions().getMeetings(trans);
            int curr2 = 0;
            while(curr2 < temp.size()){
                meetings.add(temp.get(curr2));
                curr2++;
            }
            curr++;
        }
        return meetings.contains(meeting);
    }



}
