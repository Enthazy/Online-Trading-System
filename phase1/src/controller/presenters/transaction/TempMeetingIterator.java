package controller.presenters.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Based on the sample code provided in class in Week 6
 */
public class TempMeetingIterator extends BaseIterator {
    private List<String> prompt = new ArrayList<>();
    private int current = 0;


    /**
     *Construct TempMeetingIterator.
     */
    public TempMeetingIterator(){
        prompt.add("Please enter the location for your first meeting. Press 'Enter' to confirm.");
        prompt.add("Please enter the location for your second meeting. Press 'Enter' to confirm.");
        prompt.add("Please enter the date for your first meeting in the format 'YYYY-MM-DD'. Press 'Enter' to confirm.");
    }

    /**
     * Checks if there is prompt that has not yet been returned.
     * @return true if there is prompt that has not yet been returned.
     */
    @Override
    public boolean hasNext(){
        return current < prompt.size();
    }

    /**
     * Returns the next prompt to be printed.
     * @return the next prompt.
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

    /**
     * Checks if a prompt in this presenter demands a date
     * @return True if the prompt demands a date.
     */
    @Override
    public boolean demandsDate() {
        if (current == 3) {
            return true;
        }
        return false;
    }

}


