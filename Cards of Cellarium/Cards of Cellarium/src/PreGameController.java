import Enums.GangRoleAbilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
/**This is the class for the controller used in the PreGameMenu FXML file
 *
 * @author David Lee
 * @author Carson Mathews
 * @version idk
 */
public class PreGameController
{
    /**This Label is to show the IP address that the server is running on
     */
    @FXML Label ipAddress;
    /**This is the String to hold the IP value
     */
    public static String ip;
    /**This is the player's name
     */
    private String name;

    @FXML private Button preGameButton;

    /**This is the client for the player.
     * It is static in order to allow the GameMenuController access to the object
     */
    public static Client client;

    // FIXME: 4/17/2018 Crashes when server is ran and hits listener.accept()

    /**This is the method that "creates" a server on this IP
     * @param playerName This is the name of the initial client
     */
    public void createServer(String playerName)
    {
        name = playerName;
        ip = Server.getIP();
        ipAddress.setText("IP Address: " + ip);
        client = new Client(name, ip, true);
    }

    /**This is the method that joins a server on this IP and deletes the server initially made on this client
     * @param playerName This is the player name for this client
     * @param ip This is the IP connecting to
     */
    public void joinServer(String playerName, String ip)
    {
        name = playerName;
        preGameButton.setVisible(false);
        client = new Client(playerName, ip, false);
        this.ip = ip;
        ipAddress.setText("IP Address: " + ip);

    }

    /**This creates the Game menu using the GameMenuController and passes all of the references needed to play the game
     */
    public void playGame()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/gameMenu.fxml"));
        createGameMenu(loader);
    }

    public static void createGameMenu(FXMLLoader loader)
    {
        try
        {
            Parent root = loader.load();

            Main.mainStage.setScene(new Scene(root, 1800, 1150));

            Main.mainStage.setResizable(false);
            Main.mainStage.show();

            Main.gameMenuController = loader.getController();

            Main.gameMenuController.createReferences();
            Client.gameStart = true;
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}