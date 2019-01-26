import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;

    public static String[] argsReference;

    /**This is the GameMenuController that we use to create the actual game menu when everything is made
     */
    public static GameMenuController gameMenuController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXML/mainMenu.fxml"));
        mainStage = primaryStage;

        primaryStage.setResizable(false);

        primaryStage.setTitle("Card of Cellarium");
        primaryStage.setScene(new Scene(root, 300, 500));
        primaryStage.setOnCloseRequest( v-> {
            try
            {
                if(Client.socket != null)
                {
                    Client.socket.close();
                }
                System.exit(0);
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        });

        primaryStage.show();
    }


    public static void main(String[] args)
    {
        argsReference = args;
        launch(args);
    }
}
