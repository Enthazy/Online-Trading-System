package boot;

import controller.Menu;
import controller.MenuBuilder;

public class Main {


    /**
     * The starting point of the application.
     * @param args
     */
    public static void main(String[] args) {

        Container container = new Container();

        //Runs the dependency injection container. Instantiates most classes of which we only need one instance.
        container.start();

        //Builds main menu
        Menu m = container.get("MenuBuilder", MenuBuilder.class).build();

        //Prompt the user for a menu input.
        m.prompt();

    }





}
