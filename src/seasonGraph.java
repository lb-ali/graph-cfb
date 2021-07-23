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

    public seasonGraph(ArrayList<Vertex> teams, ArrayList<Edge> edges, Dictionary<Integer, String> idLookup) {
        super(teams, edges);
        this.year = 0;
        this.idLookup = idLookup;
    }

    public seasonGraph(ArrayList<Vertex> teams, Dictionary<Integer, String> idLookup) {
        super(teams, new ArrayList<Edge>());
        this.year = 0;
        this.idLookup = idLookup;
    }

    // Finds some randomly chosen large cycle in the graph
    // Vertex seed is the starting point for this cycle
    // The arrayList returned has at least size elements
    public ArrayList<Vertex> circle(Vertex seed, int size) {
        // Initialize a path to be added to
        ArrayList<Vertex> path = new ArrayList<Vertex>(vertices.size());
        // Initialize an arrayList of vertices
        ArrayList<Vertex> avoid = new ArrayList<Vertex>();
        int last = seed.id;
        int first = seed.id;
        // Add the seed to the path
        path.add(this.getID(last));
        int count = 0;
        // Keep track of how many times this while loop runs
        // Terminate after 5000 runs
        int times = 0;
        while (path.size() < size && times < 5000) {
            // Iterate through graph to find path
            Vertex k = this.getID(last);
            // Keep track of the neighbors of k that have the most neighbors
            int[] neighborCount = new int[k.neighbors.size()];
            int lastCount = count;
            // Iterate through neighbors of this vertex
            if (k.neighbors.size() > 0) {
                Vertex l = k.neighbors.get(0);
                for (int j = 0; j < k.neighbors.size(); j++) {
                    l = k.neighbors.get(j);
                    // Keep track of how many neighbors each vertex has
                    neighborCount[j] = l.neighbors.size();
                    // If this vertex is on the backtracking list
                    if (avoid.contains(l) || (k.id == l.id)) {
                        neighborCount[j] = 0;
                    }
                }
                int largestIndex = 0;
                int largestNeighbor = neighborCount[0];
                ArrayList<Integer> seen = new ArrayList();
                Random rand = new Random();
                // Find the neighbor with the most neighbors
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
                // If vertices were added to the path, clear the avoid arrayList
                avoid.clear();
            }
            // this.printPath(path);
            times++;
        }

        // If the path is some arbitrary size
        if (path.size() > 10) {
            // Convert the path to a cycle
            ArrayList<Vertex> endPath = pathToCycle(path.get(0), path.get(path.size() - 1));
            if (!endPath.isEmpty()) {
                path.addAll(endPath);
            } else
                path = new ArrayList<Vertex>();
        } else
            path = new ArrayList<Vertex>();
        return path;
    }

    // Finds a path from b to a, return empty ArrayList if it doesnt exist
    // Use in conjunction with circle to make a cycle from a path
    public ArrayList<Vertex> pathToCycle(Vertex a, Vertex b) {
        // Use BFS from b to a
        // Keep track of which vertices have been visited
        boolean visited[] = new boolean[this.vertices.size()];
        // Keep a queue of vertices to visit
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        // Keep track of parents of each vertex
        Dictionary<Vertex, Vertex> parents = new Hashtable<Vertex, Vertex>();
        // Mark b as visited, add to front of queue
        visited[this.findIndex(b)] = true;
        queue.add(b);

        while (queue.size() != 0) {
            // Take element off front of queue
            Vertex s = queue.poll();
            // Traverse through this element's neighbors
            for (int i = 0; i < s.neighbors.size(); i++) {
                // If this element is a, unpack list through parents
                if (s.neighbors.get(i).id == a.id) {
                    Vertex p = parents.get(s);
                    ArrayList<Vertex> endPath = new ArrayList<Vertex>();
                    endPath.add(s.neighbors.get(i));
                    endPath.add(s);
                    while (p.id != b.id) {
                        endPath.add(p);
                        p = parents.get(p);
                    }
                    // Reverse endPath and return
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
        // If a path from b to a could not be found, return an empty ArrayList
        return new ArrayList<Vertex>();

    }

    // Takes two vertices and finds a long path between them
    // avoid is a list of vertices to not include in the path
    // This long path is stored in this.tempPath
    public void longestPath(Vertex a, Vertex b, ArrayList<Vertex> avoid) {
        boolean visited[] = new boolean[this.vertices.size()];
        // Mark all vertices in avoid as visited
        for (int j = 0; j < avoid.size(); j++) {
            if (!(avoid.get(j).id == a.id || avoid.get(j).id == b.id))
                visited[this.findIndex(avoid.get(j))] = true;
        }
        // Initialize a path and add a to the front of it
        ArrayList<Vertex> pathList = new ArrayList<Vertex>();
        pathList.add(a);
        boolean done = false;

        // clear tempPath
        this.tempPath = new ArrayList<Vertex>();
        this.tempPathSize = 0;

        this.longestPathUtil(a, b, visited, pathList, done);
    }

    public void longestPathUtil(Vertex a, Vertex b, boolean[] visited, ArrayList<Vertex> pathList, boolean done) {
        // If b has already been connected to a, don't run
        if (done)
            return;

        // If a == b
        if (a.id == b.id) {
            // If this is the longest path found
            if (pathList.size() > this.tempPathSize) {
                // Set this.tempPath to pathList
                this.tempPath = new ArrayList<>(pathList);
                done = true;
            }
            return;
        }
        // Mark a as visited
        visited[this.findIndex(a)] = true;

        // Iterate through all neighbors of a
        for (int i = 0; i < a.neighbors.size(); i++) {
            // If this neighbor has not been visited
            if (!visited[this.findIndex(a.neighbors.get(i))]) {
                // Add to list, run recursively
                pathList.add(a.neighbors.get(i));
                longestPathUtil(a.neighbors.get(i), b, visited, pathList, done);
                pathList.remove(a.neighbors.get(i));
            }
        }

        // visited[this.findIndex(a)] = false;
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
                System.out.print(" --> ");
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
