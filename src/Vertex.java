import java.util.*;

public class Vertex {
    public int id;
    public ArrayList<Vertex> neighbors = new ArrayList<Vertex>();

    public Vertex(int id) {
        this.id = id;
    }

    public void print() {
        System.out.println(this.id);
    }
}
