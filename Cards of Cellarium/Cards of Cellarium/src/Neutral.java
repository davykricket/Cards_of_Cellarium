import Enums.GangRoleAbilities;
import Enums.NeutralRoleAbilities;
import Enums.RoleGroup;
import javafx.scene.image.Image;

public class Neutral extends AbilityCard
{
    public NeutralRoleAbilities ability;

    public Neutral(int abilityNum)
    {
        super();
        group = RoleGroup.NEUTRAL;
        switch(abilityNum)
        {
            case 0:
                ability = NeutralRoleAbilities.KICK;
                break;
            case 1:
                ability = NeutralRoleAbilities.BLOCK;
                break;
            case 2:
                ability = NeutralRoleAbilities.HEAL;
                break;
            case 3:
                ability = NeutralRoleAbilities.FAKE;
                break;
            case 4:
                ability = NeutralRoleAbilities.DISGUISE;
                break;
            case 5:
                ability = NeutralRoleAbilities.DONOTHING;
                break;
            default:
                ability = NeutralRoleAbilities.DONOTHING;
                break;
        }
        System.out.println("Ability: " + ability.name());
        icon.setImage(new Image("Images/" + ability.name() + ".png"));
        information.setText(ability.getDescription());
    }


    public String action(String person)
    {
        return ability.name() + person;
    }
}