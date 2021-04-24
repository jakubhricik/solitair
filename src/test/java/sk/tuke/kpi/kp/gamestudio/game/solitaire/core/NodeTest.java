package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    private Node newNode(){
        return new Node();
    }

    @Test
    public void createNode(){
        Node node = newNode();

        assertNull(node.getCard());
        assertNull(node.getNext());
    }

    @Test
    public void checkNext(){
        Node node1 = newNode();
        Node node2 = newNode();

        node1.setNext(node2);
        node2.setNext(node1);

        assertEquals(node2, node1.getNext());
        assertEquals(node1, node2.getNext());
    }

    @Test
    public void checkCard(){
        Node node = newNode();
        Card card = new Card(Rank.ACE, Suit.CLUBS);

        node.setCard(card);
        assertEquals(card, node.getCard());
    }
}
