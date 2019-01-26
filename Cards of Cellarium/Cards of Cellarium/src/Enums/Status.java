package Enums;

public enum Status {
    KICKED (),
    DFNS (),
    INTE (),
    JAIL (),
    GANG (),
    TOWN (),
    SKIK (),
    WITH (),
    APOT (),
    JEST ();

    String description;

    Status()
    {
        switch(name())
        {
            case "KICKED":
                description = "You have been kicked. You cannot participate, but can spectate.";
                break;
            case "GANG":
                description = "As a gang member, your goal is to kick anyone who is not in the gang. Gang gang.";
                break;
            case "DFNS":
                description = "Defend yourself; explain why you shouldn't be kicked.";
                break;
            case "INTE":
                description = "You're being investigated by the Card Gang right now.";
                break;
            case "JAIL":
                description = "You're in jail.";
                break;
            case "TOWN":
                description = "As a townie, your goal is to survive and eliminate the gang as well as the neutrals.";
                break;
            case "SKIK":
                description = "You are the Serial Kicker. To win, you need to basically kick as many people as possible.";
                break;
            case "WITH":
                description = "Mission: Survive 5 days. If you do, you become OP and win the game. gg.";
                break;
            case "APOT":
                description = "You have evil potions to use that instantly kick someone. Use them wisely and stay alive. You may also heal people with a health potion.";
                break;
            case "JEST":
                description = "lolololol get yourself kicked by the other players, and you win! trololol";
                break;
        }
    }

    public String getDescription()
    {
        return description;
    }
}




