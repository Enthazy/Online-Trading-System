package controller;

public class MenuBuilder {

    /**
     * Holds the Menu
     */
    private Menu menu;

    /**
     * Initializes this class.
     * @param menu Menu
     */
    public MenuBuilder(Menu menu) {
        this.menu = menu;
    }

    /**
     * Builds a menu for the user to navigate.
     * @return Menu
     */
    public Menu build() {
        menu.navigateTo("root");

        menu.create("register", "Register Menu").setAction(menu.controller.auth()::register).attachParent("root");
        menu.create("login", "Login Menu").setAction(menu.controller.auth()::login).attachParent("root");
        menu.create("exit", "Exit Program").setAction(menu.controller.auth()::exit).attachParent("root");

        menu.create("inventory", "Inventory Menu").attachParent("login");
        menu.create("wishlist", "Wishlist Menu").attachParent("login");
        menu.create("transaction", "Transaction Menu").attachParent("login");
        menu.create("admin", "Admin Menu").attachParent("login").setVisibility("isAdmin");
        menu.create("setting", "Setting").attachParent("login");
        menu.create("logout", "Log Out").setAction(menu.controller.auth()::logout).attachParent("login");

        menu.create("showMyInventory", "Show My Inventory").attachParent("inventory").setAction(menu.controller.user()::showMyInventory);
        menu.create("addItem", "Add Item to Inventory").attachParent("inventory").setAction(menu.controller.user()::addItemToInventory).setVisibility("isNotFrozen");
        menu.create("viewAvailableItems", "View Available Items").attachParent("inventory").setAction(menu.controller.user()::viewInventory);


        menu.create("addToWishlist", "Add Item To Wishlist").attachParent("wishlist").setAction(menu.controller.user()::addToWishlist).setVisibility("isNotFrozen");
        menu.create("showMyWishlist", "Show My Wishlist").attachParent("wishlist").setAction(menu.controller.user()::showMyWishlist);

        menu.create("updatePassword", "Update Password").attachParent("setting").setAction(menu.controller.user()::updatePassword);
        menu.create("requestUnfreeze", "Request Unfreeze of Account").attachParent("setting").setAction(menu.controller.user()::requestUnfreeze).setVisibility("isFrozen");

        menu.create("startNewTransaction", "Start A Transaction").attachParent("transaction").setAction(menu.controller.transaction()::viewInventoryWithTrading).setVisibility("showTrade");
        menu.create("viewOpenTransactions", "View Open Transactions").attachParent("transaction").setAction(menu.controller.transaction()::openTransactions);
        menu.create("viewTransactionHistory", "View My Transaction History").attachParent("transaction").setAction(menu.controller.transaction()::transHistory);
        menu.create("viewFrequentPartner", "View Most Frequent Trading Partners").attachParent("transaction").setAction(menu.controller.transaction()::frequentPartners);

        menu.create("oneWay", "Initiate a one-way trade").attachParent("startNewTransaction").setAction(menu.controller.transaction()::oneWay).setVisibility("canBorrow");
        menu.create("twoWay", "Initiate a two-way trade").attachParent("startNewTransaction").setAction(menu.controller.transaction()::twoWay);

        menu.create("permanent", "Make Trade Permanent").attachParent("oneWay").setAction(menu.controller.transaction()::permanent);
        menu.create("temporary", "Make Trade Temporary").attachParent("oneWay").setAction(menu.controller.transaction()::temporary);
        menu.create("permanent", "Make Trade Permanent").attachParent("twoWay").setAction(menu.controller.transaction()::permanent);
        menu.create("temporary", "Make Trade Temporary").attachParent("twoWay").setAction(menu.controller.transaction()::temporary);

        menu.create("meeting", "Edit & Confirm Meetings").attachParent("transaction");
        menu.create("edit", "Edit a Meeting").attachParent("meeting").setAction(menu.controller.meeting()::editMeeting).setVisibility("isNotFrozen");
        menu.create("agree", "Agree To a Meeting").attachParent("meeting").setAction(menu.controller.meeting()::agreeMeeting).setVisibility("isNotFrozen");
        menu.create("confirm", "Confirm a Meeting Has Taken Place").attachParent("meeting").setAction(menu.controller.meeting()::confirmMeeting);

        menu.create("freeze", "Freeze a User").attachParent("admin").setAction(menu.controller.admin()::actionFreezeUser).setVisibility("isAdmin");;
        menu.create("viewUnfreeze", "View Unfreeze Requests from Users").attachParent("admin").setAction(menu.controller.admin()::actionUnfreezeUser).setVisibility("isAdmin");;
        menu.create("approveItem", "Approve New Items").attachParent("admin").setAction(menu.controller.admin()::actionConfirmInventory).setVisibility("isAdmin");;
        menu.create("makeAdmin", "Promote a User to Admin").attachParent("admin").setVisibility("isAdmin").setAction(menu.controller.admin()::promoteUser);
        menu.create("changeConfig", "Change Configuration").attachParent("admin").setVisibility("isAdmin");

        menu.create("editMaxMeetings", "Edit Max Meeting Edits").attachParent("changeConfig").setVisibility("isAdmin").setAction(menu.controller.admin()::editMaxMeetings);
        menu.create("editMaxIncompleteTransaction", "Edit Max Incomplete Transactions").attachParent("changeConfig").setVisibility("isAdmin").setAction(menu.controller.admin()::editMaxIncompleteTransactions);
        menu.create("editMaxTransactionsPerWeek", "Edit Max Transactions Per Week").attachParent("changeConfig").setVisibility("isAdmin").setAction(menu.controller.admin()::editMaxTransactionsPerWeek);

        return menu;

    }


}
