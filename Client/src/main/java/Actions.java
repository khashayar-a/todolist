// Created by Khashayar Abdouli on 8/21/14.
//

/**
 * A middle class for handling all client actions
 */
public class Actions {


    /**
     * Start the form for adding new ToDoList
     * @param user given user
     */
    public static void add(String user) {
        new AddForm(user).setVisible(true);
    }

    /**
     * Starts the form for signing up
     */
    public static void signup() {

        new SignUp().setVisible(true);
    }

    /**
     * Starts the form for viewing all the ToDoLists
     * @param userID given userID
     */
    public static void view(String userID) {
        new View(Couch.getList(userID)).setVisible(true);
    }
}
