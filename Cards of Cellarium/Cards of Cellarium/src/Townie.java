import Enums.RoleGroup;
import Enums.TownAbilities;
import javafx.scene.image.Image;

import java.util.Random;

/**This is the Townie class
 * It extends AbilityCard
 *
 * @author David Lee
 * @author Carson Mathews
 * @version pleasesaveme
 */
public class Townie extends AbilityCard
{
    /**This is the ability for the card that is being made
     */
    public TownAbilities ability;

    /**This is the default constructor
     *
     */
    public Townie()
    {

    }

    /**This is the constructor used for the Townie class
     * @param abilityNum This is the card ability being made
     */
    public Townie(int abilityNum)
    {
        super();
        group = RoleGroup.TOWN;
        switch(abilityNum)
        {
            case 0:
                ability = TownAbilities.JAIL;
                break;
            case 1:
                ability = TownAbilities.HEAL;
                break;
            case 2:
                ability = TownAbilities.INVESTIGATE;
                break;
            case 3:
                ability = TownAbilities.VIGILANTE;
                break;
            case 4:
                ability = TownAbilities.ALERT;
                break;
            case 5:
                ability = TownAbilities.PROTECT;
                break;
            case 6:
                ability = TownAbilities.KICK;
                break;
            case 7:
                ability = TownAbilities.BLOCK;
                break;
            case 8:
                ability = TownAbilities.DONOTHING;
                break;
            default:
                ability = TownAbilities.DONOTHING;
                break;
        }
        System.out.println(ability.name());
        icon.setImage(new Image("Images/" + ability.name() + ".png"));
        information.setText(ability.getDescription());
    }

    /** This method returns what action to act on a specific person
     * @param person The person to act on
     * @return This returns the statement to send to the server to execute the command
     */
    public String action(String person)
    {
        return ability.name() + person;
    }
}
