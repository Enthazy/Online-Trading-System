package controller.presenters;


import view.View;

public class ErrorPresenter {

    /**
     * Class dependencies
     */
    View view;

    /**
     * Initializes the error presenter.
     * @param view View
     */
    public ErrorPresenter(View view){
        this.view = view;
    }

    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayEntryDoesNotExistException(){
        view.display("Entry does not exist, please try again.");
    }

    /**
     * display exception message for EntryExistsException
     */
    public void displayEntryExistsException(){
        view.display("Entry exists already, please try again.");
    }

    /**
     * display exception message for NonUniformObjectsException
     */
    public void displayNonUniformObjectsException(){
        view.display("NonUniformObjectsException");
    }

    /**
     * display exception message for NoSuchElementException
     */
    public void displayNoSuchElementException(){
        view.display("No such element, please try again.");
    }

    /**
     * display exception message for TransactionDoesNotExistException
     */
    public void displayTransactionDoesNotExistException(){
        view.display("Transaction does not exist, please try again.");
    }

    /**
     * display exception message for TooManyEditionsException
     */
    public void displayTooManyEditionsException(){
        view.display("You have exceeded the number of meeting edits allowed!");
    }

    /**
     * display exception message for TooManyLocationsException
     */
    public void displayTooManyLocationsException(){
        view.display("Too many locations!");
    }

    /**
     * display exception message for TooManyTimesException
     */
    public void displayTooManyTimesException(){
        view.display("You have edited the meeting too many times!");
    }

    /**
     * display exception message for TooManyItemListsException
     */
    public void displayTooManyItemListsException(){
        view.display("You have too many item lists!");
    }

    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayUserNotFoundException(){
        view.display("User not found!");
    }

    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayDuplicatedUserNameException(){
        view.display("This username has been taken, please enter another one: ");
    }

    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayInvalidLoginCredentialsException(){
        view.display("--------------------------------------------\r\n" +
                "Wrong username or password. Please try again" +
                "\r\n--------------------------------------------");
    }

    /**
     * display exception message for ItemDoesNotExistException
     */
    public void displayItemDoesNotExistException(){
        view.display("Item does not exist.");
    }


    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayDateTimeParseException(){
        view.display("Please enter a valid date with the correct format!");
    }

    /**
     * display exception message for EntryDoesNotExistException
     */
    public void displayNoLoginUserException(){
        view.display("No login user!");
    }

    /**
     * display exception message for NoOpenTransactionException
     */
    public void displayNoOpenTransactionException(){
        view.display("There are no open transaction!");
    }

    /**
     * display exception message for PersistenceException
     */
    public void displayPersistenceException(){
        view.display("Persistence exception");
    }

    /**
     * display exception message for RuleDoesNotExistException
     */
    public void displayRuleDoesNotExistException(){
        view.display("Rule does not exist!");
    }

    /**
     * display exception message for programError
     */
    public void programError() {
        view.display("Something has gone wrong");
    }

    /**
     * display exception message for IOException
     */
    public void displayIOException() {
        view.display("There is a read write problem.");
    }

    /**
     * display exception message for EditAgreedMeetingEdition
     */
    public void displayEditAgreedMeetingException() {
        view.display("Meeting which is agreed cannot be changed anymore !");
    }


}
