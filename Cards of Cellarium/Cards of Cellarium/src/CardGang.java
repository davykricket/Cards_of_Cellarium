import Enums.GangRoleAbilities;
import Enums.RoleGroup;
import javafx.scene.image.Image;

public class CardGang extends AbilityCard
{
    public GangRoleAbilities ability;

    public CardGang(int abilityNum)
    {
        super();
        group = RoleGroup.CARDGANG;
        switch(abilityNum)
        {
            case 0:
                ability = GangRoleAbilities.MIRROR;
                break;
            case 1:
                ability = GangRoleAbilities.REVEAL;
                break;
            case 2:
                ability = GangRoleAbilities.KICK;
                break;
            case 3:
                ability = GangRoleAbilities.FRAME;
                break;
            case 4:
                ability = GangRoleAbilities.INVESTIGATE;
                break;
            case 5:
                ability = GangRoleAbilities.INTERROGATE;
                break;
            case 6:
                ability = GangRoleAbilities.DONOTHING;
                break;
            default:
                ability = GangRoleAbilities.DONOTHING;
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
