import javafx.fxml.FXML;
import javafx.scene.control.*;

/**This is the class for the controller used in the GameMenu FXML file
 *
 * @author David Lee
 * @author Carson Mathews
 * @version idk
 */
public class GameMenuController
{
    /**These 3 ListViews are used to display the player's hand, the list of players in the game,
     * and all of the players who have been kicked in the game
     */
    @FXML ListView visibleHand, playerList, kickedList;
    /**This is the chat display to show all of the messages sent throughout the server
     */
    @FXML private TextArea gameChatArea;
    /**This is the area used to type into for the chat menu
     */
    @FXML private TextField gameChatField;
    /**These 2 labels are for the timer and the name display within the UI
     */
    @FXML private Label timerLabel, nameLabel;
    /**This Checkbox is used to confirm night actions, votes, and judgement
     */
    @FXML private CheckBox confirmAction;
    /**This TextArea contains information pertaining to the player during the game
     */
    @FXML private TextArea information;

    @FXML private ListView gangList;

    /**This is the default constructor
     *
     */
    public GameMenuController()
    {

    }

    /**This method is used whenever you press enter in the game for the chat menu
     */
    public void onEnterPress()
    {
        PreGameController.client.setChatBar(gameChatField);
        System.out.println("enter was pressed");
        if(gameChatField.getText() != null) // the game stops right here lol, dunno why
        {
            gameChatArea.appendText(gameChatField.getText());
            System.out.println(gameChatField.getText());
            System.out.println("client ran the connect method");
        }
        else
            System.out.println("chatbar is empty");
    }

    /**This is the method that establishes all of the references to the objects in the game menu
     */
    public void createReferences()
    {
        PreGameController.client.setReferences(gameChatField, gameChatArea, timerLabel, nameLabel, visibleHand, playerList, kickedList, gangList, confirmAction, information);
    }
}