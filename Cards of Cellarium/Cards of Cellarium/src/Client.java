import Enums.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class Client
{
    static final int PORT = 6754;
    static String serverAddress = "10.6.226.214";

    static TextField gameChatBar;
    static TextArea gameChatText, information;
    static ListView playerHand, playerList, kickedList, gangList;
    static CheckBox confirmAction;
    static Label nameDisplay;

    private BufferedReader serverIn;
    private PrintWriter out;

    private static String name = "Kilik";

    public static Socket socket;

    private static Deck deck;

    private static RoleGroup group;

    private static String output;

    private static String actingOn;

    private static boolean isHost;

    public static Status state;

    private static Timeline timeline;

    private static Label timer;

    private static boolean roleGiven;

    private static boolean isHealed, blocked, mirrored, alert, isProtected, framed, isProtecting, fake, disguised, canVote, win;

    private static Status originalState;

    public static boolean gameStart, gameSetUp;

    private static int time = 15;

    private static int day = 1;

    private static TimeCycle phase = TimeCycle.DISCUSSION;

    private static String[] randNames = {"nohomu", "Danickar", "flying_ninja_pig", "GeneralDanier", "Carsonn", "T3h3l1t3g4m3r",
            "xXedgeXx", "xxEdgeLordxx", "Weeb", "IForgotToPutAName", "ITryMimicPeople", "poopypants103", "dabbythefrog"};


    public Client(String name, String ip, boolean host)
    {
        socket = null;
        this.name = name;

        isHost = host;

        gameStart = false;

        serverAddress = ip;

        try
        {
            socket = new Socket(serverAddress, PORT);
            Thread.sleep(1000);
            Thread server = new Thread(new ServerThread(socket));
            server.start();

            System.out.println(new Date() + "\nConnected to server.");
        }
        catch (IOException ioe)
        {
            System.err.println("Fatal Connection error!");
            ioe.printStackTrace();
        }
        catch (InterruptedException ie)
        {
            System.err.println("Fatal Connection error!");
            ie.printStackTrace();
        }
    }

    public void setReferences(TextField gameChatField, TextArea gameChatArea, Label timerLabel, Label nameLabel, ListView hand, ListView names, ListView kicked, ListView gang, CheckBox confirm, TextArea info)
    {
        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        playerList = names;
        kickedList = kicked;
        playerHand = hand;
        gameChatBar = gameChatField;
        gameChatText = gameChatArea;
        confirmAction = confirm;
        timer = timerLabel;
        information = info;
        gangList = gang;

        nameDisplay = nameLabel;
    }

    public static class ServerThread implements Runnable
    {
        private Socket socket;
        private BufferedReader serverIn;
        private PrintWriter out;

        public ServerThread(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                System.out.println("Running Client");
                out = new PrintWriter(socket.getOutputStream(), true);
                serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String input;

                out.println("NEWPLAYER" + name);

                System.out.println("Game Start: " + gameStart);
                while (!socket.isClosed())
                {
                    if (gameStart)
                    {
                        System.out.println("Game Start");
                        if (!gameSetUp)
                        {
                            gameSetUp = true;
                            Platform.runLater(() -> {
                                if (isHost)
                                    out.println("STARTGAME");
                                else
                                {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/gameMenu.fxml"));
                                    try
                                    {
                                        PreGameController.createGameMenu(loader);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                timeline = new Timeline();
                                timeline.setCycleCount(Timeline.INDEFINITE);
                                timeline.getKeyFrames().add(
                                        new KeyFrame(Duration.seconds(1), e ->
                                        {
                                            time--;
                                            timer.setText("Phase: " + phase.name() + " Time: " + time);
                                            if (time == 0)
                                            {
                                                System.out.println("Adding keyframe");
                                                timeline.stop();
                                                if (isHost)
                                                    out.println("TIMER_STOPPED");
                                            }
                                        }));

                                gameChatBar.setOnAction(e -> {
                                    if (!state.equals(Status.KICKED))
                                    {
                                        if (!phase.equals(TimeCycle.NIGHT))
                                        {
                                            System.out.println(gameChatBar.getText());
                                            out.println(name + ": " + gameChatBar.getText());
                                        }
                                        else if (phase.equals(TimeCycle.NIGHT) && (state.equals(Status.GANG) || state.equals(Status.INTE) || state.equals(Status.JAIL)))
                                        {
                                            System.out.println(gameChatBar.getText());
                                            out.println(state.name() + "TEXT" + name + ": " + gameChatBar.getText());
                                        }
                                        else if(phase.equals(TimeCycle.DEFENSE) && state.equals(Status.DFNS))
                                        {
                                            out.println(name + ": " + gameChatBar.getText());
                                        }
                                        gameChatBar.clear();
                                    }
                                });
                                nameDisplay.setText("Name: " + name);
                            });
                            System.out.println("Name: " + name);
                        }
                        if ((input = serverIn.readLine()) != null)
                        {
                            System.out.println("Input: " + input);
                            if (!roleGiven && input.startsWith("DISTRIBROLE"))
                            {
                                switch (input.substring(11))
                                {
                                    case "TOWN":
                                        state = Status.TOWN;
                                        group = RoleGroup.TOWN;
                                        break;
                                    case "GANG":
                                        state = Status.GANG;
                                        group = RoleGroup.CARDGANG;
                                        break;
                                    case "APOT":
                                        state = Status.APOT;
                                        group = RoleGroup.NEUTRAL;
                                        break;
                                    case "SKIK":
                                        state = Status.SKIK;
                                        group = RoleGroup.NEUTRAL;
                                        break;
                                    case "WITH":
                                        state = Status.WITH;
                                        group = RoleGroup.NEUTRAL;
                                        break;
                                    case "JEST":
                                        state = Status.JEST;
                                        group = RoleGroup.NEUTRAL;
                                        break;
                                    default:
                                        state = Status.TOWN;
                                        group = RoleGroup.TOWN;
                                        break;
                                }
                                originalState = state;
                                deck = new Deck(group, state);
                                Platform.runLater(() ->
                                {
                                    for (int x = 0; x < 6; x++)
                                        playerHand.getItems().add(deck.draw());
                                    information.setText(state.getDescription());
                                });
                                roleGiven = true;
                            }
                            else if (input.equals("START_TIMER"))
                            {
                                Platform.runLater(() -> timeline.playFromStart());
                            }
                            else if (input.startsWith("ADDPLAYER"))
                            {
                                String finalInput = input;
                                Platform.runLater(() -> {
                                    try
                                    {
                                        kickedList.setMouseTransparent(true);
                                        kickedList.setFocusTraversable(false);
                                    }
                                    catch(NullPointerException npe)
                                    {

                                    }
                                    if (state.equals(Status.GANG))
                                    {
                                        try
                                        {
                                            gangList.setMouseTransparent(true);
                                            gangList.setFocusTraversable(false);
                                        }
                                        catch(NullPointerException npe)
                                        {

                                        }
                                        gangList.setVisible(true);
                                        if (finalInput.substring(9).startsWith("GANG"))
                                            gangList.getItems().add(new TextField(finalInput.substring(13)));
                                        else
                                            playerList.getItems().add(new TextField(finalInput.substring(13)));

                                        for (Object item : gangList.getItems())
                                        {
                                            ((TextField) item).setEditable(false);
                                            ((TextField) item).setMaxWidth(150);
                                        }
                                    }
                                    else
                                        playerList.getItems().add(new TextField(finalInput.substring(13)));

                                    for (Object item : playerList.getItems())
                                    {
                                        ((TextField) item).setEditable(false);
                                        ((TextField) item).setMaxWidth(150);
                                    }
                                });
                            }
                            else if(input.startsWith("NEWNAME"))
                            {
                                Random rand = new Random();
                                if(input.substring(7).equals(name))
                                {
                                    name = randNames[rand.nextInt(randNames.length)] + rand.nextInt(100);
                                    out.println("NEWPLAYER" + name);
                                }
                            }
                            else if(input.startsWith("WIN"))
                            {
                                switch(input.substring(3))
                                {
                                    case "TOWN" :
                                        timeline.stop();
                                        information.setText("All of the Card Gang has been kicked.\nThe Town and any neutral still in the game wins!");
                                        win = true;
                                        break;
                                    case "GANG" :
                                        timeline.stop();
                                        information.setText("All of the town have been eliminated.\nThe Card Gang and any neutral still in the game wins!");
                                        win = true;
                                        break;
                                    case "WITHER":
                                        timeline.stop();
                                        information.setText("The wither gained unlimited power and kicked everyone off the server.\n");
                                        if(!originalState.equals(Status.WITH))
                                        {
                                            out.println("KICK" + name);
                                            state = Status.KICKED;
                                        }
                                        else
                                            out.println("SERV" + name + " won the game!");
                                        win = true;
                                        break;
                                }
                            }
                            else if (input.startsWith("KICKED"))
                            {
                                String finalInput = input;
                                Platform.runLater(() ->
                                {
                                    for(int x = 0; x < playerList.getItems().size(); x++)
                                    {
                                        if(((TextField)playerList.getItems().get(x)).getText().equals(finalInput.substring(6)))
                                        {
                                            kickedList.getItems().add(playerList.getItems().get(x));
                                            playerList.getItems().remove(x);
                                            break;
                                        }
                                    }
                                    try
                                    {
                                        for(int x = 0; x < gangList.getItems().size(); x++)
                                        {
                                            if(((TextField)gangList.getItems().get(x)).getText().equals(finalInput.substring(6)))
                                            {
                                                kickedList.getItems().add(gangList.getItems().get(x));
                                                gangList.getItems().remove(x);
                                                break;
                                            }
                                        }
                                    }
                                    catch(NullPointerException npe)
                                    {

                                    }
                                    for (Object item : kickedList.getItems())
                                    {
                                        ((TextField) item).setEditable(false);
                                        ((TextField) item).setMaxWidth(150);
                                    }
                                });

                            }
                            else if(input.equals("STATE"))
                            {
                                System.out.println(name + " State: " + state.name());
                                out.println("STATE" + state.name());
                            }
                            else if(input.startsWith("DEFENSE"))
                            {
                                phase = TimeCycle.DEFENSE;
                                if(input.substring(7).equals(name))
                                {
                                    state = Status.DFNS;
                                    canVote = false;
                                    information.setText(state.getDescription());
                                }
                                time = 20;
                                timeline.playFromStart();
                            }
                            else if(input.startsWith("LASTWORDS"))
                            {
                                phase = TimeCycle.LASTWORDS;
                                if(input.substring(9).equals(name))
                                {
                                    state = Status.KICKED;
                                    out.println("KICK" + name);
                                    information.setText(state.getDescription());
                                    out.println("SERVMay " + name + " enjoy the boot.");
                                }
                                time = 5;
                                timeline.playFromStart();
                            }
                            else if(input.startsWith("JUSTICED"))
                            {
                                if(input.substring(8, 12).equals("TOWN") && input.substring(12).equals(actingOn))
                                {
                                    information.setText("You kicked yourself because you kicked an innocent person!");
                                    out.println("SERV" + name + " kicked himself/herself because they kicked an innocent person!");
                                    out.println("KICKED" + name);
                                    state = Status.KICKED;
                                }
                            }
                            else if(input.startsWith("PROTECTED"))
                            {
                                if(actingOn != null && input.substring(9).equals(actingOn) && isProtecting)
                                {
                                    state = Status.KICKED;
                                    information.setText("You took the blow for someone and have been kicked!");
                                    out.println("SERV" + name + " took the blow for someone and got kicked!");
                                    out.println("KICKED" + name);
                                }
                            }
                            else if(input.startsWith("DONOTHING"))
                            {
                                if(input.substring(9).equals(name))
                                    gameChatText.appendText("You did nothing this night.\n");
                            }
                            else if (input.startsWith("KICK"))
                            {
                                if (input.substring(4).equals(name) && !alert)  //Make it so that it kicks the visitor on alert
                                {
                                    System.out.println("Is Healed: " + isHealed);
                                    if (isHealed)
                                    {
                                        gameChatText.appendText("The doctor saved you!");
                                        out.println("SERVSomeone was saved by another player!");
                                    }
                                    else if (blocked)
                                    {
                                        gameChatText.appendText("You blocked the attack!\n");
                                        out.println("SERVSomeone blocked an attack!");
                                    }
                                    else if (isProtected)
                                    {
                                        gameChatText.appendText("Someone took the hit for you!\n");
                                        out.println("PROTECTED" + name);
                                    }
                                    else if(mirrored)
                                    {
                                        gameChatText.appendText("You reflected the attack!\n");
                                        out.println("MIRROR" + name);
                                    }
                                    else
                                    {
                                        out.println("SERV" + input.substring(4) + " was kicked last night by someone in the game!");
                                        if(originalState.equals(Status.JEST))
                                            out.println("SERV" + input.substring(4) + " was the Jester and is one of the winners in this game!");
                                        gameChatText.appendText("You have been kicked!\n");
                                        state = Status.KICKED;
                                        out.println("KICKED" + name);
                                        information.setText(state.getDescription());
                                    }
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("VIGILANTE"))
                            {
                                String role;
                                if (input.substring(9).equals(name) && !alert)
                                {
                                    if (isHealed)
                                    {
                                        gameChatText.appendText("The doctor saved you!");
                                        out.println("SERVSomeone was saved by a doctor!");
                                    }
                                    else if (blocked)
                                    {
                                        gameChatText.appendText("You blocked the attack!\n");
                                        out.println("SERVSomeone blocked an attack!");
                                    }
                                    else if (isProtected)
                                    {
                                        gameChatText.appendText("Someone took the hit for you!\n");
                                        out.println("PROTECTED" + name);
                                    }
                                    else if(mirrored)
                                    {
                                        gameChatText.appendText("You reflected the attack!\n");
                                        out.println("MIRROR" + name);
                                    }
                                    else
                                    {
                                        out.println("SERV" + input.substring(9) + " was attacked with justice and kicked!");
                                        information.setText("KICKED\nKICKED\nKICKED\nKICKED");
                                        gameChatText.appendText("You have been kicked!\n");
                                        if(originalState.equals(Status.JEST))
                                            out.println("SERV" + input.substring(4) + " was the Jester and is one of the winners in this game!");
                                        role = state.name();
                                        if(!role.equals("TOWN") && !role.equals("GANG"))
                                            role = "NEUT";
                                        out.println("JUSTICED" + role + name);
                                        out.println("KICKED" + name);
                                        state = Status.KICKED;
                                    }
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("MIRROR"))
                            {
                                if(input.substring(6).equals(name))
                                {
                                    state = Status.KICKED;
                                    information.setText(state.getDescription());
                                    out.println("KICKED" + name);
                                    out.println("SERV" + name + " got hit by a mirrored attack!");
                                }
                            }
                            else if (input.startsWith("HEAL"))
                            {
                                if (input.substring(4).equals(name) && !alert)
                                {
                                    isHealed = true;
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("PROTECT"))
                            {
                                if(input.substring(7).equals(name) && !alert)
                                    isProtected = true;
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("INVESTIGATE"))
                            {
                                if (input.substring(11).equals(name) && !alert)
                                {
                                    if(!originalState.equals(Status.TOWN) || framed || fake)    //Do that for the other ones too
                                    {
                                        out.println("SERVThis person is suspicious");
                                    }
                                    else if(originalState.equals(Status.TOWN) || disguised)
                                        out.println("SERVThis person is not suspicious");
                                    else
                                        out.println("SERVThis person is suspicious");
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("ALERT"))
                            {
                                System.out.println("actingOn: " + actingOn);
                                if(actingOn != null)
                                {
                                    if(input.substring(5).equals(actingOn) && !state.equals(Status.KICKED));
                                    {
                                        gameChatText.appendText("You were kicked because the person you visited was on alert!\n");
                                        out.println("SERV" + name + " was kicked by someone on alert!");
                                        state = Status.KICKED;
                                        out.println("KICKED" + name);
                                    }
                                }
                            }
                            else if(input.startsWith("INTERROGATE"))
                            {
                                if(input.substring(11).equals(name) && !alert)
                                {
                                    state = Status.INTE;
                                    information.setText(state.getDescription());
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("JAIL") && !input.startsWith("JAILTEXT"))
                            {
                                if(input.substring(4).equals(name) && !alert)
                                {
                                    state = Status.JAIL;
                                    gameChatText.appendText("You are in jail now. You may only talk to the jailer.\n");
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }
                            else if(input.startsWith("FRAME"))
                            {
                                if(input.substring(5).equals(name))
                                {
                                    framed = true;
                                }
                            }
                            else if(input.startsWith("REVEAL"))
                            {
                                if (input.substring(6).equals(name) && !alert)
                                {
                                    if(!originalState.equals(Status.TOWN) || framed || fake)
                                    {
                                        out.println("SERVThis person's part of the Card Gang!");
                                    }
                                    else if(originalState.equals(Status.TOWN) || disguised)
                                        out.println("SERVThis person is a town member!");
                                    else
                                        out.println("SERVThis person is the " + originalState);
                                }
                                else if(alert)
                                    out.println("ALERT" + name);
                            }

                            else if (input.startsWith("CHANGE_CYCLE"))  //This should work I hope
                            {
                                if(!win)
                                {
                                    switch (input.substring(12))
                                    {
                                        case "DISCUSSION":
                                            playerHand.setDisable(true);
                                            day++;

                                            phase = TimeCycle.DISCUSSION;
                                            confirmAction.setDisable(false);
                                            confirmAction.setVisible(false);

                                            if (!state.equals(Status.KICKED))
                                            {
                                                information.setText(state.getDescription());

                                                if (day != 1)
                                                {
                                                    try
                                                    {
                                                        playerHand.getItems().add(deck.draw());
                                                        if (!confirmAction.isSelected())
                                                        {
                                                            Platform.runLater(() ->
                                                            {
                                                                Random rand = new Random();
                                                                playerHand.getItems().remove(rand.nextInt(playerHand.getItems().size()));
                                                            });
                                                        }
                                                    }
                                                    catch (NullPointerException npe)
                                                    {

                                                    }
                                                    if (output != null)
                                                        out.println(output);
                                                    output = null;
                                                }
                                            }

                                            playerList.setDisable(false);
                                            gameChatText.clear();
                                            gameChatBar.clear();
                                            time = 45;
                                            break;
                                        case "VOTING":
                                            phase = TimeCycle.VOTING;

                                            //Resetting booleans
                                            alert = false;
                                            isHealed = false;
                                            isProtected = false;
                                            mirrored = false;
                                            blocked = false;
                                            framed = false;
                                            disguised = false;
                                            fake = false;
                                            canVote = true;
                                            actingOn = null;

                                            if (!state.equals(Status.KICKED) && canVote)
                                            {
                                                state = originalState;
                                                information.setText("Vote who to put on trial.\nSelect his/her name and confirm action if you want to vote on them.\nSelect nothing and then hit confirm action to abstain.");
                                                confirmAction.setVisible(true);
                                                confirmAction.setSelected(false);
                                                confirmAction.setOnAction(e -> {
                                                    if (playerList.getSelectionModel().getSelectedItem() != null)
                                                    {
                                                        out.println("VOTE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                    }
                                                    playerList.setDisable(false);
                                                    confirmAction.setDisable(true);
                                                });
                                            }
                                            time = 30;
                                            break;
                                        case "JUDGEMENT":
                                            phase = TimeCycle.JUDGEMENT;
                                            if (!state.equals(Status.KICKED) && canVote)
                                            {
                                                information.setText("Make judgement of the one on trial.\n\nSelect his/her name and confirm action if you want to kick them.\nSelect nothing and then hit confirm action if you do not want to kick them.");
                                                confirmAction.setVisible(true);
                                                confirmAction.setSelected(false);
                                                confirmAction.setDisable(false);
                                                confirmAction.setOnAction(e -> {
                                                    //Make all sending judgement votes here
                                                    if (playerList.getSelectionModel().getSelectedItem() != null)
                                                    {
                                                        out.println("JUDGE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                    }
                                                    confirmAction.setDisable(true);
                                                });
                                            }
                                            time = 20;
                                            break;
                                        case "NIGHT":
                                            phase = TimeCycle.NIGHT;
                                            gameChatText.clear();
                                            gameChatBar.clear();
                                            confirmAction.setSelected(false);
                                            if (!state.equals(Status.KICKED))
                                            {
                                                information.setText("Perform one of your cards actions this night.\nLose the card if you use it.\n\nIf no card selected, lose a random card from your hand.");
                                                playerList.setDisable(false);
                                                confirmAction.setVisible(true);
                                                confirmAction.setDisable(false);
                                                playerHand.setDisable(false);
                                                confirmAction.setOnAction(e -> {
                                                    try
                                                    {
                                                        if (state.equals(Status.TOWN))
                                                        {
                                                            System.out.println("Here");
                                                            switch (((Townie) playerHand.getSelectionModel().getSelectedItem()).ability)
                                                            {
                                                                case HEAL:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    out.println("HEAL" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case JAIL:
                                                                    state = Status.JAIL;
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    out.println("JAIL" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case BLOCK:
                                                                    blocked = true;
                                                                    break;
                                                                case PROTECT:
                                                                    isProtecting = true;
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    out.println("PROTECT" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case DONOTHING:
                                                                    out.println("DONOTHING" + name);
                                                                    break;
                                                                case KICK:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "KICK" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                case ALERT:
                                                                    alert = true;
                                                                    break;
                                                                case VIGILANTE:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "VIGILANTE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                case INVESTIGATE:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "INVESTIGATE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                default:
                                                                    Platform.runLater(() ->
                                                                    {
                                                                        Random rand = new Random();
                                                                        playerHand.getItems().remove(rand.nextInt(playerHand.getItems().size()));
                                                                    });
                                                                    break;
                                                            }
                                                        }
                                                        else if (state.equals(Status.GANG))
                                                        {
                                                            switch (((CardGang) playerHand.getSelectionModel().getSelectedItem()).ability)
                                                            {
                                                                case FRAME:
                                                                    out.println("FRAME" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case MIRROR:
                                                                    mirrored = true;
                                                                    break;
                                                                case INVESTIGATE:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "INVESTIGATE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                case REVEAL:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "REVEAL" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                case INTERROGATE:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    out.println("INTERROGATE" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case DONOTHING:
                                                                    out.println("DONOTHING" + name);
                                                                    break;
                                                                case KICK:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "KICK" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                default:
                                                                    Platform.runLater(() ->
                                                                    {
                                                                        Random rand = new Random();
                                                                        playerHand.getItems().remove(rand.nextInt(playerHand.getItems().size()));
                                                                    });
                                                                    break;
                                                                //Add another gang ability called interrogate.  It's investigate but all of the gang talks with 1 person.
                                                            }
                                                        }
                                                        else
                                                        {
                                                            switch (((Neutral) playerHand.getSelectionModel().getSelectedItem()).ability)
                                                            {
                                                                case KICK:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    output = "KICK" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    break;
                                                                case BLOCK:
                                                                    blocked = true;
                                                                    break;
                                                                case FAKE:
                                                                    fake = true;
                                                                    break;
                                                                case HEAL:
                                                                    actingOn = ((TextField) playerList.getSelectionModel().getSelectedItem()).getText();
                                                                    out.println("HEAL" + ((TextField) playerList.getSelectionModel().getSelectedItem()).getText());
                                                                    break;
                                                                case DISGUISE:
                                                                    disguised = true;
                                                                    break;
                                                                case DONOTHING:
                                                                    out.println("DONOTHING" + name);
                                                                    break;
                                                                default:
                                                                    Platform.runLater(() ->
                                                                    {
                                                                        Random rand = new Random();
                                                                        playerHand.getItems().remove(rand.nextInt(playerHand.getItems().size()));
                                                                    });
                                                                    break;
                                                            }
                                                        }
                                                        playerHand.setDisable(true);
                                                        playerList.setDisable(true);
                                                        confirmAction.setDisable(true);
                                                        if (playerHand.getSelectionModel().getSelectedItem() != null && confirmAction.isSelected())
                                                        {
                                                            Platform.runLater(() -> playerHand.getItems().remove(playerHand.getSelectionModel().getSelectedItem()));
                                                        }
                                                    }
                                                    catch (NullPointerException npe)
                                                    {
                                                        if (playerHand.getSelectionModel().getSelectedItem() == null && !confirmAction.isSelected())
                                                        {
                                                            Platform.runLater(() ->
                                                            {
                                                                Random rand = new Random();
                                                                playerHand.getItems().remove(rand.nextInt(playerHand.getItems().size()));
                                                            });
                                                        }
                                                    }
                                                });
                                            }

                                            gameChatText.appendText("It is night...\n");
                                            time = 40;
                                            break;
                                        default:
                                            time = 45;
                                            break;
                                    }
                                    timeline.playFromStart();
                                }
                            }
                            else
                            {
                                if(!input.equals("STARTGAME"))
                                {
                                    if(input.startsWith("SERV"))
                                        gameChatText.appendText(input.substring(4) + "\n");
                                    else if (!phase.equals(TimeCycle.NIGHT))
                                        gameChatText.appendText(input + "\n");
                                    else
                                    {
                                        if((state.equals(Status.GANG) || state.equals(Status.INTE)) && (input.startsWith("GANGTEXT") || input.startsWith("INTETEXT")))
                                            gameChatText.appendText(input.substring(8) + "\n");
                                        else if(state.equals(Status.JAIL) && input.startsWith("JAILTEXT"))
                                            gameChatText.appendText(input.substring(8) + "\n");
                                    }
                                }
                            }
                        }
                        else
                            break;
                    }
                    else
                    {
                        if (!isHost)
                        {
                            if ((input = serverIn.readLine()) != null && !gameStart)
                            {
                                System.out.println(input);
                                if (input.equals("STARTGAME"))
                                {
                                    gameStart = true;
                                    Platform.runLater(() -> {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/gameMenu.fxml"));
                                        try
                                        {
                                            PreGameController.createGameMenu(loader);
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                                else if(input.startsWith("NEWNAME"))
                                {
                                    Random rand = new Random();
                                    if(input.substring(7).equals(name))
                                    {
                                        name = randNames[rand.nextInt(randNames.length)] + rand.nextInt(100);
                                        out.println("NEWPLAYER" + name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            finally
            {
                try
                {
                    this.socket.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public void setChatBar(TextField bar)
    {
        gameChatBar = bar;
    }
}