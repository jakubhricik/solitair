package sk.tuke.kpi.kp.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.Card;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.CardStack;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.GameBoard;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;


@Controller
@RequestMapping("/solitaire")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SolitaireController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private GameBoard gameBoard = new GameBoard();
    private Boolean isGameStarted = false;
    private int cardBack = 1;

    private CardStack stock;
    private CardStack talon;
    private final CardStack[] foundations = new CardStack[4];
    private final CardStack[] tableau = new CardStack[7];
    private int cardsInTalon = 0;

    @RequestMapping
    public String solitaire() {
        if (!isGameStarted) {
            gameBoard.startGame();
            reinitializeStacks();
        }
        reinitializeStacks();
        return "solitaire";
    }

    @RequestMapping("/hardMode")
    @ResponseBody
    public String hardMode(){
        if(gameBoard.isHardMode()){
            gameBoard = new GameBoard();
            isGameStarted = true;
            gameBoard.startGame();
        }else{
            gameBoard = new GameBoard();
            isGameStarted = true;
            gameBoard.setHardMode(true);
            gameBoard.startGame();
        }
        reinitializeStacks();
        return getHtmlGameBoard();
    }

    @RequestMapping(value = "/cardTheme", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String changeCardBack() {
        if (cardBack <= 2) cardBack++;
        else if (cardBack == 3) cardBack = 1;
        return getHtmlGameBoard();
    }

    @RequestMapping(value = "/undo", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String undo() {
        gameBoard.quickLoad();
        return getHtmlGameBoard();
    }


    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String newGame() {
        gameBoard = new GameBoard();
        isGameStarted = true;
        gameBoard.startGame();
        return getHtmlGameBoard();
    }


    @RequestMapping(value = "/gameBoard", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getHtmlGameBoard() {
        reinitializeStacks();
        StringBuilder sb = new StringBuilder();

        sb.append("<div id='UpperSiteBoard'>\n");
            sb.append("<div class='row'>\n");
                sb.append("<div class='col-sm-4'>\n");
                    sb.append("<div class='row' id='StockTalonBoard'>\n");
                        sb.append(getHtmlStockAndTalon());
                    sb.append("</div>\n");
                sb.append("</div>\n");

                sb.append("<div class='col-sm-8'>\n");
                    sb.append("<div class='row' id='FoundationsBoard' >\n");
                        sb.append(getHtmlFoundations());
                    sb.append("</div>\n");
                sb.append("</div>\n");
            sb.append("</div>\n");
        sb.append("</div>\n");

        sb.append("<div id='tableauBoard'  class=\"row\">\n");
            sb.append(getHtmlTableau());
        sb.append("</div>\n");

        return sb.toString();
    }

    @RequestMapping(value = "/moveCards", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public void moveCards(
            @RequestParam(required = false) String sourcePile,
            @RequestParam(required = false) String destinationPile,
            @RequestParam(required = false) Integer numberOfCards
    ) {
        try {
            if (numberOfCards > 1) {
                moveCardsInTableau(sourcePile, destinationPile, numberOfCards);
            } else {
                moveOneCard(sourcePile, destinationPile);
            }

        } catch (Exception e) {
            //tato vynimka znamena, ze neprisli parametre pre move
            //vynimka je nepodstatna
        }
        reinitializeStacks();
    }

    @RequestMapping(value = "/foundation", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String foundationMove(@RequestParam Integer sourcePile) {
        StringBuilder sb = new StringBuilder();

        try {
            Card card = foundations[sourcePile].peek();
            sb.append("<span draggable ='ture' class = 'foundation").append(sourcePile).append(" movable card col' >\n");
            String rank = Integer.toString(card.getRank().getValue());
            String suit = card.getSuit().toString().toLowerCase();
            sb.append("<img src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");

        } catch (Exception e) {
            sb.append("<span class = 'card col' >\n");
            sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
        }
        sb.append("</span>\n");

        return sb.toString();
    }

    @RequestMapping(value = "/tableau", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String tableauMove(@RequestParam Integer sourcePile) {
        StringBuilder sb = new StringBuilder();

        int numberOfCards = 0;
        CardStack stackForHelp = new CardStack(StackType.HELP);

        while (!tableau[sourcePile].isEmpty()) {
            stackForHelp.push(tableau[sourcePile].pop());
            numberOfCards++;
        }

        while (!stackForHelp.isEmpty()) {
            Card card = stackForHelp.pop();

            if (card.isFacingUp()) {
                sb.append("<span draggable ='ture' class = 'tableau").append(sourcePile).append(" cardNum").append(numberOfCards).append(" movable card' >\n");
                String rank = Integer.toString(card.getRank().getValue());
                String suit = card.getSuit().toString().toLowerCase();
                sb.append("<img  src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");
            } else {
                sb.append("<span class = 'card' >\n");
                sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack").append(cardBack).append(".png\">");
            }

            sb.append("</span>\n");
            numberOfCards--;
        }

        return sb.toString();
    }


    @RequestMapping(value = "/deal", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dealCard() {
        gameBoard.quickSave();
        try {
            if (gameBoard.getStock().isEmpty()){
                gameBoard.reloadStock();
                cardsInTalon = 0;
            }
            else {
                if(gameBoard.isHardMode()){
                    cardsInTalon = Math.min(stock.getSize(), 3);
                }else cardsInTalon = 1;
                gameBoard.deal();
            }
        } catch (Exception e) {
            // Tato vynimka znamena, ze neprisli parametre pre deal
            //vynimka je nepodstatna
        }
        reinitializeStacks();
        return getHtmlStockAndTalon();
    }


    @RequestMapping(value = "/talonStock", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getHtmlStockAndTalon() {
        StringBuilder sb = new StringBuilder();
        //stack

        sb.append("<span class='card col'>\n");
        sb.append("<a onclick='dealCard()'>");
        try {
            stock.peek();
            sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack").append(cardBack).append(".png\">");
        } catch (Exception e) {
            sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
        }
        sb.append("</a>\n");
        sb.append("</span>\n");



        try {
            Card stockTopCard = talon.peek();
            if (stockTopCard.isFacingUp()) {
                String rank = Integer.toString(stockTopCard.getRank().getValue());
                String suit = stockTopCard.getSuit().toString().toLowerCase();
                sb.append("<span draggable ='ture' class = 'talon movable card col' >\n");
                sb.append("<img  src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");
            } else {
                sb.append("<span class = 'card col' >\n");
                sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack").append(cardBack).append(".png\">");
            }
        } catch (Exception e) {
            sb.append("<span class = 'card col' >\n");
            sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
        }
        sb.append("</span>\n");

        if(gameBoard.isHardMode()){
            if (talon.isEmpty()) cardsInTalon = 0;
            else if (talon.getSize() < 3) cardsInTalon = talon.getSize();
            sb.append("<div class = 'sideCards col'>\n");
            for (int i = 0; i < cardsInTalon -1; i++){
                sb.append("<span class = 'sideCard card' >\n");
                sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack").append(cardBack).append(".png\">");
                sb.append("</span>\n");
            }
            sb.append("</div>\n");
        }


        return sb.toString();
    }

    @RequestMapping(value = "/gameProgressPercentage",  produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String gameProgressPercentage(){
        reinitializeStacks();
        float totalCards = 0;
        for (CardStack pile : foundations){
            totalCards += pile.getSize();
        }
        float percentage = Math.round((totalCards / 52) * 100.0);
        return Float.toString(percentage);
    }

    @RequestMapping(value = "/isHardMode",  produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String isHardMode(){
        if(gameBoard.isHardMode()) return "true";
        else return "false";
    }

    private String getHtmlFoundations() {
        StringBuilder sb = new StringBuilder();
        int pileNumber = 0;

        for (CardStack foundationPile : foundations) {
            sb.append("<div id='foundation").append(pileNumber).append("' class='foundation").append(pileNumber).append(" col dropAble'>\n");
            try {
                Card card = foundationPile.peek();
                sb.append("<span draggable ='ture' class = 'foundation").append(pileNumber).append(" movable card' >\n");
                String rank = Integer.toString(card.getRank().getValue());
                String suit = card.getSuit().toString().toLowerCase();
                sb.append("<img src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");

            } catch (Exception e) {
                sb.append("<span class = 'card' >\n");
                sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
            }
            sb.append("</span>\n");
            sb.append("</div>\n");
            pileNumber++;
        }
        return sb.toString();
    }

    private void reinitializeStacks() {
        stock = gameBoard.getStock();
        talon = gameBoard.getTalon();
        for (int numberOfPile = 0; numberOfPile < tableau.length; numberOfPile++) {
            tableau[numberOfPile] = gameBoard.getTableau(numberOfPile);
        }
        for (int numberOfPile = 0; numberOfPile < foundations.length; numberOfPile++) {
            foundations[numberOfPile] = gameBoard.getFoundations(numberOfPile);
        }
    }

    private void moveOneCard(String sourcePile, String destinationPile) {
        gameBoard.quickSave();
        StackType source = null, destination = null;
        int sourcePileNum = 0, destinationPileNum = 0;

        int savedTalonCardsCount = talon.getSize();

        if (sourcePile.contains("talon")) {
            source = StackType.TALON;
        }
        else if (sourcePile.contains("tableau")) {
            source = StackType.TABLEAU;
            for (int i = 0; i < 7; i++) {
                if (sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        } else if (sourcePile.contains("foundation")) {
            source = StackType.FOUNDATION;
            for (int i = 0; i < 4; i++) {
                if (sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        }

        if (destinationPile.contains("tableau")) {
            destination = StackType.TABLEAU;
            for (int i = 0; i < 7; i++) {
                if (destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        } else if (destinationPile.contains("foundation")) {
            destination = StackType.FOUNDATION;
            for (int i = 0; i < 4; i++) {
                if (destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        }
        gameBoard.moveCard(source, sourcePileNum, destination, destinationPileNum);
        reinitializeStacks();
        if(talon.getSize() < savedTalonCardsCount) cardsInTalon--;
    }

    private void moveCardsInTableau(String sourcePile, String destinationPile, int numberOfCards) {
        gameBoard.quickSave();
        int sourcePileNum = 0, destinationPileNum = 0;

        if (sourcePile.contains("tableau")) {
            for (int i = 0; i < 7; i++) {
                if (sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        }

        if (destinationPile.contains("tableau")) {
            for (int i = 0; i < 7; i++) {
                if (destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        }

        gameBoard.moveCardsInTableau(sourcePileNum, destinationPileNum, numberOfCards);
    }

    private String getHtmlTableau() {
        StringBuilder sb = new StringBuilder();
        int pileNumber = 0;

        for (CardStack tableauPile : tableau) {
            int numberOfCards = 0;
            CardStack stackForHelp = new CardStack(StackType.HELP);
            sb.append("<div id='tableau").append(pileNumber).append("' class='tableau").append(pileNumber).append(" col dropAble'>\n");

            while (!tableauPile.isEmpty()) {
                stackForHelp.push(tableauPile.pop());
                numberOfCards++;
            }

            while (!stackForHelp.isEmpty()) {

                Card card = stackForHelp.pop();

                if (card.isFacingUp()) {
                    sb.append("<span draggable ='ture' class = 'tableau").append(pileNumber).append(" cardNum").append(numberOfCards).append(" movable card' >\n");
                    String rank = Integer.toString(card.getRank().getValue());
                    String suit = card.getSuit().toString().toLowerCase();
                    sb.append("<img  src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");
                } else {
                    sb.append("<span class = 'card' >\n");
                    sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack").append(cardBack).append(".png\">");
                }

                sb.append("</span>\n");
                numberOfCards--;
            }
            sb.append("</div>\n");
            pileNumber++;
        }
        return sb.toString();
    }
}
