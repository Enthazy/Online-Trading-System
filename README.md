# Online-Trading-System
This is the course project for the course Software Design (CSC207) in University of Toronto.

This project is contribuited by Andrew Wang, Weigeng Peng, Xiuyu, Hanchun Wang, Ganchu Yao, Zheng Chen, HeiShen

==============================================================================

# INTRODUCTION

==============================================================================

- This is a web application that relies on the spring-boot framework (https://spring.io/projects/spring-boot)

==============================================================================

# UTILIZATION OF SPRING-BOOT FRAMEWORK

==============================================================================
- We use the Spring-boot framework in a few key areas:
  1) Routing HTTP GET/POST requests to the correct controller methods
  2) Handling session variables (i.e. we want browser 1 to stay logged in, on each HTTP request).
     As noted above, the "driver" for the session variables is a SQL database.
  3) Handling two-way communication between the view and the controllers
     a) Spring-Boot uses Model objects to pass to the view, and the view (implemented with Thymeleaf) is able to do basic things such as display dynamic data, iterate through collections, and run conditionals.
     b) Spring-Boot/Thymeleaf uses a simple object with getters and setters to pass form data from the view back to the controller, which the controller can access by type-hinting the correct class.
  4) We also use Spring-boot's dependency injection framework. Controllers are instantiated with the functionality of handling web requests automatically by the framework,
     and by type-hinting the correct concrete class or abstract interface, we are able to get an instance of that class we type-hinted.
     The construction of these objects are defined in SpringConfig, in the boot directory.



==============================================================================

# DESIGN NOTES/DESIGN PATTERNS/SOLID/CLEAN ARCHITECTURE/HOW OUR CODE HAS IMPROVED:

==============================================================================

1) Dependency Injection:
   - We use Spring's container, and allows us to manage dependencies (and swap out implementations) via dependency injection easily.
   - The dependencies are explicit, and each class's dependencies are limited, so that we have minimal coupling.
   - It also allows us to swap out implementations of abstract interfaces easily.
   - Relevant classes: almost classes except for the entities are involved in this design pattern.


2) Persistence:
   -   We apply dependency inversion and let most (if not all) use-case classes depends on "PersistenceInterface".
   -   In Phase 1, we have chosen SerPersistenceGateway as the concrete implementation of PersistenceInterface.
   -   In Phase 2, we have chosen to continue to use it.
   -   The persistence interface takes generics, and thus is unaware of exactly what type of object is saving,
       so long as what it saves implements the "Persistable" interface

3) Complex Querying & Decorator Design Pattern:
   - We use a decorator pattern to decorate a Query, in multiple steps, before sending it off to the Fetcher class to fetch from persistence.
   - This query builder allows the user to chain filters together easily.
     Example usage:
     query().onlyApproved().onlyOwnedBy(5).heldByOwner().getNames();
     This will return the names of items that have been approved, owned by user id 5 and held by user id 5.
   - Relevant Classes: ItemFetcher and TransactionFetcher

4) Many-to-many mapping:
   - We use the MapsRelation interface and a RelationMapper to do many-to-many mapping.
   - This class maps the relations between one entity to another entity.
     For example, an item may have many tags, and a tag may be associated with many items.
     For another example, a transaction has a number of meetings, and a meeting will belong to a transaction.
     Once registered, an entity that implements HasRelations will able to use this following syntax to fetch all the tags associated with it.
     tradeEntity.relation(relationMapper, "transactions", Transaction.class)  will provide an ArrayList of Transactions associated with this Trade
     transactionEntity.relation(relationMapper, "trades", Trade.class) will provide an ArrayList of Trades associated with the Transactions
   - Relevant Classes:
     Entities that implements RelationMapper:
        Item , Meeting, Tag, Trade, Transaction User
     Usecases that depends on RelationMapper:
        ItemFetcher, MeetingManager, TagManager, TransactionManager, CreditManager

5) EventHandler & Pub/Sub:
   -   We have an event handler that handles "global events" that many use-case classes/controllers need to be aware of, such as the change in authenticated user,
       a new registered user, or a change in the system configuration done by the administrator.
   -   This allows for convenient decoupling of classes that shouldn't really be aware of each other.
   -   Relevant classes: All the classes in the eventhandler package


6) Facade:
   -   We implemented two facades, one related to system functionality called SystemFacade (methods that aren't really related to the trading part of the application).
       And the other called TradingFacade (methods that are related to trading).
       SystemFacede, TradingFacade

7) Command Design Pattern:
   -   In building the "undo" functionality, we opted to save "changes" rather than "state" of the application.
       In other words, we opted to store the "before" and "after" of every action, so that we could revert to the "before" state.
   -   Thus we utilized the "Command Design Pattern", and encapsulated all the actions we want to undo.
   -   Relevant classes: All the classes in the command package.

==============================================================================

VIEWING THE APPLICATION

==============================================================================
- We also have provided a deployed version of our app, with dummy data, that is based on this code.
  Aside from the data, it should run exactly like the local version. Please visit:
  http://cs207-env.eba-jdbxmr3u.us-east-2.elasticbeanstalk.com/

==============================================================================

RUNNING THE APPLICATION LOCALLY:

==============================================================================
- This project uses maven.
- From the phase2 directory (important), run mvnw spring-boot:run or run ./mvnw spring-boot:run (for macOS) to launch the application locally.
  IMPORTANT: You will still need a working internet connection,
  since we opted to use an AWS EC2 hosted relational database (rather than redis)
  to save user session data, since it was easier to install.
- Make sure port 5000 is clear, and go to 127.0.0.1:5000 to view the app in your browser.
- In terms of persistence of application data, we have opted to stay with our Phase 1 .ser file. This was mostly because
  the focus of this code is on Java, and we'd prefer spending our time there, and create more features instead of implementing a relational database.
- The name of the file will be serfileentities.<CLASS NAME>.ser in the Phase2 directory.
- Please make sure that proper read/write access is available on this directory,
  or you may get a message indicating an IOException, since the ser files won't save.
  (In this extreme circumstance, the message will be logged to application-errors.log)

==============================================================================

SEEDING THE APPLICATION WITH DUMMY DATA

==============================================================================
- If you go to the root url, you will see that we have a function to allow you to seed data, or delete data and start again.
  This is for marking purposes only
- We have added the following accounts, all with PASSWORD 123:
  1@admin.com  --> NOTE THIS IS AN ADMINISTRATOR ACCOUNT
  1@1.com
  2@2.com
  3@3.com
- The pre-populated data includes the users, some approved items, and an ongoing trade.


==============================================================================

READ ME / UML / COMMENTARY TO AID MARKING:

==============================================================================
- We have added README files in relevant packages in the source files in order to explain further our design decisions.
- We will also have lots of helper text in the actual "View", so you can get a good idea of what the intended consequence of all the settings will be.



==============================================================================

FEATURES WE HAVE BUILT/CHOSEN FOR PHASE 2

==============================================================================
- In the file Phase2Requirements in the same directory, we have added the posted requirements for Phase 2.
- In addition to the specification for PHASE 1, we have implemented the following:

PART 1) The Mandatory features for Phase 2
    a) Undo - You can find this functionality when you log in as admin and go to the admin panel.
    b) Automatically suggest items - You can find this after you launch a trade in the "Browse Avaiable Items" menu.
       Once you click the permanent or temporary button, you the next page will recommend items you can exchange.
    c) Demo accounts - You can add demo accounts in the admin panel. When you login as a demo account, none of your actions are recorded.
    d) Change threshold values - You can adjust these values in the admin panel
    e) Account status - Go to the account page and pause your account to go on vacation!

PART 2) The 4 following optional extensions
    a) Allow users to enter their home city when creating an account, so that the system will only show them other users in the same city.
    b) Create a point system that allows users who has a cumulative points above 1000 then you can borrow as much as you wish.
    c) Replace your text UI with a Graphic User Interface (GUI). (This counts as two features according to the posted requirements)

PART 3) The following additional features:
    a) A reporting feature, which allows the administrator to view application-wide analytics, the most popular traded items, the users with the highest credit.
    b) A tagging feature, which allows items to be associated with tags, and thereby one can filter items by their tag.
    c) The monetization of the system. i.e. Giving users the option to sell or trade items.
       (This feature's idea is from the posted requirements for Part 2, but we were told excess Part 2 features can be used for Part 3)
    d) The user and the admin are able to change their password and home city.
    e) Cancel transaction option for users
        (From phase 1, the only way that a transaction will be canceled is when the relevant users has edited more than a certain numbers of times the meeting location/date so we included this option for the user to cancel immediately transaction a transaction that hasn't been agreed to)
    f) Added ability to swap themes


==============================================================================

OUTSIDE SOURCES

==============================================================================
- All the java code in the java directory is written by our group.
- We also wrote the views and html, but used a Bootstrap css template (https://bootswatch.com/darkly/, https://bootswatch.com/flatly/ ) and some HTML snippest the bootswatch snippet provides to aid with setting styles.
- The view is based on a templating engine called Thymeleaf which works closely with Spring-Boot
- In the <head></head> tags of layout.html, you can see the other references (JQuery, bootstrap, flatpickr etc.)


