import java.util.*;

public class Edge {
    public Vertex head;
    public Vertex tail;

    public Edge(Vertex head, Vertex tail) {
        this.head = head;
        this.tail = tail;
        this.head.neighbors.add(tail);
    }

    public void print() {
        System.out.println(head.id + ", " + tail.id);
    }
}
