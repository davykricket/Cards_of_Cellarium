package Enums;

public enum TownAbilities
{
    PROTECT (0),
    HEAL (1),
    INVESTIGATE (2),
    JAIL (3),
    ALERT (4),
    VIGILANTE (5),
    KICK (6),
    BLOCK (7),
    DONOTHING (8);

    private String description;

    TownAbilities(int abilityNum)
    {
        switch(abilityNum)
        {
            case 0:
                description = "Protect one player from being kicked, but you get kicked instead";
                break;
            case 1:
                description = "Choose a person to save from being kicked.  Can be self-casted";
                break;
            case 2:
                description = "Visit someone in the night and sees if they're suspicious or not.";
                break;
            case 3:
                description = "Chooses one person to take to jail to interrogate.  Chats with that one person and can choose to kick them or not";
                break;
            case 4:
                description = "Goes on alert for that night, kicking whoever visits them during that night.";
                break;
            case 5:
                description = "Can choose to kick someone at night, but if they're innocent then you kick yourself.";
                break;
            case 6:
                description = "Kick a player at night.";
                break;
            case 7:
                description = "Block yourself from getting kicked for this night.";
                break;
            case 8:
                description = "Do nothing.\nYes.\nLiterally nothing.";
                break;
        }
    }

    public String getDescription()
    {
        return description;
    }
}
