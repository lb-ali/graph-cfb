import java.util.*;

public class seasonGraph extends Graph {

    public int year;
    public ArrayList<Vertex> tempPath = new ArrayList();
    public int tempPathSize = 0;
    public Dictionary<Integer, String> idLookup = new Hashtable<Integer, String>();

    public seasonGraph(int year, ArrayList<Vertex> teams, ArrayList<Edge> edges, Dictionary<Integer, String> idLookup) {
        super(teams, edges);
        this.year = year;
        this.idLookup = idLookup;
    }

    // Finds some randomly chosen large cycle in the graph
    public ArrayList<Vertex> circle(Vertex seed, int size) {
        ArrayList<Vertex> path = new ArrayList<Vertex>(vertices.size());
        ArrayList<Vertex> avoid = new ArrayList<Vertex>();
        int last = seed.id;
        int first = seed.id;
        path.add(this.getID(last));
        int count = 0;
        int times = 0;
        while (path.size() < size && times < 5000) {
            // Iterate through graph to find path
            // while (times < 500) {
            Vertex k = this.getID(last);
            // this.vertexPrint(k);
            int lastCount = count;
            // Iterate through neighbors of this vertex
            // Keep track of the neighboring vertices with the most neighbors
            int[] neighborCount = new int[k.neighbors.size()];
            if (k.neighbors.size() > 0) {
                Vertex l = k.neighbors.get(0);
                for (int j = 0; j < k.neighbors.size(); j++) {
                    l = k.neighbors.get(j);
                    neighborCount[j] = l.neighbors.size();

                    // System.out.print(j + "th Check: ");
                    // this.vertexPrint(k);
                    // System.out.print("with ");
                    // this.vertexPrint(l);
                    // System.out.print(this.isNeighbor(k, l));
                    // System.out.println(" ");

                    // If this vertex is not on the backtracking list
                    if (avoid.contains(l) || (k.id == l.id)) {
                        neighborCount[j] = 0;
                    }
                    // j++;
                }
                int largestIndex = 0;
                int largestNeighbor = neighborCount[0];
                ArrayList<Integer> seen = new ArrayList();
                Random rand = new Random();
                // Use some randomness to pick next
                for (int n = 0; n < ((int) neighborCount.length / 2); n++) {
                    int m = rand.nextInt(neighborCount.length - 1);
                    while (seen.contains(m)) {
                        m = rand.nextInt(neighborCount.length - 1);
                    }
                    // m++;
                    seen.add(m);
                    if (neighborCount[m] > largestNeighbor && !path.contains(k.neighbors.get(m))) {
                        largestIndex = m;
                        largestNeighbor = neighborCount[m];
                    }
                }
                l = k.neighbors.get(largestIndex);
                if (!path.contains(l) && !avoid.contains(l) && !(k.id == l.id)) {
                    path.add(l);
                    count++;
                    last = l.id;
                }
            }

            // Backtracking
            // System.out.println(lastCount + ", " + count);
            // If nothing was added on the last pass
            if (count == lastCount) {
                if (count > 1) {
                    // Remove last one or two vertice(s) added to path
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
            // this.printPath(path);
            times++;
        }

        if (path.size() > 10) {
            ArrayList<Vertex> endPath = pathToCycle(path.get(0), path.get(path.size() - 1));
            if (!endPath.isEmpty()) {
                // System.out.println("\n\n");
                // this.printPath(path);
                // this.printPath(endPath);
                path.addAll(endPath);
            } else
                path = new ArrayList<Vertex>();
        } else
            path = new ArrayList<Vertex>();
        // System.out.println(end);
        return path;
    }

    // Finds a path from b to a, return empty ArrayList if it doesnt exist
    // Use in conjunction with circle to make a cycle from a path
    public ArrayList<Vertex> pathToCycle(Vertex a, Vertex b) {
        // Use BFS from b to a
        boolean visited[] = new boolean[this.vertices.size()];
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        Dictionary<Vertex, Vertex> parents = new Hashtable<Vertex, Vertex>();
        visited[this.findIndex(b)] = true;
        queue.add(b);

        while (queue.size() != 0) {
            // Take element off queue
            Vertex s = queue.poll();
            // Traverse through this element's neighbors
            for (int i = 0; i < s.neighbors.size(); i++) {
                // If this element is a, unpack list through parents
                if (s.neighbors.get(i).id == a.id) {
                    // this.vertexPrint(a);
                    Vertex p = parents.get(s);
                    ArrayList<Vertex> endPath = new ArrayList<Vertex>();
                    endPath.add(s.neighbors.get(i));
                    endPath.add(s);
                    while (p.id != b.id) {
                        // this.vertexPrint(p);
                        endPath.add(p);
                        p = parents.get(p);
                        // this.vertexPrint(p);
                    }
                    // System.out.println(parents);
                    // endPath.add(b);
                    Collections.reverse(endPath);
                    // this.printPath(endPath);
                    return endPath;
                }

                // If this element has not had its neighbors visited, add to queue
                if (!visited[this.findIndex(s.neighbors.get(i))]) {
                    parents.put(s.neighbors.get(i), s);
                    visited[this.findIndex(s.neighbors.get(i))] = true;
                    queue.add(s.neighbors.get(i));
                }
            }
        }
        return new ArrayList<Vertex>();

    }

    public void longestPath(Vertex a, Vertex b, ArrayList<Vertex> avoid) {
        boolean visited[] = new boolean[this.vertices.size()];
        for (int j = 0; j < avoid.size(); j++) {
            if (!(avoid.get(j).id == a.id || avoid.get(j).id == b.id))
                visited[this.findIndex(avoid.get(j))] = true;
        }
        ArrayList<Vertex> pathList = new ArrayList<Vertex>();
        pathList.add(a);
        boolean done = false;

        this.tempPath = new ArrayList<Vertex>();
        this.tempPathSize = 0;

        this.longestPathUtil(a, b, visited, pathList, done);
        // System.out.print("After: ");
        // this.printPath(this.tempPath);

    }

    public void longestPathUtil(Vertex a, Vertex b, boolean[] visited, ArrayList<Vertex> pathList, boolean done) {
        if (done)
            return;
        if (a.id == b.id) {
            if (pathList.size() > this.tempPathSize) {
                // System.out.println("Same below: ");
                this.tempPath = new ArrayList<>(pathList);
                done = true;
                // this.printPath(this.tempPath);
                // this.printPath(pathList);
            }
            return;
        }
        // this.vertexPrint(a);
        // this.vertexPrint(b);
        visited[this.findIndex(a)] = true;

        for (

                int i = 0; i < a.neighbors.size(); i++) {
            if (!visited[this.findIndex(a.neighbors.get(i))]) {
                pathList.add(a.neighbors.get(i));
                longestPathUtil(a.neighbors.get(i), b, visited, pathList, done);

                pathList.remove(a.neighbors.get(i));
            }
        }

        visited[this.findIndex(a)] = false;
    }

    public ArrayList<Vertex> getTempPath() {
        return this.tempPath;
    }

    public void printPath(ArrayList<Vertex> path) {
        System.out.print("[");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(idLookup.get(path.get(i).id));
            if (i + 1 == path.size()) {
                System.out.println("]");
            } else {
                System.out.print(", ");
            }
        }
    }

    public void vertexPrint(Vertex v) {
        System.out.println(idLookup.get(v.id));
    }

    public int findIndex(Vertex v) {
        for (int i = 0; i < this.vertices.size(); i++) {
            if (this.vertices.get(i).id == v.id) {
                return i;
            }
        }
        return -1;
    }

}
