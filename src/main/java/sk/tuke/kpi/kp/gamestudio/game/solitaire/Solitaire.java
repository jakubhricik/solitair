package sk.tuke.kpi.kp.gamestudio.game.solitaire;

import sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi.ConsoleUi;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.GameBoard;

public class Solitaire {
    public static void main(String[] args) {

        GameBoard gameBoard = new GameBoard();
        ConsoleUi consoleUi = new ConsoleUi();

        consoleUi.play(gameBoard);
    }
}
