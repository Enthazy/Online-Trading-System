package controller.presenters.auth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates through a list of String prompts. Based on the sample code provided in class in Week 6.
 */
public class LoginIterator implements Iterator<String> {
    private List<String> prompt = new ArrayList<>();
    private int current = 0;


    /**
     *Construct LoginIterator.
     */
    public LoginIterator(){
        prompt.add("Please enter username and password to log in \nUsername:");
        prompt.add("Password:");
    }

    /**
     * Checks if there is login prompt that has not yet been returned.
     * @return true if there is login prompt that has not yet been returned.
     */
    @Override
    public boolean hasNext(){
        return current < prompt.size();
    }

    /**
     * Returns the next login prompt to be printed.
     * @return the next login prompt.
     */
    @Override
    public String next() {
        String next;
        try {
            next = prompt.get(current);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
        current += 1;
        return next;
    }

    /**
     * Removes the prompt just returned. Unsupported.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

}
