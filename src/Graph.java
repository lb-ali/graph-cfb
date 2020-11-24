import java.util.*;

// Implements a directed, unweighted graph
public class Graph {
    public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    public ArrayList<Edge> edges = new ArrayList<Edge>();

    public Graph(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public void print() {
        for (int i = 0; i < this.vertices.size(); i++) {
            this.vertices.get(i).print();
        }

        for (int i = 0; i < this.edges.size(); i++) {
            this.edges.get(i).print();
        }

        // System.out.println(this.edges);
    }

    public boolean isNeighbor(Vertex k, Vertex l) {
        for (int i = 0; i < edges.size(); i++) {
            if (k.id == edges.get(i).head.id && l.id == edges.get(i).tail.id) {
                return true;
            }
        }
        return false;
    }

    public Vertex getID(int id) {
        for (int i = 0; i < this.vertices.size(); i++) {
            if (this.vertices.get(i).id == id) {
                return this.vertices.get(i);
            }
        }
        Vertex temp = new Vertex(0);
        return temp;
    }

    // Returns a Hamiltonian path assuming one exists
    public ArrayList<Vertex> hamCycle(Vertex seed) {
        // for (int i = 0; i < this.vertices.size(); i++) {
        // this.vertices.get(i).print();
        // }
        ArrayList<Vertex> path = new ArrayList<Vertex>(vertices.size());
        ArrayList<Vertex> avoid = new ArrayList<Vertex>();
        int last = seed.id;
        int first = seed.id;
        path.add(this.getID(last));
        int count = 0;
        while (path.size() < this.vertices.size() + 1) {
            // while (count < 30) {
            // while (i < 4) {
            // System.out.print(i);
            // System.out.println(this.vertices.size());
            Vertex k = this.getID(last);
            // System.out.println(last);
            int lastCount = count;
            // System.out.println("Start");
            // k.print();
            // int j = i;
            // while (j < this.vertices.size() - path.size() + 1) {
            for (int j = 0; j < this.vertices.size(); j++) {
                Vertex l = this.vertices.get(j);
                // System.out.print("Check: ");
                // k.print();
                // System.out.print("with ");
                // l.print();
                // System.out.print(this.isNeighbor(k, l));
                // System.out.println(" ");
                if (this.isNeighbor(k, l) && !avoid.contains(l)) {
                    // if (!path.contains(k.id)) {
                    // path.add(k.id);
                    // }
                    if (!path.contains(l)) {
                        path.add(l);
                        count++;
                        // System.out.println(k.id + " add " + l.id);
                        last = l.id;
                        break;
                    } else if (first == l.id && path.size() == this.vertices.size()) {
                        path.add(l);
                        count++;
                        break;
                    }
                }
                // j++;
            }
            // Backtracking
            // System.out.println(lastCount + ", " + count);
            // If nothing was added on the last pass
            if (count == lastCount) {
                // Remove last one or two vertice(s) added to path
                if (count > 1) {
                    path.remove(count);
                    count--;
                    avoid.add(path.get(count));
                    path.remove(count);
                    count--;
                    // Document which vertex to not go to next
                    last = path.get(count).id;
                }
                // break;
            } else {
                avoid.clear();
            }
            this.printPath(path);

            // last++;
            // if (last >= this.vertices.size() && path
        }
        // System.out.println(path);
        this.printPath(path);
        return path;
    }

    public void printPath(ArrayList<Vertex> path) {
        System.out.print("[");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i).id);
            if (i + 1 == path.size()) {
                System.out.println("]");
            } else {
                System.out.print(", ");
            }
        }
    }
}
