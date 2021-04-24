package sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.GameState;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.GamestudioException;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.RatingException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.CardStack;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.GameBoard;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi.Colors.*;


public class ConsoleUi {

    private final boolean COLORIZED_OUTPUT = true;
    public final String GAME = "solitaire";

    private GameBoard gameBoard;
    private final Scanner scanner = new Scanner(System.in);

    private static final Pattern COMMAND_PATTERN_ONE_CARD = Pattern.compile("(M) ([TAFN]+)([1-9])? ([TAFN]+)([1-9])");
    private static final Pattern COMMAND_PATTERN_MORE_CARD = Pattern.compile("(M)([1-9]) ([1-9]) ([1-9])");

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private CommentService commentService;

    private CardStack stock;
    private CardStack talon;
    private final CardStack[] foundations = new CardStack[4];
    private final CardStack[] tableau = new CardStack[7];


    public void play(GameBoard gameBoard) {
        if (gameBoard == null) throw new IllegalArgumentException("Cannot initialize game with null gameBoard!");
        this.gameBoard = gameBoard;
        printIntro();
        this.gameBoard.startGame();
        do {
            show();
            handleInput();
        } while (this.gameBoard.getGameState() == GameState.PLAYING);

        handleEndOfGame();
    }

    private void show() {
        reinitializeStacks();

        System.out.println();

        printStockAndTalon(stock, talon);
        printFoundations(foundations);
        printTableau(tableau);
    }

    private void saveStatistics(int earnedScore, Date endOfGame) {
        System.out.print("Please enter your nickname: ");
        String nickname = scanner.nextLine();

        if (nickname.length() <= 1)
            nickname = System.getProperty("user.name");

        scoreService.addScore(new Score(GAME, nickname, earnedScore, endOfGame));

        System.out.print("Do you want leave some comment and rate this game ? (N/y): ");
        String line = scanner.nextLine().toUpperCase();

        if (line.equals("Y")) {
            String comment;
            do {
                System.out.println("Comment: ");
                comment = scanner.nextLine();
                if (comment.length() > 1024)
                    System.out.println("your comment is too long");
                else if (comment.length() == 0)
                    System.out.println("you have to write comment");
            } while (comment.length() > 1024 || comment.length() == 0);

            commentService.addComment(new Comment(GAME, nickname, comment, endOfGame));

            System.out.print("Leave rating from 1 to 5 :");
            int rating = scanner.nextInt();

            ratingService.setRating(new Rating(GAME, nickname, rating, endOfGame));
        }

        System.out.println("Thank You for your feedback !");

    }

    private void reinitializeStacks() {
        stock = gameBoard.getStock();
        talon = gameBoard.getTalon();
        for(int numberOfPile = 0; numberOfPile < tableau.length; numberOfPile++){
            tableau[numberOfPile] = gameBoard.getTableau(numberOfPile);
        }
        for(int numberOfPile = 0; numberOfPile < foundations.length; numberOfPile++){
            foundations[numberOfPile] = gameBoard.getFoundations(numberOfPile);
        }
    }

    private void handleInput() {
        if (COLORIZED_OUTPUT)
            System.out.print(BLUE + "\nEnter command (X - exit, ? - help): " + RESET);
        else
            System.out.print("\nEnter command (X - exit, ? - help): ");

        String line = scanner.nextLine().toUpperCase();

        if (!handleRegex(line) && !handleFileSavingLoading(line) && !handleDatabaseOperations(line)){
            switch (line) {
                case "X":
                    gameBoard.surrender();
                    break;

                case "?":
                    printHint();
                    handleInput();
                    break;

                case "D":
                    try {
                        gameBoard.deal();
                    } catch (IllegalStateException e1) {
                        System.out.println(e1.getMessage());
                        try{
                            gameBoard.reloadStock();
                        }catch (IllegalStateException e2){
                            System.out.println(e2.getMessage());
                        }
                    }
                    break;

                case "M":
                    handleMove();
                    break;

                case "S":
                    gameBoard.quickSave();
                    break;

                case "L":
                    try {
                        gameBoard.quickLoad();
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                default:
                    if (COLORIZED_OUTPUT)
                        System.out.println(RED + "I don't understand. Try again!" + RESET);
                    else
                        System.out.println("I don't understand. Try again!");

                    handleInput();
                    break;
            }
        }
    }

    private boolean handleRegex(String line) {
        Matcher matcher = COMMAND_PATTERN_ONE_CARD.matcher(line);
        if (matcher.matches()) {
            String source = matcher.group(2);
            String destination = matcher.group(4);
            int sourcePile = 0;
            if (line.length() == 9) {
                sourcePile = Integer.parseInt(matcher.group(3)) - 1;
            }
            int destinationPile = Integer.parseInt(matcher.group(5)) - 1;
            moveOneCard(source, destination, sourcePile, destinationPile);
            return true;
        }

        Matcher matcher2 = COMMAND_PATTERN_MORE_CARD.matcher(line);
        if (matcher2.matches()) {
            int numberOfCards = Integer.parseInt(matcher2.group(2));
            int sourcePile = Integer.parseInt(matcher2.group(3)) - 1;
            int destinationPile = Integer.parseInt(matcher2.group(4)) - 1;
            moveMoreCards(numberOfCards, sourcePile, destinationPile);
            return true;
        }

        return false;
    }

    private boolean handleFileSavingLoading(String line){
        if(line.equals("SAVE")){
            gameBoard.saveGame();
            return true;
        }else if(line.equals("LOAD")) {
            try{
                gameBoard = GameBoard.loadGame();
            }catch (GamestudioException e){
                System.out.println(e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean handleDatabaseOperations(String line){
        switch (line) {
            case "SCORE":
                printCurrentScore();
                return true;
            case "TOP":
                printTopPlayers();
                return true;
            case "COMMENTS":
                printLastComments();
                return true;
            case "RATING":
                printAverageRating();
                return true;
            default:
                return false;
        }
    }

    private void handleMove() {
        System.out.println("How many cards you want to move?");
        String line = scanner.nextLine();
        int numberOfCards = 0;

        try {
            numberOfCards = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println(line + " is not number");
        }

        if (numberOfCards == 1) {
            moveOneCard();
        } else if (numberOfCards > 1 && numberOfCards < 14) {
            moveMoreCards(numberOfCards);
        }
    }

    private void handleEndOfGame() {
        int earnedScore = gameBoard.getScore();
        Date endOfGame = new Date();

        if (gameBoard.getGameState() == GameState.SOLVED) {
            System.out.println("\nCongratulations You solved the Solitaire game. Your SCORE: " + earnedScore);
        } else {
            earnedScore = 0;
            System.out.println("\nLOOSER You Surrender the Solitaire game. Your SCORE: 0");
        }

        System.out.print("Do you want to save your statistics? (Y/n): ");
        String line = scanner.nextLine().toUpperCase();

        if (!line.equals("N")) {
            saveStatistics(earnedScore, endOfGame);
        }

        System.out.print("Want to play again? (N/y): ");
        String playAgain = (new Scanner(System.in)).nextLine().toUpperCase();
        if (playAgain.equals("Y")) {
            play(new GameBoard());
        } else System.exit(0);
    }

    private void moveMoreCards(int numberOfCards) {
        System.out.println("Write source tableau pile number and destination tableau pile number: \n" +
                "\t(1 2 - from pile 1 to pile 2)");

        String line = scanner.nextLine().toUpperCase();

        int sourcePile = line.charAt(0) - '1';
        int destinationPile = line.charAt(2) - '1';

        try {
            gameBoard.moveCardsInTableau(sourcePile, destinationPile, numberOfCards);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void moveMoreCards(int numberOfCards, int sourcePile, int destinationPile) {
        try {
            gameBoard.moveCardsInTableau(sourcePile, destinationPile, numberOfCards);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void moveOneCard() {
        System.out.println("Write type of stack: tn - talon, fn1 - foundation pile 1 " +
                "\n ta2 - tableau pile 2");
        System.out.println("Write some combination ( tn fn1, ta5 fn2, ... )");
        String line = scanner.nextLine().toUpperCase();

        try {
            if (line.startsWith("TN")) {
                int pile = line.charAt(5) - '1';
                if (line.contains("FN")) {
                    gameBoard.moveCard(StackType.TALON, 1, StackType.FOUNDATION, pile);
                } else if (line.contains("TA")) {
                    gameBoard.moveCard(StackType.TALON, 1, StackType.TABLEAU, pile);
                }
            } else if (line.startsWith("TA")) {
                int tableauPile = line.charAt(2) - '1';
                int destinationPile = line.charAt(6) - '1';
                if (line.contains("FN")) {
                    gameBoard.moveCard(StackType.TABLEAU, tableauPile, StackType.FOUNDATION, destinationPile);
                } else if (line.contains("TA")) {
                    gameBoard.moveCard(StackType.TABLEAU, tableauPile, StackType.TABLEAU, destinationPile);
                }
            } else if (line.startsWith("FN")) {
                int foundationPile = line.charAt(2) - '1';
                int destinationPile = line.charAt(6) - '1';
                if (line.contains("TA")) {
                    gameBoard.moveCard(StackType.FOUNDATION, foundationPile, StackType.TABLEAU, destinationPile);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void moveOneCard(String source, String destination, int sourcePile, int destinationPile) {
        try {
            if (source.startsWith("TN")) {
                if (destination.contains("FN")) {
                    gameBoard.moveCard(StackType.TALON, 1, StackType.FOUNDATION, destinationPile);
                } else if (destination.contains("TA")) {
                    gameBoard.moveCard(StackType.TALON, 1, StackType.TABLEAU, destinationPile);
                }
            } else if (source.startsWith("TA")) {
                if (destination.contains("FN")) {
                    gameBoard.moveCard(StackType.TABLEAU, sourcePile, StackType.FOUNDATION, destinationPile);
                } else if (destination.contains("TA")) {
                    gameBoard.moveCard(StackType.TABLEAU, sourcePile, StackType.TABLEAU, destinationPile);
                }
            } else if (source.startsWith("FN")) {
                if (destination.contains("TA")) {
                    gameBoard.moveCard(StackType.FOUNDATION, sourcePile, StackType.TABLEAU, destinationPile);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printIntro() {
        System.out.println("\n\nWelcome to Solitaire !");
        System.out.println("If you dont now how to play press '?'");
        System.out.println("If you want to play press any other key");
        System.out.print("\nDo you want to set game difficulty at high ? (N/y): ");


        String line = scanner.nextLine().toUpperCase();

        if (line.equals("Y")) {
            gameBoard.setHardMode(true);
        } else if (line.contains("?")) {
            printHint();
        }

        System.out.println("\nPRESS ENTER TO START GAME ... \n");
        scanner.nextLine();
    }

    private void printHint() {
        System.out.println("\n======= HOW TO PLAY =========");

        System.out.println("1. Press 'D' to deal card from stock to talon.");
        System.out.println("2. Press 'M' to move card, then type how many cards you want to move");

        System.out.println("\t- if you want to move 1 card, type source, and destination");
        System.out.println("\t\tTalon - tn");
        System.out.println("\t\tTableau - taN, ('N' represents number of pile, from 1 to 7)");
        System.out.println("\t\tFoundation - fnN, ('N' represents number of pile, from 1 to 4)");

        System.out.println("\t- if you want to move more cards, then type 'S D'");
        System.out.println("\t\tS represent number of source tableau pile");
        System.out.println("\t\tD represent number of destination tableau pile");

        System.out.println("\t- short commands :");
        System.out.println("\t\tm tn ta1 - move 1 card from talon to tableau 1. pile");
        System.out.println("\t\tm ta2 fn3 - move 1 card from tableau 2.pile to foundations 3. pile");
        System.out.println("\t\tm3 4 1 - move 3 cards from tableau 4.pile to tableau 1.pile");

        System.out.println("3. Press 'S' to quick save, 'L' to quick load");
        System.out.println("4. Type 'SAVE' to save game in computer, type 'LOAD' to load game from computer");
        System.out.println("5. Type 'SCORE' to find out how many points you currently have");

        System.out.println("6. To get informations:");
        System.out.println("\t- type 'TOP' to get top 10 players");
        System.out.println("\t- type 'COMMENTS' to get last 10 comments");
        System.out.println("\t- type 'RATING' to get average rating about game");

        System.out.println("7. Press 'X' to quit game");
        System.out.println();
    }

    private void printCurrentScore(){
        if(COLORIZED_OUTPUT)
            System.out.println(YELLOW + "Your current score: " + RED + gameBoard.getScore() + RESET);
        else
            System.out.println("Your current score: " + gameBoard.getScore());

        System.out.println();
    }

    private void printTopPlayers(){
        List<Score> scores = scoreService.getTopScores(GAME);
        if(COLORIZED_OUTPUT){
            System.out.println(YELLOW + "======== TOP 10 players =========");
            for (Score score: scores) {
                System.out.println(BLUE + "Player : " + RED + score.getPlayer() + BLUE + "  Score: " + RED + score.getPoints());
            }
        }else{
            System.out.println("======== TOP 10 players =========");
            for (Score score: scores) {
                System.out.println("Player : " + score.getPlayer() + "  Score: " + score.getPoints());
            }
        }
    }

    private void printLastComments(){
        List<Comment> comments = commentService.getComments(GAME);
        if(COLORIZED_OUTPUT){
            System.out.println(YELLOW + "======== Last 10 comments =========");
            for (Comment comment: comments) {
                System.out.println(BLUE + "Player : " + RED + comment.getPlayer() + BLUE + "\nComment:\n" + RESET + comment.getComment());
            }
        }else{
            System.out.println("======== Last 10 comments =========");
            for (Comment comment: comments) {
                System.out.println("Player : " + comment.getPlayer() + "\nComment:\n" + comment.getComment());
            }
        }
    }

    private void printAverageRating(){
        try{
            int averageRating = ratingService.getAverageRating(GAME);
            System.out.println("\n_____________________________");
            if(COLORIZED_OUTPUT) {
                System.out.print(BLUE + "Game rating: ");
                for (int star = 0; star < averageRating; star++) {
                    System.out.print( YELLOW + " ★" + RESET);
                }
            } else {
                System.out.print("Game rating: ");
                for (int star = 0; star < averageRating; star++) {
                    System.out.print(" ★");
                }
            }
            System.out.println("\n-----------------------------");
            System.out.println();
        } catch(RatingException e){
            System.out.println(e.getMessage());
        }

    }

    private void printTableau(CardStack[] tableau) {
        int tableauPileNum = 1;

        if (COLORIZED_OUTPUT)
            System.out.println(GREEN + "\n=== tableau ============" + RESET);
        else
            System.out.println("\n=== tableau ============");

        for (CardStack tableauPile : tableau) {
            if (COLORIZED_OUTPUT)
                System.out.println(YELLOW + "\nPile number: " + tableauPileNum + RESET);
            else
                System.out.println("\nPile number: " + tableauPileNum);

            while (!tableauPile.isEmpty()) {
                if (tableauPile.peek().isFacingUp() && tableauPile.peek().getSuit().getColor() == 1 && COLORIZED_OUTPUT) {
                    System.out.printf(RED + "|%s | " + RESET, tableauPile.pop().toString());
                } else System.out.printf("|%s | ", tableauPile.pop().toString());
            }
            System.out.println();
            tableauPileNum++;
        }
    }

    private void printFoundations(CardStack[] foundations) {

        if (COLORIZED_OUTPUT) {
            System.out.println(GREEN + "\n=== foundations ========" + RESET);
            System.out.println(BLUE + " 1       2       3       4    " + RESET);
        } else {
            System.out.println("\n=== foundations ========");
            System.out.println(" 1       2       3       4    ");
        }

        System.out.println(" _____   _____   _____   _____");
        for (CardStack foundationPile : foundations) {
            try {
                if (foundationPile.peek().getSuit().getColor() == 1 && COLORIZED_OUTPUT) {
                    System.out.printf("|" + RED + "%s" + RESET + " | ", foundationPile.peek().toString());
                } else System.out.printf("|%s | ", foundationPile.peek().toString());

            } catch (IllegalStateException e) {
                System.out.print("| X X | ");
            }
        }
        System.out.println("\n -----   -----   -----   -----");
    }

    private void printStockAndTalon(CardStack stock, CardStack talon) {

        if (COLORIZED_OUTPUT)
            System.out.println(GREEN + "=== stock  ==  talon ===" + RESET);
        else
            System.out.println("=== stock  ==  talon ===");

        System.out.println("    _____       _____");
        try {
            if (talon.peek().getSuit().getColor() == 1 && COLORIZED_OUTPUT) {
                System.out.printf("   | %d  |     |" + RED + "%s " + RESET + "|\n", stock.getSize(), talon.peek().toString());
            } else System.out.printf("   | %d  |     |%s |\n", stock.getSize(), talon.peek().toString());


        } catch (IllegalStateException e) {
            System.out.printf("   | %d  |     | X X |\n", stock.getSize());
        }
        System.out.println("    -----       -----");
    }

}
