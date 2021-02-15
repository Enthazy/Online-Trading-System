package controller.presenters.transaction;

import java.util.Iterator;

abstract public class BaseIterator implements Iterator<String> {

    /**
     * Checks if a prompt in this presenter demands a date
     * @return True if the prompt demands a date.
     */
    abstract public boolean demandsDate();


}
