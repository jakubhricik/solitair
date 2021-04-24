package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import java.io.Serializable;

public class Node implements Serializable {
    private Card card;
    private Node next;

    public Node() {
        next = null;
        card = null;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
