package view;


/**
 * Displays outputs in a command line.
 */
public class TextUI implements View{

    /**
     * Displays a message.
     * @param message A message string.
     */
    @Override
    public void display(String message) {
        System.out.println(message);
    }

}
