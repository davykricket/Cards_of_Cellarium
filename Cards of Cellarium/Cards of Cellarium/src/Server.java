import Enums.TimeCycle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Server
{
    private static final int PORT = 6754;
    private static ArrayList<String> names = new ArrayList<String>();

    private static ServerSocket listener;

    private static ArrayList<ClientThread> clients = new ArrayList<>();

    private static TimeCycle phase;

    private static ArrayList<String> gameMakeUp = new ArrayList<>();

    private static boolean areRolesDistrib = false;

    public static int day = 0;

    private static int townAlive, gangAlive, clientsChecked;

    private static boolean gameStart = false;

    private static boolean witherAlive;

    private static String judged;

    private static ArrayList<Integer> votes = new ArrayList<>();


    public static void main(String[] args) throws IOException
    {
        listener = null;
        phase = TimeCycle.DISCUSSION;

        System.out.println(new Date() + "\nServer Online.\n");
        try
        {
            System.out.println(PORT);
            listener = new ServerSocket(PORT);
            acceptClients();
        }
        catch (IOException ioe)
        {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
    }

    public static void acceptClients()
    {
        while (true)
        {
            try
            {
                Socket socket = listener.accept();
                ClientThread client = new ClientThread(socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);
            }
            catch (IOException ioe)
            {
                System.out.println("Accept failed on: " + PORT);
            }
        }
    }

    public static class ClientThread extends Server implements Runnable
    {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientThread(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (!socket.isClosed())
                {
                    System.out.println("Loop");
                    System.out.println("Clients size: " + clients.size());
                    String input = in.readLine();
                    System.out.println("input: " + input + " isClosed: " + socket.isClosed());

                    if (input != null)
                    {
                        if (input.startsWith("NEWPLAYER"))
                        {
                            boolean changedName = false;
                            for(int x = 0; x < names.size(); x++)
                            {
                                if(names.get(x).equals(input.substring(9)))
                                {
                                    names.remove(x);
                                    for(ClientThread client : clients)
                                    {
                                        client.getWriter().println("NEWNAME" + input.substring(9));
                                    }
                                    clients.add(clients.get(x));
                                    clients.remove(x);
                                    changedName = true;
                                    break;
                                }
                            }
                            if(!changedName)
                            {
                                names.add(input.substring(9));
                                votes.add(0);
                            }
                        }
                        else if (input.startsWith("STARTGAME"))
                        {
                            Random rand = new Random();
                            ArrayList<String> roles = new ArrayList<>();
                            int index;
                            for (ClientThread client : clients)
                            {
                                client.getWriter().println("STARTGAME");
                                gameStart = true;
                                System.out.println("Are Roles Distrib: " + areRolesDistrib);
                                if (!areRolesDistrib)
                                    distributeRoles();

                                index = rand.nextInt(gameMakeUp.size());
                                client.getWriter().println("DISTRIBROLE" + gameMakeUp.get(index));
                                roles.add(gameMakeUp.get(index));
                                if(!roles.get(roles.size() - 1).equals("TOWN") && !roles.get(roles.size() - 1).equals("GANG"))
                                    roles.set(roles.size() - 1, "NEUT");
                                gameMakeUp.remove(index);
                                areRolesDistrib = true;
                            }

                            for(ClientThread client : clients)
                            {
                                for (int x = 0; x < names.size(); x++)
                                {
                                    client.getWriter().println("ADDPLAYER" + roles.get(x) + names.get(x));
                                }
                                client.getWriter().println("START_TIMER");
                            }
                        }
                        else if(input.startsWith("STATE"))
                        {
                            switch (input.substring(5))
                            {
                                case "TOWN":
                                    townAlive++;
                                    break;
                                case "GANG":
                                    gangAlive++;
                                    break;
                                case "WITH":
                                    witherAlive = true;
                                    break;
                            }
                            clientsChecked++;
                            System.out.println("Clients Checked: " + clientsChecked);
                            if(clientsChecked == clients.size())
                                checkWins();
                        }
                        else if (input.startsWith("VOTE"))   //Use this for voting who to lynch
                        {
                            for (int x = 0; x < names.size(); x++)
                            {
                                if (input.substring(4).equals(names.get(x)))
                                {
                                    votes.set(x, votes.get(x) + 1);
                                }
                            }
                        }
                        else if (input.startsWith("JUDGE"))  //Use this for judgement for lynching
                        {
                            if(input.substring(5).equals(judged))
                            {
                                for(int x = 0; x < names.size(); x++)
                                {
                                    if(judged.equals(names.get(x)))
                                    {
                                        votes.set(x, votes.get(x) + 1);
                                    }
                                }
                            }
                        }
                        else if(input.startsWith("MIRROR"))
                        {
                            Random rand = new Random();
                            boolean diffPerson = false;
                            String nameOutput = names.get(rand.nextInt(names.size()));
                            while(!diffPerson)
                            {
                                if(nameOutput.equals(input.substring(6)))
                                    nameOutput = names.get(rand.nextInt(names.size()));
                                else
                                    diffPerson = true;
                            }
                            for(ClientThread client : clients)
                            {
                                client.getWriter().println("MIRROR" + nameOutput);
                            }
                        }
                        else if (input.equals("TIMER_STOPPED"))
                        {
                                switch (phase)   //This is checked at the end of the cycle so output a message to collect data here
                                {
                                    case DISCUSSION:
                                        day++;
                                        if (day != 1)
                                            phase = TimeCycle.VOTING;
                                        else if (day <= 1)
                                            phase = TimeCycle.NIGHT;
                                        witherAlive = false;
                                        clientsChecked = 0;
                                        townAlive = 0;
                                        gangAlive = 0;
                                        checkAlive();
                                        changeCycles();
                                        break;
                                    case VOTING:
                                        for (int x = 0; x < votes.size(); x++)
                                        {
                                            if (votes.get(x) > votes.size() / 2)
                                            {
                                                for (ClientThread client : clients)
                                                {
                                                    judged = names.get(x);
                                                    client.getWriter().println("DEFENSE" + names.get(x));
                                                    phase = TimeCycle.DEFENSE;
                                                }
                                                if(phase.equals(TimeCycle.DEFENSE))
                                                {
                                                    break;
                                                }
                                            }
                                        }
                                        for (int x = 0; x < votes.size(); x++)
                                        {
                                            votes.set(x, 0);
                                        }
                                        if(phase.equals(TimeCycle.VOTING))
                                        {
                                            phase = TimeCycle.NIGHT;
                                            changeCycles();
                                        }
                                        break;
                                    case DEFENSE:
                                        phase = TimeCycle.JUDGEMENT;
                                        changeCycles();
                                        break;
                                    case JUDGEMENT:
                                        for (int x = 0; x < votes.size(); x++)
                                        {
                                            if (votes.get(x) > votes.size() / 2)
                                            {
                                                for (ClientThread client : clients)
                                                {
                                                    client.getWriter().println("LASTWORDS" + names.get(x));
                                                    phase = TimeCycle.LASTWORDS;
                                                }
                                                if(phase.equals(TimeCycle.LASTWORDS))
                                                {
                                                    break;
                                                }
                                            }
                                        }
                                        for (int x = 0; x < votes.size(); x++)
                                        {
                                            votes.set(x, 0);
                                        }
                                        if(phase.equals(TimeCycle.JUDGEMENT))
                                        {
                                            phase = TimeCycle.NIGHT;
                                            changeCycles();
                                        }
                                        break;
                                    case LASTWORDS:
                                        witherAlive = false;
                                        clientsChecked = 0;
                                        townAlive = 0;
                                        gangAlive = 0;
                                        checkAlive();
                                        phase = TimeCycle.NIGHT;
                                        changeCycles();
                                        break;
                                    case NIGHT:
                                        phase = TimeCycle.DISCUSSION;
                                        changeCycles();
                                        break;
                                    default:
                                        phase = TimeCycle.DISCUSSION;
                                        changeCycles();
                                        break;
                                }
                        }
                        else
                            for (ClientThread client : clients)
                            {
                                client.getWriter().println(input);
                            }

                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch (IOException ioe)
            {
                System.out.println("In IOException");
                ioe.printStackTrace();
            }
            finally
            {
                try
                {
                    Iterator<ClientThread> clientIterator = clients.iterator();


                    socket.close();
                    while (clientIterator.hasNext())
                    {
                        ClientThread iterated = clientIterator.next();

                        if (iterated.socket.isClosed())
                        {
                            clientIterator.remove();
                        }
                    }

                    if (clients.size() == 0)
                    {
                        System.exit(0);
                    }
                    System.out.println("Clients Size: " + clients.size());
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }

        public PrintWriter getWriter()
        {
            return out;
        }
    }

    public void distributeRoles()   //Test this and see if it all works correctly; there are things that can be changed in the future
    {
        if (clients.size() <= 6) //if there are 8 or less players, there will only be town and gang
        {
            for (int x = 0; x < (clients.size()); x++)
            {
                if (x < ((clients.size()) * .5))
                {
                    gameMakeUp.add("TOWN");
                }
                else
                {
                    gameMakeUp.add("GANG");
                }
            }
        }
        else if (clients.size() > 6 && clients.size() <= 12) //between 9 and 15 players, there will be 2 neutral and the rest will be split between town and gang
        {
            for (int x = 0; x < (clients.size() - 2); x++)
            {
                if (x < ((clients.size() - 2) * .5))
                {
                    gameMakeUp.add("TOWN");
                }
                else if (x >= ((clients.size() - 2) * .5) && x < (clients.size() - 2))
                {
                    gameMakeUp.add("GANG");
                }
            }
            for (int x = 0; x < 2; x++)
            {
                chooseNeutral();
            }
        }
        else if (clients.size() > 12) //more than 15, then all 8 neutrals will be played
        {
            for (int x = 0; x < (clients.size() - 4); x++)
            {
                if (x < (clients.size() - 4) * .5)
                    gameMakeUp.add("TOWN");
                else if (x >= ((clients.size() - 4) * .5) && x < (clients.size() - 4))
                    gameMakeUp.add("GANG");
            }
            gameMakeUp.add("JEST");
            gameMakeUp.add("SKIK");
            gameMakeUp.add("APOT");
            gameMakeUp.add("WITH");
        }
    }

    private void chooseNeutral()
    {
        Random rand = new Random();
        switch (rand.nextInt(4))
        {
            case 0:
                gameMakeUp.add("APOT");
                break;
            case 1:
                gameMakeUp.add("JEST");
                break;
            case 2:
                gameMakeUp.add("SKIK");
                break;
            case 3:
                gameMakeUp.add("WITH");
                break;
            default:
                gameMakeUp.add("JEST");
                break;
        }
    }

    private static void changeCycles()
    {
        for (ClientThread client : clients)
        {
            client.getWriter().println("CHANGE_CYCLE" + phase.name());
        }
    }

    public static String getIP()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException uhe)
        {
            uhe.printStackTrace();
            return null;
        }
    }

    public static void checkAlive()
    {
        for(ClientThread client : clients)
        {
            client.getWriter().println("STATE");
        }
    }

    public static void checkWins()
    {
        System.out.println("Town Alive: " + townAlive);
        System.out.println("Gang Alive: " + gangAlive);
        System.out.println("Wither Alive: " + witherAlive);
        if(day >= 5 && witherAlive)
            for(ClientThread client : clients)
                client.getWriter().println("WINWITHER");
         else if(gangAlive <= 0 && townAlive >= 0)
            for(ClientThread client : clients)
                client.getWriter().println("WINTOWN");
        else if(gangAlive >= 0 && townAlive <= 0)
            for(ClientThread client : clients)
                client.getWriter().println("WINGANG");
    }
}

