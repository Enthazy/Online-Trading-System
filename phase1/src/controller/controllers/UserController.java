package controller.controllers;

import controller.presenters.*;
import entities.*;
import persistence.exceptions.*;
import usecases.*;
import usecases.items.*;
import java.io.*;
import java.util.*;


/**
 * This class holds every possible action invoked from the Menu, at any level.
 */
public class UserController extends AbstractBaseController  {

    /**
     * Class Dependencies
     */
    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;

    /**
     * Create an User Controller
     *
     * @param tradingFacade
     * @param systemFacade
     * @param controllerPresenter
     * @param errorPresenter
     */
    public UserController(TradingFacade tradingFacade, SystemFacade systemFacade,
                          ControllerPresenter controllerPresenter, ErrorPresenter errorPresenter) {
        super(controllerPresenter, errorPresenter);
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
    }


    /**
     * Shows a user's inventory.
     */
    public void showMyInventory() {

        try {
            List<String> items = tradingFacade.fetchItems().query().onlyApproved()
                                                                    .notDeleted()
                                                                    .onlyOwnedBy(this.userId)
                                                                    .heldByOwner()
                                                                    .getNames();
            if(!items.isEmpty()) controllerPresenter.displayList(items);
            else controllerPresenter.get("NoItemInventory");
        } catch (IOException e) {
            errorPresenter.displayIOException();
        }

    }


    /**
     * Views inventory available for trading.
     */
    public void viewInventory() {

        try {
            List<String> items = tradingFacade.fetchItems().query().onlyApproved()
                                                                    .notDeleted()
                                                                    .exceptOwnedBy(this.userId)
                                                                    .heldByOwner()
                                                                    .ownedByUnfrozenUser()
                                                                    .getNames();
            if(!items.isEmpty()) controllerPresenter.displayList(items);
            else this.controllerPresenter.get("noAvailableItems");
        } catch (IOException e) {
            errorPresenter.displayIOException();
        }

    }


    /**
     * Handles the action of adding an item to one's inventory. The user must enter a non-blank value.
     */
    public void addItemToInventory() {

        try {
            String itemName = "";
            while(itemName.equals("")) {
                controllerPresenter.get("addNewItem");
                itemName = this.readString();
                if(itemName.equals("")) controllerPresenter.get("nonBlankValue");
            }

            String description = "";
            while(description.equals("")) {
                controllerPresenter.get("AddDescription");
                description = this.readString();
                if(description.equals("")) controllerPresenter.get("nonBlankValue");
            }

            tradingFacade.editItems().addItemToInventory(itemName, description, userId);
            controllerPresenter.get("AddedItem");

        } catch (IOException | PersistenceException e) {
            controllerPresenter.get("NotAddedItem");
        }

    }


    /**
     * Updates a user's password.
     */
    public void updatePassword() {

        try {
            controllerPresenter.get("newpassword");
            String pwd = this.readString();
            if (systemFacade.users().changePassword(pwd, this.userId)) {
                controllerPresenter.get("successpassword");
            }
            else controllerPresenter.get("failpassword");

        } catch (PersistenceException | IOException e) {
            errorPresenter.displayIOException();
        }
    }


    /**
     * Requests an unfreeze of the logged in user's account.
     */
    public void requestUnfreeze() {

        try {
            controllerPresenter.get("UnfreezeReq");
            controllerPresenter.get("YesNo");

            int option = this.readYesNo();

            //Handles incorrect input
            if (option == 0) {
                controllerPresenter.get("WantYesOrNo");
                this.requestUnfreeze();
            }
            //Handles yes
            else if (option == 1) {
                systemFacade.users().requestUnfreeze(userId);
                controllerPresenter.get("RequestSend");
            }
            //Do nothing if the user says no
            //else if (option == 2) {}

        } catch (IOException | PersistenceException e) {
            errorPresenter.displayIOException();
        }
    }


    /**
     * Adds an item to wishlist.
     */
    public void addToWishlist() {
        try {
            ItemQueryBuilder query = tradingFacade.fetchItems().query().onlyApproved()
                                                                .notDeleted()
                                                                .exceptOwnedBy(userId)
                                                                .notInWishlistOf(userId)
                                                                .heldByOwner()
                                                                .ownedByUnfrozenUser();

            if(!query.getNames().isEmpty()) {
                controllerPresenter.get("SelectOptionMessage");
                int option = selectItemList(query.getNames());
                if (option > 0){
                    int itemId = query.getIds().get(option-1);
                    addToWishlistSelect(itemId);
                }
            }
            else controllerPresenter.get("NoItemOther");
        } catch (IOException | PersistenceException e) {
            errorPresenter.displayPersistenceException();
        }
    }


    /**
     * Shows a user's wishlist.
     */
    public void showMyWishlist() {
        try {
            ItemQueryBuilder items = tradingFacade.fetchItems().query().onlyApproved()
                                                .notDeleted()
                                                .exceptOwnedBy(userId)
                                                .inWishlistOf(userId)
                                                .heldByOwner()
                                                .ownedByUnfrozenUser();

            if(!items.getNames().isEmpty()) {
                int option = selectItemList(items.getNames());
                if (option > 0) {
                    int itemId = items.getIds().get(option-1);
                    showMyWishlistSelect(itemId);
                }
            }
            else controllerPresenter.get("NoItemWishlist");
        }catch (IOException | PersistenceException e) {
            errorPresenter.displayPersistenceException();
        }
    }

/********************************************************************************************************
 *
 * Helper methods
 *
 *********************************************************************************************************/


    private void addToWishlistSelect(int itemId) throws IOException, PersistenceException {
        showItemInfo(itemId);
        controllerPresenter.get("AddToWishlist");
        controllerPresenter.get("WantYesOrNo");
        if (this.readYesNo() == 1) {
            tradingFacade.editItems().addItemToWishlist(itemId, userId);
            controllerPresenter.get("AddItemSuccess");
        }
    }


    private void showItemInfo(int itemId) throws IOException {
        Item item = tradingFacade.fetchItems().query().findById(itemId).getObject();
        controllerPresenter.displayItem(item.getName(), item.getDescription());
    }


    private void showMyWishlistSelect(int itemId) throws IOException, PersistenceException {
        showItemInfo(itemId);
        controllerPresenter.get("Do you want to remove this item from the wishlist?");
        controllerPresenter.get("WantYesOrNo");
        if (this.readYesNo() == 1) {
            tradingFacade.editItems().removeItemFromWishlist(itemId, userId);
            controllerPresenter.get("RemoveItemSuccess");
        }
    }


    private int selectItemList(List<String> items) {
        if (items.isEmpty()) return -2;
        controllerPresenter.displayList(items);
        controllerPresenter.get("ChooseItemToRemoveFromWishlist");
        try {
            int option = this.readOption(items);
            if (option == -1) return -1;//Means back to upper menu
            if (option == 0) return selectItemList(items);
            else return option;
        } catch (IOException e) {
            errorPresenter.displayIOException();
            return selectItemList(items);
        }
    }


}
