package sk.tuke.kpi.kp.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private CardStack stock;
    private CardStack talon;
    private final CardStack[] foundations = new CardStack[4];
    private final CardStack[] tableau = new CardStack[7];

    @RequestMapping
    public String solitaire(@RequestParam(required = false) Boolean deal,
                            @RequestParam(required = false) Boolean move,
                            @RequestParam(required = false) String sourcePile,
                            @RequestParam(required = false) String destinationPile,
                            @RequestParam(required = false) Integer numberOfCards
    ) {
        if(!isGameStarted) {
            gameBoard.startGame();
            reinitializeStacks();
        }

        try {
            if (deal){
                if(gameBoard.getStock().isEmpty()) gameBoard.reloadStock();
                else gameBoard.deal();
            }

        } catch (Exception e) {
            // Tato vynimka znamena, ze neprisli parametre pre deal
        }

        try{
            if(move){
                if(numberOfCards > 1){
                    moveCardsInTableau(sourcePile, destinationPile, numberOfCards);
                }else{
                    moveOneCard(sourcePile, destinationPile);
                }
            }
        }catch (Exception e){
            //tato vynimka znamena, ze neprisli parametre pre move
        }

        
        reinitializeStacks();
        return "solitaire";
    }

    @RequestMapping("/new")
    public String newGame() {
        gameBoard = new GameBoard();
        isGameStarted = true;
        gameBoard.startGame();
        reinitializeStacks();
        return "solitaire";
    }


    public String getHtmlTableau(){
        StringBuilder sb = new StringBuilder();
        int pileNumber = 0;

        for (CardStack tableauPile : tableau) {
            int numberOfCards = 0;
            CardStack stackForHelp = new CardStack(StackType.HELP);
            sb.append("<div class='tableau").append(pileNumber).append(" column dropAble'>\n");

            while(!tableauPile.isEmpty()){
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
                    sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack2.png\">");
                }

                sb.append("</span>\n");
                numberOfCards--;
            }
            sb.append("</div>\n");
            pileNumber++;
        }
        return sb.toString();
    }

    public String getHtmlStockAndTalon(){
        StringBuilder sb = new StringBuilder();
        //stack
        sb.append("<div class=\"column\">\n");
        sb.append("<span class=\"card\">\n");
        sb.append("<a href=\"/solitaire?deal=true\">");
            try{
                stock.peek();
                sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack2.png\">");
            }catch (Exception e){
                sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
            }
        sb.append("</a>\n");
        sb.append("</span>\n");
        sb.append("</div>\n");

        //talon
        sb.append("<div class=\"column\">\n");

        try{
            Card stockTopCard = talon.peek();
            if (stockTopCard.isFacingUp()) {
                String rank = Integer.toString(stockTopCard.getRank().getValue());
                String suit = stockTopCard.getSuit().toString().toLowerCase();
                sb.append("<span draggable ='ture' class = 'talon movable card' >\n");
                sb.append("<img  src=\"").append("/images/PaperCards/").append(suit.substring(0, 1).toUpperCase()).append(suit.substring(1)).append("/").append(rank).append(suit).append(".png\">");
            } else {
                sb.append("<span class = 'card' >\n");
                sb.append("<img src=\"").append("/images/PaperCards/").append("CardBack2.png\">");
            }
        }catch (Exception e){
            sb.append("<span class = 'card' >\n");
            sb.append("<img src=\"").append("/images/PaperCards/").append("Blank Card.png\">");
        }
        sb.append("</span>\n");
        sb.append("</div>\n");



        return sb.toString();
    }

    public String getHtmlFoundations(){
        StringBuilder sb = new StringBuilder();
        int pileNumber = 0;

        sb.append("<div class=\"margin-left\">\n");
        for (CardStack foundationPile : foundations) {
            sb.append("<div class='foundation").append(pileNumber).append(" column dropAble'>\n");
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
        sb.append("</div>\n");
        return sb.toString();
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

    private void moveOneCard(String sourcePile, String destinationPile){
        StackType source = null, destination = null;
        int sourcePileNum = 0, destinationPileNum = 0;

        if(sourcePile.contains("talon")) source = StackType.TALON;
        else if (sourcePile.contains("tableau")){
            source = StackType.TABLEAU;
            for (int i = 0 ; i < 7; i++){
                if(sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        }
        else if (sourcePile.contains("foundation")){
            source = StackType.FOUNDATION;
            for (int i = 0 ; i < 4; i++){
                if(sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        }

        if (destinationPile.contains("tableau")){
            destination = StackType.TABLEAU;
            for (int i = 0 ; i < 7; i++){
                if(destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        }
        else if (destinationPile.contains("foundation")){
            destination = StackType.FOUNDATION;
            for (int i = 0 ; i < 4; i++){
                if(destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        }
        gameBoard.moveCard(source, sourcePileNum, destination, destinationPileNum);
    }

    private void moveCardsInTableau(String sourcePile, String destinationPile, int numberOfCards){
        int sourcePileNum = 0, destinationPileNum = 0;

        if (sourcePile.contains("tableau")){
            for (int i = 0 ; i < 7; i++){
                if(sourcePile.contains(Integer.toString(i))) sourcePileNum = i;
            }
        }

        if (destinationPile.contains("tableau")){
            for (int i = 0 ; i < 7; i++){
                if(destinationPile.contains(Integer.toString(i))) destinationPileNum = i;
            }
        }

        gameBoard.moveCardsInTableau(sourcePileNum, destinationPileNum, numberOfCards);
    }
}
