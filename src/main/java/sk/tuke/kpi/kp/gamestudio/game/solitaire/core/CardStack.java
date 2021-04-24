package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;

import java.io.Serializable;

public class CardStack implements Serializable {
    private Node topCard;
    private int size;
    private final StackType stackType;


    public CardStack(StackType stackType) {
        size = 0;
        topCard = new Node();
        this.stackType = stackType;
    }


    public void push(Card card) {
        if (card == null) throw new IllegalArgumentException("Illegal Card added to stack");

        if (isEmpty()) {
            if (topCard == null) {
                topCard = new Node();
            }
            topCard.setCard(card);
        } else {
            Node newTopCard = new Node();
            newTopCard.setCard(card);
            newTopCard.setNext(topCard);
            this.topCard = newTopCard;
        }
        size++;
    }

    public Card peek() {
        if (!isEmpty()) {
            return topCard.getCard();
        } else throw new IllegalStateException("Cannot peek, card stock is empty");
    }

    public Card pop() {
        if (!isEmpty()) {
            Card card = topCard.getCard();
            topCard = topCard.getNext();
            size--;
            return card;
        } else throw new IllegalStateException("Cannot pop, card stock is empty");
    }

    @Override
    protected CardStack clone() {

        if (this.isEmpty()) return new CardStack(stackType);

        CardStack reversedHelpStock = new CardStack(stackType);
        CardStack clone = new CardStack(stackType);
        Card card;
        boolean cardFacing;

        while (!isEmpty()) {
            reversedHelpStock.push(this.pop());
        }

        while (!reversedHelpStock.isEmpty()) {
            card = reversedHelpStock.pop();
            cardFacing = card.isFacingUp();
            card.setFacingUp(true);
            clone.push(new Card(card.getRank(), card.getSuit()));
            this.push(card);

            clone.peek().setFacingUp(cardFacing);
            this.peek().setFacingUp(cardFacing);
        }
        return clone;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public StackType getStackType() {
        return stackType;
    }

}
