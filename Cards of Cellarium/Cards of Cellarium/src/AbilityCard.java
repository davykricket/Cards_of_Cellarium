import Enums.RoleGroup;
import Enums.TownAbilities;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public abstract class AbilityCard extends VBox
{
    public ImageView icon;

    public TextArea information;

    public RoleGroup group;

    public AbilityCard()
    {
        setPrefSize(286, 400);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPadding(new Insets(25, 25, 25, 25));
        setSpacing(30);

        icon = new ImageView();
        icon.setFitWidth(236);
        icon.setFitHeight(213);

        information = new TextArea();
        information.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        information.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        information.setPrefSize(236, 107);

        information.setEditable(false);
        information.setWrapText(true);

        this.getChildren().addAll(icon, information);
    }

    //Think about what methods to add into this class
    public abstract String action(String person);
}
