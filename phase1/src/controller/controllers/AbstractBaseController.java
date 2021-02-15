package controller.controllers;

import controller.Menu;
import controller.presenters.*;
import controller.presenters.transaction.BaseIterator;
import usecases.authentication.ListensForUser;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;


abstract public class AbstractBaseController implements ListensForUser  {

    /**
     * Class dependencies
     */
    protected final ControllerPresenter controllerPresenter;
    protected final ErrorPresenter errorPresenter;

    /**
     * Initializes the class
     * @param controllerPresenter ControllerPresenter
     * @param errorPresenter ErrorPresenter
     */
    public AbstractBaseController(ControllerPresenter controllerPresenter, ErrorPresenter errorPresenter) {
        this.controllerPresenter = controllerPresenter;
        this.errorPresenter = errorPresenter;
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


    /**
     * Holds the current logged in user.
     */
    protected int userId;


    /**
     * Holds an instance of the menu.
     */
    protected Menu menu;


    /**
     * Sets the current logged in user id. Required for ListensForUser
     * @param userId A unique key representing the user.
     */
    @Override
    public void updateUserId(int userId) {
        this.userId = userId;
    }


    /**
     * Sets the Menu parameter
     * @param menu menu
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }


    /**
     * Reads a string.
     *
     * @return Returns the string input from the console.
     * @throws IOException if there is a IO error.
     */
    public String readString() throws IOException {
        String input = "";
        try {
            input = br.readLine();
        } catch (NullPointerException e) {
            System.exit(0);
        }
        return input;
    }


    /**
     * Read a number that is smaller than max
     *
     * @param max the upper bound
     * @return the option
     */
    public int readOption(int max) {
        try {
            if (Integer.parseInt(readString()) < max && Integer.parseInt(readString()) > 0) {
                return Integer.parseInt(readString());
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    /**
     * Read the choice of a given list of strings.
     *
     * @param list Displayed List of String
     * @return -1: back, 0: refresh, i: the item at index (i-1)
     * @throws IOException if there is a IO error.
     */
    public int readOption(List<String> list) throws IOException{
        try {
            String input = readString();
            if (input.equals("exit")) {
                return -1;//back to lower level
            }
            if (Integer.parseInt(input) <= list.size() && Integer.parseInt(input) > 0) {
                return Integer.parseInt(input);
            }
        } catch (NullPointerException e) {
            System.exit(0);
        } catch (Exception e) {
            return 0;
        }
        //throw an error
        return 0;//refresh
    }

    /**
     * Read the input yes or no
     * @return 1: Yes, 2: No, 0: otherwise
     * @throws IOException if there is a IO error.
     */
    public int readYesNo() throws IOException {
        try {
            String input = readString();
            if (input.equals("y") || input.equals("yes")) {
                return 1;
            }
            if (input.equals("n") || input.equals("no")) {
                return 2;
            } else {
                return 0;
            }
        } catch (NullPointerException e) {
            System.exit(0);
        }
        return 0;
    }

    /**
     * Read an natural number (int) in range [min, max], min should greater than -1
     * return -1 for invalid input
     *
     * @param min greater than -1
     * @param max greater than min
     * @return the valid input integer in range
     */
    public int readInt(int min, int max) {
        try {
            String input = readString();
            if (Integer.parseInt(input) <= max && Integer.parseInt(input) >= min) {
                return Integer.parseInt(input);
            }
        } catch (NullPointerException e) {
            System.exit(0);
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }


    /**
     * Keeps prompting user until we get a valid date in the future.
     * @param date the string for valid date input
     * @return A string representing a date
     * @throws IOException the io exception meet
     */
    protected String validateDateInput(String date) throws IOException {
        boolean isDate = false;
        while(!isDate) {
            try {
                LocalDate dateCandidate = LocalDate.parse(date);
                if (dateCandidate.isAfter(LocalDate.now())) isDate = true;
                else {
                    controllerPresenter.get("FutureDate");
                    date = br.readLine();
                }
            } catch (DateTimeParseException e) {
                controllerPresenter.get("promptDateAgain");
                date = br.readLine();
            }
        }
        return date;
    }


    /**
     * Gathers input based on either a permanent or temporary transaction.
     * @param prompts the reader
     * @return List<String>
     * @throws IOException io exception meet
     */
    protected List<String> gatherInput(BaseIterator prompts) throws IOException {

        List<String> inputList = new ArrayList<>();
        while(prompts.hasNext()){
            controllerPresenter.display(prompts.next());
            String input = readString();
            if(prompts.demandsDate()) input = this.validateDateInput(input);
            inputList.add(input);
        }
        return inputList;

    }



}
