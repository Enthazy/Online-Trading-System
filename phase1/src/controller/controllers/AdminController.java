package controller.controllers;

import controller.presenters.*;
import entities.Item;
import persistence.exceptions.PersistenceException;
import usecases.*;
import usecases.items.ItemQueryBuilder;
import usecases.rules.RuleDoesNotExistException;
import java.io.IOException;
import java.util.*;

public class AdminController extends AbstractBaseController  {

    /**
     * Class Dependencies
     */
    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;


    /**
     * Initializes the class
     * @param tradingFacade TradingFacade
     * @param systemFacade SystemFacade
     * @param controllerPresenter ControllerPresenter
     * @param errorPresenter ErrorPresenter
     */
    public AdminController(TradingFacade tradingFacade, SystemFacade systemFacade,
                          ControllerPresenter controllerPresenter, ErrorPresenter errorPresenter) {
        super(controllerPresenter, errorPresenter);
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
    }


    /**
     * Allows admin to edit the maximum number of transactions a user is allowed per week.
     */
    public void editMaxTransactionsPerWeek() {
        controllerPresenter.get("MaxTransactionPerWeek");
        int option = this.readInt(0,999);
        if(option == -1) {
            controllerPresenter.get("invalidIntegerInput");
            this.editMaxTransactionsPerWeek();
        }
        else systemFacade.config().edit("maxTransactionsPerWeek", option);
    }


    /**
     * Allows admin to edit the threshold value of the maximum incomplete transactions a user is allowed.
     */
    public void editMaxIncompleteTransactions() {
        controllerPresenter.get("MaxIncompleteTransactions");
        int option = this.readInt(0,999);
        if(option == -1) {
            controllerPresenter.get("invalidIntegerInput");
            this.editMaxIncompleteTransactions();
        }
        else systemFacade.config().edit("maxIncompleteTransactions", option);
    }


    /**
     * Allows admin to edit the maximum edits to a meeting that is allowed before the transaction is cancelled.
     */
    public void editMaxMeetings() {
        controllerPresenter.get("editMaxMeetings");
        int option = this.readInt(0,999);
        if(option == -1) {
            controllerPresenter.get("invalidIntegerInput");
            this.editMaxMeetings();
        }
        else systemFacade.config().edit("maxMeetingEdits", option);

    }


    /**
     * Display all the items that need to be approved. The admin can choose from the list and approve or reject.
     */
    public void actionConfirmInventory() {
        try {
            ItemQueryBuilder query = tradingFacade.fetchItems().query().notDeleted().exceptApproved().ownedByUnfrozenUser();

            if(!query.getNames().isEmpty()) {
                controllerPresenter.displayList(query.getNames());
                controllerPresenter.get("ChooseItemToConfirm");
                int option = this.readOption(query.getNames());
                if(option > 0) {
                    Item item = query.getObjects().get(option-1);
                    controllerPresenter.display(item.getName());
                    controllerPresenter.get("WantYesOrNo");
                    if (this.readYesNo() == 1) {
                        tradingFacade.editItems().makeVisible(item.getKey());
                        controllerPresenter.get("AddItemSuccess");
                    }
                }
                else if(option == -1) this.menu.navigateTo("admin");
                else {
                    this.controllerPresenter.get("InvalidOption");
                    this.actionConfirmInventory();
                }
            }
            else controllerPresenter.get("NoItemApprove");
        } catch (IOException | PersistenceException e) {
            errorPresenter.displayPersistenceException();
        }
    }


    /**
     * Displays all the Users that may need to be frozen based on the system suggested rules.
     * The admin then can choose any particular user and freeze him or her.
     */
    public void actionFreezeUser() {
        try {
            List<String> usernames = systemFacade.alert().getFreezeSuggestionsStr();
            if(!usernames.isEmpty()) {
                controllerPresenter.get("FreezeUserPrompt");
                controllerPresenter.displayList(usernames);
                int option = this.readOption(usernames);
                if (option ==0) {
                    controllerPresenter.get("FreezeUserValidInput");
                    this.actionFreezeUser();

                }           else if(option == -1) this.menu.navigateTo("admin");
                else {
                    int id = systemFacade.alert().getFreezeSuggestions().get(option-1);
                    systemFacade.users().freezeUser(id);
                    controllerPresenter.get("FreezeUser");
                    this.menu.navigateTo("admin");
                }
            }
            else controllerPresenter.get("NoFreezeSuggestions");
        } catch (IOException | PersistenceException e) {
            controllerPresenter.display("error");
        } catch (RuleDoesNotExistException e) {
            errorPresenter.displayRuleDoesNotExistException();
        }
    }

    /**
     * Displays all the users who have sent the administrator a request to unfreeze the account.
     * The admin then can choose to unfreeze this particular user or not.
     */
    public void actionUnfreezeUser() {
        try {
            List<String> usernames = systemFacade.alert().getUnfreezeRequestsStr();
            if(usernames.size() != 0) {
                controllerPresenter.displayList(usernames);
                int option = this.readOption(usernames);
                if (option == 0) {
                    controllerPresenter.get("UnfFreezeUserValidInput");
                    this.actionUnfreezeUser();
                }
                else if (option == -1) this.menu.navigateTo("admin");
                else {
                    int id = systemFacade.alert().getUnfreezeRequests().get(option - 1);
                    systemFacade.users().unFreezeUser(id);
                    controllerPresenter.get("UnfreezeUser");
                }
            }
            else controllerPresenter.get("NoUsersFreeze");
        } catch (IOException | PersistenceException e) {
            errorPresenter.displayPersistenceException();
        }
    }


    /**
     * Promotes a normal user to administrator status.
     */
    public void promoteUser() {
        try {
            List<String> users = systemFacade.users().allNonAdminUsernames();
            if (users.size() > 0) {
                controllerPresenter.displayList(users);
                controllerPresenter.get("promoteUserPrompt");
                int option = this.readOption(users);
                if (option > 0) {
                    int id = systemFacade.users().allNonAdmin().get(option - 1).getKey();
                    systemFacade.users().promote(id);
                    controllerPresenter.get("PromoteUser");
                }
                else if(option == -1) {
                    this.menu.navigateTo("admin");
                }
                else {
                    this.controllerPresenter.get("InvalidOption");
                    this.promoteUser();
                }
            }
            else controllerPresenter.get("NoPromoteUser");
        } catch (IOException | PersistenceException e) {
            errorPresenter.displayPersistenceException();
        }
    }


}
