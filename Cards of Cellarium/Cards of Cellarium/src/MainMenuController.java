import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class MainMenuController
{
    private PreGameController preGameController;

    @FXML private TextField name;

    public void joinServer()
    {
        VBox window = new VBox(10);
        window.setPrefSize(300, 100);
        window.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        window.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        Label label = new Label("Input Server IP:");

        TextField inputArea = new TextField();
        inputArea.setPromptText("IP");

        Button accept = new Button("Connect");
        accept.setOnAction(e -> {
            if(inputArea.getText() != null)
            {
                try
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/preGameMenu.fxml"));

                    Parent root = loader.load();
                    Main.mainStage.setScene(new Scene(root, 650, 750));

                    Main.mainStage.setResizable(false);
                    Main.mainStage.show();

                    preGameController = loader.getController();
                    preGameController.joinServer(name.getText(), inputArea.getText());
                }
                catch(IOException ioe)
                {

                }
            }
        });

        window.getChildren().addAll(label, inputArea, accept);

        Main.mainStage.setScene(new Scene(window, 300, 100));
        Main.mainStage.setResizable(false);
        Main.mainStage.show();
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/gameMenu.fxml"));

        Parent root = loader.load();

        Main.mainStage.setOnCloseRequest( e-> {
            try
            {
                if(Client.socket != null)
                {
                    Client.socket.close();
                }
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        });

        Main.mainStage.setScene(new Scene(root, 1800, 1150));

        Main.mainStage.setResizable(false);
        Main.mainStage.show();

        gameMenuController = loader.getController();

        gameMenuController.createClient(name.getText());*/
    }

    public void host() throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/preGameMenu.fxml"));

        Parent root = loader.load();
        Main.mainStage.setScene(new Scene(root, 650, 750));

        Main.mainStage.setResizable(false);
        Main.mainStage.show();

        preGameController = loader.getController();
        preGameController.createServer(name.getText());
    }

    public void exit()
    {
        System.exit(0);
    }
}