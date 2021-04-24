package sk.tuke.kpi.kp.gamestudio.game.solitaire;

import sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi.ConsoleUi;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.Card;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.CardStack;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.GameBoard;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;

public class Test{

    public static void main(String[] args) {

        CardStack stock = new CardStack(StackType.STOCK);
        CardStack talon = new CardStack(StackType.TALON);
        CardStack [] tableau = new CardStack[7];
        CardStack [] foundations = new CardStack[4];

        for (int i = 0 ; i < 7 ; i++) {
            tableau[i] = new CardStack(StackType.TABLEAU);
        }

        for (int i = 0 ; i < 4 ; i++) {
            foundations[i] = new CardStack(StackType.FOUNDATION);
        }


        int pile = 0;
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(rank, suit);
                card.setFacingUp(true);
                foundations[pile].push(card);
            }
            pile ++;
        }

        Card card = foundations[3].pop();

        tableau[1].push(card);

//        GameBoard gameBoard = new GameBoard(false ,stock, talon, foundations, tableau);
//        ConsoleUi consoleUi = new ConsoleUi();

//        consoleUi.play(gameBoard);

    }
}
