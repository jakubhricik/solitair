package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import org.springframework.http.server.DelegatingServerHttpResponse;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.GameState;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.GamestudioException;

import java.io.*;
import java.util.Random;

public class GameBoard implements Serializable{

    private static final String SAVED_GAME = "save.bin";

    public final int SIZE_OF_STOCK = 24;
    public final int SIZE_OF_DECK = 52;
    public final int TABLEAU_PILE_COUNT = 7;
    public final int FOUNDATION_PILE_COUNT = 4;


    private GameState gameState;
    private boolean hardMode = false;

    private final Card[] cardsDeck = new Card[SIZE_OF_DECK];

    private CardStack stock;
    private CardStack talon;
    private final CardStack[] foundations;
    private final CardStack[] tableau;

    private GameBoard savedState;
    private int savedScore = 10000;

    private final Random random = new Random();

    private int countOfPlayerInteractions;
    private boolean gameStarted;


    public GameBoard() {
        gameStarted = false;
        stock = new CardStack(StackType.STOCK);
        talon = new CardStack(StackType.TALON);
        foundations = new CardStack[FOUNDATION_PILE_COUNT];
        tableau = new CardStack[TABLEAU_PILE_COUNT];

        makeDeck();
        prepareGame();
    }

    private GameBoard(boolean gameStarted, CardStack stock, CardStack talon, CardStack[] foundations, CardStack[] tableau){
        this.gameStarted = gameStarted;
        this.stock = stock;
        this.talon = talon;
        this.foundations = foundations;
        this.tableau = tableau;
    }


    /**
     * Move one card from From Stack Type to destination Stack Type
     *
     * @param from            Stack type of stack, that we wanted take card
     * @param fromPile        number of from stack pile
     * @param destination     Stack type of stack, that we wanted to put card
     * @param destinationPile number of destination stack pile
     */
    public void moveCard(StackType from, int fromPile, StackType destination, int destinationPile) {
        checkArguments(from, fromPile, destination, destinationPile);

        //from talon
        if (from == StackType.TALON) {
            if (destination == StackType.TABLEAU) {
                talonToTableau(destinationPile);
            } else if (destination == StackType.FOUNDATION) {
                talonToFoundations(destinationPile);
            }
        }

        //from tableau
        else if (from == StackType.TABLEAU) {
            if (destination == StackType.FOUNDATION) {
                tableauToFoundations(fromPile, destinationPile);
            } else if (destination == StackType.TABLEAU) {
                moveCardsInTableau(fromPile, destinationPile, 1);
            }
            if (!tableau[fromPile].isEmpty()) tableau[fromPile].peek().setFacingUp(true);
        }

        //from foundations
        else if (from == StackType.FOUNDATION && destination == StackType.TABLEAU) {
            foundationsToTableau(fromPile, destinationPile);
        } else throw new IllegalStateException("Illegal arguments combinations");


        if (destination == StackType.FOUNDATION)
            updateGameState();

    }


    /**
     * Checking if arguments are correct, if not throw exception
     *
     * @param from            Stack type of stack
     * @param fromPile        number of stack type
     * @param destination     Stack type of stack
     * @param destinationPile number of stack type
     */
    private void checkArguments(StackType from, int fromPile, StackType destination, int destinationPile) {
        if (destination == StackType.STOCK || from == StackType.STOCK || destination == StackType.TALON)
            throw new IllegalArgumentException("Illegal argument combination");

        if (from == StackType.FOUNDATION) {
            if (fromPile < 0 || fromPile > FOUNDATION_PILE_COUNT - 1)
                throw new IllegalArgumentException("Illegal argument in pile, choose from 0 to 3");
        } else if (from == StackType.TABLEAU) {
            if (fromPile < 0 || fromPile > TABLEAU_PILE_COUNT - 1)
                throw new IllegalArgumentException("Illegal argument in tableau, choose from 0 to 6");
        }

        if (destination == StackType.FOUNDATION) {
            if (destinationPile < 0 || destinationPile > FOUNDATION_PILE_COUNT - 1)
                throw new IllegalArgumentException("Illegal argument in pile, choose from 0 to 3");
        } else if (destination == StackType.TABLEAU) {
            if (destinationPile < 0 || destinationPile > TABLEAU_PILE_COUNT - 1)
                throw new IllegalArgumentException("Illegal argument in tableau, choose from 0 to 6");
        }
    }


    private void updateGameState() {
        int numberOfFullFoundations = 0;

        for (int pileNum = 0; pileNum < FOUNDATION_PILE_COUNT; pileNum++) {
            numberOfFullFoundations += getFoundations(pileNum).getSize();
        }

        if (numberOfFullFoundations == SIZE_OF_DECK) gameState = GameState.SOLVED;
    }


    //moving cards

    /**
     * Move number of cards from one pile to second pile in tableau
     *
     * @param fromPile        number of tableau pile where from we take number of cards
     * @param destinationPile number of tableau pile where we wanted to move number of cards
     * @param numberOfCards   number of cards we wanted to move
     */
    public void moveCardsInTableau(int fromPile, int destinationPile, int numberOfCards) {

        if (fromPile > TABLEAU_PILE_COUNT - 1 || destinationPile > TABLEAU_PILE_COUNT - 1 || fromPile < 0 || destinationPile < 0 || fromPile == destinationPile)
            throw new IllegalArgumentException("Illegal argument combination");

        if (tableau[fromPile] == null || tableau[destinationPile] == null)
            throw new IllegalStateException("Problem with piles");

        if (tableau[fromPile].getSize() < numberOfCards)
            throw new IllegalArgumentException("Illegal number of cards");

        CardStack helpStack = new CardStack(StackType.STOCK);

        for (int i = 0; i < numberOfCards; i++) {

            if (!tableau[fromPile].peek().isFacingUp()) {
                while (!helpStack.isEmpty()) {
                    tableau[fromPile].push(helpStack.pop());
                }
                throw new IllegalArgumentException("Cannot move uncovered cards");
            }

            helpStack.push(tableau[fromPile].pop());
        }

        if (tableau[destinationPile].isEmpty() && helpStack.peek().getRank() == Rank.KING) {
            while (!helpStack.isEmpty()) {
                tableau[destinationPile].push(helpStack.pop());
            }
            countOfPlayerInteractions++;
        } else if (helpStack.peek().getRank().getValue() == tableau[destinationPile].peek().getRank().getValue() - 1
                && helpStack.peek().getSuit() != tableau[destinationPile].peek().getSuit()) {
            while (!helpStack.isEmpty()) {
                tableau[destinationPile].push(helpStack.pop());
            }
            countOfPlayerInteractions++;
        } else {
            while (!helpStack.isEmpty()) {
                tableau[fromPile].push(helpStack.pop());
            }
            countOfPlayerInteractions++;
        }
        if (!tableau[fromPile].isEmpty()) tableau[fromPile].peek().setFacingUp(true);
    }


    /**
     * Move card from tableau pile to foundation pile
     *
     * @param tableauPile    number of pile in tableau
     * @param foundationPile number of pile in foundation
     */
    private void tableauToFoundations(int tableauPile, int foundationPile) {

        if (foundations == null || tableau == null)
            throw new IllegalStateException("Problem with tableau or foundations");

        Card topFoundationPileCard;
        Card topTableauPileCard;

        try {
            topTableauPileCard = tableau[tableauPile].peek();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Tableau pile is empty");
        }

        try {
            topFoundationPileCard = foundations[foundationPile].peek();

            if (topFoundationPileCard.getRank().getValue() == topTableauPileCard.getRank().getValue() - 1
                    && topFoundationPileCard.getSuit() == topTableauPileCard.getSuit()) {
                moveCard(tableau[tableauPile], foundations[foundationPile]);
            } else {
                throw new IllegalStateException("Cannot move tableau card to foundation pile " + foundationPile);
            }


        } catch (IllegalStateException e) {
            if (topTableauPileCard.getRank() == Rank.ACE && e.getMessage().equals("Cannot peek, card stock is empty")) {
                moveCard(tableau[tableauPile], foundations[foundationPile]);
            } else throw e;
        }
    }


    /**
     * Move card from talon to foundation pile
     *
     * @param pile number of pile
     */
    private void talonToFoundations(int pile) {

        if (foundations == null || talon == null)
            throw new IllegalStateException("Problem with talon or foundations");

        Card topFoundationPileCard;
        Card topTalonCard;

        try {
            topTalonCard = talon.peek();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Talon pile is empty");
        }

        try {
            topFoundationPileCard = foundations[pile].peek();

            if (topFoundationPileCard.getRank().getValue() == topTalonCard.getRank().getValue() - 1
                    && topFoundationPileCard.getSuit().getColor() == topTalonCard.getSuit().getColor()) {
                moveCard(talon, foundations[pile]);
            } else {
                throw new IllegalStateException("Cannot move talon card to foundation pile " + pile);
            }


        } catch (IllegalStateException e) {
            if (topTalonCard.getRank() == Rank.ACE && e.getMessage().equals("Cannot peek, card stock is empty")) {
                moveCard(talon, foundations[pile]);
            } else throw e;
        }

    }


    /**
     * Move card from talon to tableau pile
     *
     * @param pile number of pile
     */
    private void talonToTableau(int pile) {

        if (tableau == null || talon == null)
            throw new IllegalStateException("Problem with tableau or talon");

        Card topTableauPileCard;
        Card topTalonCard;

        try {
            topTalonCard = talon.peek();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Talon pile is empty");
        }

        try {
            topTableauPileCard = tableau[pile].peek();

            if (topTableauPileCard.getRank().getValue() == topTalonCard.getRank().getValue() + 1
                    && topTableauPileCard.getSuit().getColor() != topTalonCard.getSuit().getColor()) {
                moveCard(talon, tableau[pile]);
            } else {
                throw new IllegalStateException("Cannot move talon card to tableau pile " + pile);
            }


        } catch (IllegalStateException e) {
            if (topTalonCard.getRank() == Rank.KING && e.getMessage().equals("Cannot peek, card stock is empty")) {
                moveCard(talon, tableau[pile]);
            } else throw e;
        }

    }


    /**
     * Move card from foundation pile to tableau pile
     *
     * @param foundationPile number of foundation pile
     * @param tableauPile    number of tableau pile
     */
    private void foundationsToTableau(int foundationPile, int tableauPile) {

        if (foundations == null || tableau == null)
            throw new IllegalStateException("Problem with tableau or foundations");

        Card topFoundationPileCard;
        Card topTableauPileCard;

        try {
            topFoundationPileCard = foundations[foundationPile].peek();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Foundation pile is empty");
        }

        try {
            topTableauPileCard = tableau[tableauPile].peek();

            if (topFoundationPileCard.getRank().getValue() == topTableauPileCard.getRank().getValue() - 1
                    && topFoundationPileCard.getSuit().getColor() != topTableauPileCard.getSuit().getColor()) {
                moveCard(foundations[foundationPile], tableau[tableauPile]);
            } else {
                throw new IllegalStateException("Cannot move foundation card to tableau pile " + tableauPile);
            }


        } catch (IllegalStateException e) {
            if (topFoundationPileCard.getRank() == Rank.KING && e.getMessage().equals("Cannot peek, card stock is empty")) {
                moveCard(foundations[foundationPile], tableau[tableauPile]);
            } else throw e;
        }
    }


    /**
     * Move card between two card stacks
     *
     * @param from        card stack from we take card
     * @param destination card stack where we added card
     */
    private void moveCard(CardStack from, CardStack destination) {

        if (from == null || destination == null)
            throw new IllegalArgumentException("There are problem with stacks");
        if (from.isEmpty())
            throw new IllegalStateException("First pile is empty");

        //move card from talon to tableau
        Card chosenCard = from.pop();
        destination.push(chosenCard);
        countOfPlayerInteractions++;
    }


    //STOCK and TALON functions

    /**
     * Deal 1 card from stock to talon
     */
    public void deal() {
        if (stock == null || talon == null) throw new IllegalStateException("Stock or talon doesnt exist.");
        if (stock.isEmpty()) throw new IllegalStateException("Stock is empty.");

        if (hardMode) {
            for (int turnedCardsCount = 0; turnedCardsCount < 3; turnedCardsCount++) {
                if (!stock.isEmpty()) {
                    Card pickedCard = stock.pop();
                    pickedCard.setFacingUp(true);
                    talon.push(pickedCard);
                }
            }
        } else {
            Card pickedCard = stock.pop();
            pickedCard.setFacingUp(true);
            talon.push(pickedCard);
        }
        countOfPlayerInteractions++;
    }

    /**
     * If stock is empty and talon is not, it will reload stock
     * by move each card from talon to stock
     */
    public void reloadStock() {
        if (!stock.isEmpty()) throw new IllegalStateException("Stock is not empty");
        if (talon.isEmpty()) throw new IllegalStateException("Talon is empty");

        while (!talon.isEmpty()) {
            Card pickedCard = talon.pop();
            pickedCard.setFacingUp(false);
            stock.push(pickedCard);
        }
    }


    //functions to prepare game, and work with deck

    /**
     * Preparing game ,
     * fill tableau and stock with cards,
     * setting tableau top cards as facing up.
     */
    private void prepareGame() {

        //fill tableau
        for (int i = 0; i < TABLEAU_PILE_COUNT; i++) {
            tableau[i] = new CardStack(StackType.TABLEAU);
            shuffleAndFillStack(i + 1, tableau[i]);
        }

        //fill stock
        shuffleAndFillStack(SIZE_OF_STOCK, stock);

        //face up top cards in tableau
        for (CardStack cardStack : tableau) {
            cardStack.peek().setFacingUp(true);
        }

        //initialize foundations stacks
        for (int i = 0; i < FOUNDATION_PILE_COUNT; i++) {
            foundations[i] = new CardStack(StackType.FOUNDATION);
        }

        quickSave();
    }


    /**
     * File stack with number of cards from deck
     *
     * @param numberOfCards number represent how many cards you want to add to the stack
     * @param stack         stack where you want to add cards
     */
    private void shuffleAndFillStack(int numberOfCards, CardStack stack) {

        if (stack == null)
            throw new IllegalArgumentException("Cannot fill null stack");
        if (numberOfCards < 1)
            throw new IllegalArgumentException("Cannot fill stack with 0 and < cards");
        if (numberOfCards > getNumOfCardsInDeck())
            throw new IllegalArgumentException("In deck is not enough cards");

        for (int i = 0; i < numberOfCards; i++) {
            int randomNumber = random.nextInt(SIZE_OF_DECK) + 1;
            try {
                stack.push(takeCardFromDeck(randomNumber));
            } catch (IllegalStateException e) {
                //in deck on randomNumber is no cards
                i--;
            }
        }
    }

    /**
     * Generate deck of 52 cards, and store it in cardsDeck
     */
    private void makeDeck() {
        int cardInDeck = 0;

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cardsDeck[cardInDeck] = new Card(rank, suit);
                cardInDeck++;
            }
        }
    }

    /**
     * Picking card from deck, by number
     *
     * @param numberOfCard int number, represent number of card 1 - 52 from deck
     *                     it is position in the deck
     * @return Card from deck
     */
    private Card takeCardFromDeck(int numberOfCard) {
        if (numberOfCard > SIZE_OF_DECK || numberOfCard < 1)
            throw new IndexOutOfBoundsException("Number of card is bigger than deck.");

        if (cardsDeck[numberOfCard - 1] == null)
            throw new IllegalStateException("Card with number " + numberOfCard + " is out of deck.");

        Card cartWePick = cardsDeck[numberOfCard - 1];
        cardsDeck[numberOfCard - 1] = null;

        return cartWePick;
    }


    /**
     * Count number of Card objects in deck
     *
     * @return number of cards that are currently in deck
     */
    private int getNumOfCardsInDeck() {
        int resultNumber = 0;
        for (Card card : cardsDeck) {
            if (card != null) resultNumber++;
        }
        return resultNumber;
    }

    /**
     * Save state of game in savedState variable,
     * than you are able to quick load game
     */
    public void quickSave(){
        CardStack[] foundationsCopy = new CardStack[FOUNDATION_PILE_COUNT];
        CardStack[] tableauCopy = new CardStack[TABLEAU_PILE_COUNT];

        for(int pileNumber = 0; pileNumber < FOUNDATION_PILE_COUNT; pileNumber++){
            foundationsCopy[pileNumber] = getFoundations(pileNumber).clone();
        }

        for(int pileNumber = 0; pileNumber < TABLEAU_PILE_COUNT; pileNumber++){
            tableauCopy[pileNumber] = getTableau(pileNumber).clone();
        }

        savedState = new GameBoard(this.gameStarted, this.stock.clone(), this.talon.clone(),foundationsCopy, tableauCopy);
    }

    /**
     * Quick load, replace current game state with state that is saved
     */
    public void quickLoad(){
        if(savedState == null) throw new IllegalStateException("Game not saved jet. Cannot load");
        this.gameStarted = savedState.gameStarted;
        this.stock = savedState.stock.clone();
        this.talon = savedState.talon.clone();

        for(int pileNumber = 0; pileNumber < FOUNDATION_PILE_COUNT; pileNumber++){
            this.foundations[pileNumber] = savedState.getFoundations(pileNumber).clone();
        }

        for(int pileNumber = 0; pileNumber < TABLEAU_PILE_COUNT; pileNumber++){
            this.tableau[pileNumber] = savedState.getTableau(pileNumber).clone();
        }
    }

    /**
     * save game to the save.bin file
     */
    public void saveGame() {
        savedScore = getScore();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVED_GAME))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new GamestudioException("Error saving game", e);
        }
    }

    /**
     * load game from save.bin file if exist
     * @return GameBoard of saved game
     */
    public static GameBoard loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVED_GAME))) {
            GameBoard loadedGame = (GameBoard) ois.readObject();
            loadedGame.countOfPlayerInteractions = 0;
            return loadedGame;
        } catch (Exception e) {
            throw new GamestudioException("Error loading game", e);
        }
    }


    /**
     * After calling this function the timer start count millis, this is important for counting score
     */
    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            gameState = GameState.PLAYING;
            countOfPlayerInteractions = 0;
        }
    }

    public CardStack getStock() {
        return stock.clone();
    }

    public CardStack getTalon() {
        return talon.clone();
    }

    public CardStack getFoundations(int pileNum) {
        if (pileNum > FOUNDATION_PILE_COUNT - 1 || pileNum < 0) throw new IllegalArgumentException("Illegal pile num");
        return foundations[pileNum].clone();
    }

    public CardStack getTableau(int pileNum) {
        if (pileNum > TABLEAU_PILE_COUNT - 1 || pileNum < 0) throw new IllegalArgumentException("Illegal pile num");
        return tableau[pileNum].clone();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isHardMode() {
        return hardMode;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }

    public void surrender() {
        gameState = GameState.SURRENDER;
    }

    public int getScore() {
        int result;

        if(isHardMode())
            result =  savedScore - (2 * countOfPlayerInteractions);
        else
            result =  savedScore - (5 * countOfPlayerInteractions);

        if (result < 0) result = 0;
        return result;
    }
}
