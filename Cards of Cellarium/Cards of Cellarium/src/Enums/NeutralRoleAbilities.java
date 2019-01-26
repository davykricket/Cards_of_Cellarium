package Enums;

public enum NeutralRoleAbilities
{
    KICK (0),
    BLOCK (1),
    HEAL (2),
    FAKE (3),
    DISGUISE (4),
    DONOTHING (5);

    private String description;

    NeutralRoleAbilities(int abilityNum)
    {
        switch(abilityNum)
        {
            case 0:
                description = "Kick a player at night.";
                break;
            case 1:
                description = "Block yourself from getting kicked for this night.";
                break;
            case 2:
                description = "Choose a person to save from being kicked.  Can be self-casted";
                break;
            case 3:
                description = "Fake it to visitors and appear as a mafia member for this night.";
                break;
            case 4:
                description = "Disguise yourself as a town member to deceive visitors.";
                break;
            case 5:
                description = "Do nothing.\nYes.\nLiterally nothing.";
                break;
        }
    }

    public String getDescription()
    {
        return description;
    }
}
