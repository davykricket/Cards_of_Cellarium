package Enums;

public enum GangRoleAbilities
{
    KICK (0),
    REVEAL (1),
    FRAME (2),
    MIRROR (3),
    INVESTIGATE (4),
    INTERROGATE (5),
    DONOTHING (6);

    private String description;

    GangRoleAbilities(int abilityNum)
    {
        switch(abilityNum)
        {
            case 0:
                description = "Kick a player at night.";
                break;
            case 1:
                description = "Reveal the exact role of a player at night.";
                break;
            case 2:
                description = "Make someone look like a Card Gang member to the investigator";
                break;
            case 3:
                description = "If someone targets to kick you, randomly kick a different player.";
                break;
            case 4:
                description = "Investigate someone at night to see if they're neutral or town.";
                break;
            case 5:
                description = "Kidnap one person and interrogate them throughout the night with other gang members.";
                break;
            case 6:
                description = "Do nothing.\nYes.\nLiterally nothing.";
                break;
        }
    }

    public String getDescription()
    {
        return description;
    }
}
