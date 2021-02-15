package controller;

import controller.controllers.*;
import controller.presenters.ControllerPresenter;
import controller.presenters.ErrorPresenter;
import usecases.access.ProvidesAccess;
import java.io.IOException;
import java.util.*;

public class Menu extends AbstractBaseController {


    /**
     * Dependencies
     */
    protected ControllerFacade controller;
    protected ProvidesAccess accessesMonitor;

    /**
     * Creates a new Menu.
     * @param controller
     * @param accessesMonitor
     * @param cp
     * @param ep
     */
    public Menu(ControllerFacade controller, ProvidesAccess accessesMonitor, ControllerPresenter cp, ErrorPresenter ep) {
        super(cp, ep);
        controller.user().setMenu(this);
        controller.auth().setMenu(this);
        controller.transaction().setMenu(this);
        controller.meeting().setMenu(this);
        controller.admin().setMenu(this);

        this.controller = controller;
        this.accessesMonitor = accessesMonitor;

    }

/********************************************************************************************************
 *
 * Menu Structure
 *
 *********************************************************************************************************/

/**
     * A list of key value pairs representing menu items/nodes.
     */
    protected Map<String, String> menu = new HashMap<String, String>();

    /**
     * A list of "tuples", with the first entry being the parent key of a menu item,
     * and the second entry being a child key of a menu item.
     */
    protected List<List<String>> parentChild = new ArrayList<List<String>>();


    /**
     * A map of unique menu keys to their associated actions when a user selects this item.
     */
    protected Map<String, Runnable> runnable = new HashMap<String, Runnable>();


    /**
     * A list "tuples" that represent visibility settings that we have attached to this menu.
     */
    protected List<List<String>> visibility = new ArrayList<List<String>>();


    /**
     * A map of all the user permissions that the system is aware of (represented by a key),
     * and whether the current user has such permission (represented by boolean).
     */
    protected Map<String, Boolean> userPermissions = new HashMap<String, Boolean>();

    /**
     * Menu Status
     */
    protected String currentBuildKey; //Represents the current menu we are dynamically building
    protected String currentParent = "root"; //Represents the current position of the menu we are displaying


/********************************************************************************************************
 *
 * Methods to navigate through the menu
 *
 *********************************************************************************************************/

    /**
     * Takes the action of selecting a particular option in a menu.
     * This method is the core of the menu, which is a an infinite loop that
     * continually provides user options, until the user exits the program.
     */
    public void prompt() {
        this.updatePermissions();
        this.cp.get("chooseOption");
        this.cp.menuDisplay(this.getChildMenu());

        try {
            int option = this.readOption(this.getChildMenu());

            //Goes up a level
            if (option == -1) {
                if(!this.currentParent.equals("root")) this.currentParent = this.getParent();
                else cp.get("alreadyAtRoot");
            }

            //Selects an invalid option
            else if (option == 0) cp.get("InvalidOption");

            //Makes a valid selection
            else if (option <= this.getChildMenu().size() && 0 < option) {
                cp.get("Selected", this.getChildMenu().get(option - 1));
                //Change the parent
                String key = this.getMenuKey(option-1);
                this.currentParent = key;
                //Run action if there is something to run
                if (this.runnable.containsKey(key)) this.runnable.get(key).run();

            }
            //Update permissions after every menu action
            this.updatePermissions();

            //If there exists a child menu, go to it
            if (this.getChildMenu().size() != 0) this.prompt();
                //Else go back to the parent
            else this.currentParent = this.getParent();
            this.prompt();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Changes the current menu options being displayed to the user.
     * @param key A unique key to describe the menu options to display.
     */
    public void navigateTo(String key) {
        this.currentParent = key;
    }


    /**
     * Gets the parent key of the current menu item.
     * @return A string representing the unique key of the parent.
     */
    public String getParent() {
        String parentKey = null;
        for (List<String> i : parentChild) {
            if (i.get(1).equals(this.currentParent)) {
                parentKey = i.get(0);
            }
        }
        return parentKey;
    }


    /**
     * Gets all the child keys of the current parent.
     * @return a List of ids
     */
    public List<String> getChildren() {
        List childKeys = new ArrayList<String>();
        for (List<String> relation : parentChild) {
            if (relation.get(0).equals(this.currentParent) && this.shouldShow(relation.get(1))) {
                childKeys.add(relation.get(1));
            }
        }
        return childKeys;
    }


    /**
     * Gets a list children of the current parent, represented as menu options.
     * @return A List of strings
     */
    public List<String> getChildMenu() {
        List<String> childMenu = new ArrayList<String>();
        for (String key : this.getChildren()) {
            childMenu.add(this.menu.get(key));
        }
        return childMenu;
    }


    /**
     * Gets the nth menu item's unique key.
     * @param n An integer representing the n'th item in the menu
     * @return A string representing the n'th items unique key.
     */
    public String getMenuKey(int n) {
        return this.getChildren().get(n);
    }



/********************************************************************************************************
 *
 * Methods to build/edit the menu from other classes
 *
 *********************************************************************************************************/


    /**
     * Creates a menu item.
     * @param key a unique key representing the id of the menu item.
     * @param displayValue A string representing the visual display of the menu item to the user.
     * @return This menu.
     */
    public Menu create(String key, String displayValue) {
        this.menu.put(key, displayValue);
        this.currentBuildKey = key;
        return this;
    }


    /**
     * Sets the current menu item being built as an option underneath another menu item.
     * @param parentKey The unique key of the parent to attach this menu item to
     * @return This menu.
     */
    public Menu attachParent(String parentKey) {
        List<String> keyValue = new ArrayList<String>();
        keyValue.add(parentKey);
        keyValue.add(currentBuildKey);
        this.parentChild.add(keyValue);
        return this;
    }


    /**
     * Sets an action for the program to take when the user selects this particular menu item.
     * @param func a function to run when the user selects this menu item.
     * @return This menu.
     */
    public Menu setAction(Runnable func) {
        this.runnable.put(currentBuildKey, func);
        return this;
    }


    /**
     * Sets whether this menu item is available for the user to see, based on the user's level of permissions.
     * @param permission A unique key from the list of userPermissions map.
     * @return This menu.
     */
    public Menu setVisibility(String permission) {
        List<String> keyPermissionPair= new ArrayList<>();
        keyPermissionPair.add(currentBuildKey);
        keyPermissionPair.add(permission);
        this.visibility.add(keyPermissionPair);
        return this;
    }

/********************************************************************************************************
 *
 * Methods to set visibility of certain menu items, based on user permissions
 *
 *********************************************************************************************************/


    /**
     * Decides whether to show a certain menu item.
     * If there exists at least one permission requirement that evaluates to false, then we return false.
     * @param key The unique key representing the menu item.
     * @return True if and only if this menu item should be shown.
     */
    public boolean shouldShow(String key) {
        for (List<String> permission : this.visibility) {
            if (permission.get(0).equals(key) && !userPermissions.get(permission.get(1))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the userPermissions array list with all the permissions available.
     */
    private void updatePermissions() {

        try {
            this.userPermissions.put("isAdmin", this.accessesMonitor.isAdmin());
            this.userPermissions.put("isFrozen", this.accessesMonitor.isFrozen());
            this.userPermissions.put("isNotFrozen", !this.accessesMonitor.isFrozen());
            this.userPermissions.put("canLend", this.accessesMonitor.canLend());
            this.userPermissions.put("canBorrow", this.accessesMonitor.canBorrow());

            boolean trade = (this.accessesMonitor.canLend() || this.accessesMonitor.canBorrow()) && !this.accessesMonitor.isFrozen();
            this.userPermissions.put("showTrade", trade);
            this.userPermissions.put("hideTrade", !trade);

        }
        catch(IOException e){
            this.errorPresenter.displayIOException();
        }
    }


}