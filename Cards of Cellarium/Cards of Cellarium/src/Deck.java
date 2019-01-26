import Enums.RoleGroup;
import Enums.Status;

import java.util.ArrayList;
import java.util.Random;

public class Deck
{
    private ArrayList<AbilityCard> deck = new ArrayList<>();

    public int deckSize = deck.size();

    private RoleGroup deckGroup;

    /**This is the default constructor for the class
     */
    public Deck()
    {

    }

    public Deck(RoleGroup grouping, Status role)
    {
        deckGroup = grouping;
        switch(deckGroup)
        {
            case TOWN:
                for(int x = 0; x < 54; x++)
                {
                    if(x <= 3)
                    {
                        deck.add(new Townie(0));
                    }
                    else if(x > 3 && x <= 8)
                    {
                        deck.add(new Townie(1));
                    }
                    else if(x > 8 && x <= 17)
                    {
                        deck.add(new Townie(2));
                    }
                    else if(x > 17 && x <= 26)
                    {
                        deck.add(new Townie(3));
                    }
                    else if(x > 26 && x <= 44)
                    {
                        Random rand = new Random();
                        deck.add(new Townie(rand.nextInt(2) + 4));
                    }
                    else
                    {
                        deck.add(new Townie(8));
                    }
                }
                for(int x = 0; x < 6; x++)
                {
                    if(x < 3)
                    {
                        deck.add(new Townie(6));
                    }
                    else
                    {
                        deck.add(new Townie(7));
                    }
                }
                break;
            case CARDGANG:
                for(int x = 0; x < 60; x++)
                {
                    if(x <= 3)
                    {
                        deck.add(new CardGang(0));
                    }
                    else if(x > 3 && x <= 8)
                    {
                        deck.add(new CardGang(1));
                    }
                    else if(x > 8 && x <= 17)
                    {
                        deck.add(new CardGang(2));
                    }
                    else if(x > 17 && x <= 31)
                    {
                        Random rand = new Random();
                        deck.add(new CardGang(rand.nextInt(3) + 3));
                    }
                    else
                    {
                        deck.add(new CardGang(6));
                    }
                }
                break;
            case NEUTRAL:
                for(int x = 0; x < 60; x++)
                {
                    if(x <= 5)
                    {
                        deck.add(new Neutral(0));
                    }
                    else if(x > 5 && x <= 11)
                    {
                        deck.add(new Neutral(1));
                    }
                    else if(x > 11 && x <= 35)
                    {
                        switch(role)
                        {
                            case APOT:
                                if(x <= 23)
                                    deck.add(new Neutral(2));
                                else
                                    deck.add(new Neutral(0));
                                break;
                            case SKIK:
                                if(x <= 23)
                                    deck.add(new Neutral(4));
                                else
                                    deck.add(new Neutral(0));
                                break;
                            case JEST:
                                if(x <= 23)
                                    deck.add(new Neutral(3));
                                else
                                    deck.add(new Neutral(1));
                                break;
                            case WITH:
                                if(x <= 23)
                                    deck.add(new Neutral(4));
                                else
                                    deck.add(new Neutral(1));
                                break;
                        }
                    }
                    else
                    {
                        deck.add(new Neutral(5));
                    }
                }
                break;
            default:
                break;
        }
        shuffle();
    }

    private void shuffle()
    {
        Random rand = new Random();
        ArrayList<AbilityCard> tempDeck = new ArrayList<>();

        for(AbilityCard card : deck)
        {
            tempDeck.add(card);
        }
        deck.clear();
        while(!tempDeck.isEmpty())
        {
            try
            {
                AbilityCard tempCard = tempDeck.get(rand.nextInt(tempDeck.size()));
                deck.add(tempCard);
                tempDeck.remove(tempCard);
            }
            catch(NullPointerException npe)
            {

            }
        }
    }

    public AbilityCard draw()
    {
        AbilityCard card = deck.get(0);
        deck.remove(card);
        return card;
    }
}