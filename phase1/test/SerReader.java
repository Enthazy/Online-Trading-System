
import boot.Container;
import entities.*;
import persistence.PersistenceInterface;
//import usecases.permissions.PermissionsManagerMock;

import java.io.IOException;
import java.util.List;

public class SerReader {

    public static void main(String[] args) {

        Container container = new Container();
        try{
            container.start();

            PersistenceInterface p = container.get("PersistenceInterface", PersistenceInterface.class);

            List<User> users = p.all(User.class);
            List<Item> items  = p.all(Item.class);
            List<Inventory> inventory  = p.all(Inventory.class);
            List<Meeting> meeting  = p.all(Meeting.class);
            List<Trade> trade  = p.all(Trade.class);
            List<Transaction> transactions  = p.all(Transaction.class);
            List<WishList> wishlist  = p.all(WishList.class);
            List<Config> config  = p.all(Config.class);

            for (User user: users) {
                System.out.println(user);
            }

            for (Item item :items) {
                System.out.println(item);
            }

            for (Inventory i :inventory) {
                System.out.println(i);
            }

            for (Meeting i :meeting) {
                System.out.println(i);
            }

            for (Trade i :trade) {
                System.out.println(i);
            }

            for (Transaction i :transactions) {
                System.out.println(i);
            }

            for (WishList i :wishlist) {
                System.out.println(i);
            }

            for (Config i :config) {
                System.out.println(i);
            }


        }  catch (IOException e) {
            e.printStackTrace();
        }


    }



}
