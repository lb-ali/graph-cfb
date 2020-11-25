import java.util.*;

// import org.graalvm.compiler.graph.Node.EdgeVisitor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        ArrayList<Vertex> teams = new ArrayList<Vertex>();
        ArrayList<Edge> games = new ArrayList<Edge>();
        Dictionary<Integer, String> idLookup = new Hashtable<Integer, String>();

        // Read data from csv file using BufferedReader
        String csv = "/home/lbali/Documents/Java/cfb_project/cfb_project/src/data.csv";
        BufferedReader br = null;
        String splitBy = ",";
        try {
            br = new BufferedReader(new FileReader(csv));
            // br.readLine();
            while ((br.readLine()) != null) {
                String[] temp = br.readLine().split(splitBy);

                // Get ID's for both teams
                int id1 = Integer.parseInt(temp[11]);
                int id2 = Integer.parseInt(temp[20]);
                Vertex team1 = new Vertex(id1);
                Vertex team2 = new Vertex(id2);
                boolean addTeam1 = true;
                boolean addTeam2 = true;
                Edge tempEdge = null;
                // int i = 0;

                // Check if teams have already been added to vertices
                Enumeration<Integer> enumerator = idLookup.keys();
                for (int i = 0; i < teams.size(); i++) {
                    int tempID = teams.get(i).id;
                    if (tempID == id1) {
                        team1 = teams.get(i);
                        addTeam1 = false;
                    }
                    if (tempID == id2) {
                        team2 = teams.get(i);
                        addTeam2 = false;
                    }
                    // i++;
                }

                // Add an edge from the winner to the loser
                if (Integer.parseInt(temp[14]) > Integer.parseInt(temp[23])) {
                    tempEdge = new Edge(team1, team2);
                } else {
                    tempEdge = new Edge(team2, team1);
                }
                games.add(tempEdge);

                // Add teams to vertices if necessary
                if (addTeam1) {
                    teams.add(team1);
                    idLookup.put(team1.id, temp[12]);
                }
                if (addTeam2) {
                    teams.add(team2);
                    idLookup.put(team2.id, temp[21]);
                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        // System.out.println(count);

        // Initialize a graph which represents all games played in the season
        seasonGraph g = new seasonGraph(0, teams, games, idLookup);
        // Seed size is how large the initial cycle found will be
        int seedSize = 10;
        // Final size is how large the final cycle will be
        int finalSize = 55;
        int firstSize = 0;
        Random rand = new Random();
        int seed = rand.nextInt(teams.size());

        // Initialize an empty path before path finding
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        while (path.size() < finalSize) {
            // Find a new random vertex to start the cycle
            if (path.size() < finalSize) {
                do {
                    seed = rand.nextInt(teams.size());
                } while (teams.get(seed).neighbors.size() < 2);
            }
            // Use try and catch because circle sometimes throws a null pointer exception
            try {
                path = g.circle(teams.get(seed), seedSize + 10);
            } catch (Exception e) {
                path = new ArrayList<Vertex>();
            }

            if (path.size() > seedSize) {
                firstSize = path.size();
                // System.out.println("Finding subpaths, start path (" + firstSize + " units
                // long) in: ");
                // g.printPath(path);

                // Expand the cycle by finding unique paths within this cycle
                for (int i = 1; i < path.size() - 1; i++) {
                    firstSize = path.size();
                    // // Find a cycle starting from this point
                    Vertex temp1 = path.get(i);
                    Vertex temp2 = path.get(i + 1);
                    // Find the longest path between these two points
                    g.longestPath(temp1, temp2, path);
                    // Remove first and last elements of this path
                    if (g.getTempPath().size() > 2) {
                        g.getTempPath().remove(0);
                        g.getTempPath().remove(g.getTempPath().size() - 1);
                        // Check if any elements from this path are in the first cycle found
                        boolean add = true;
                        for (int j = 0; j < g.getTempPath().size(); j++) {
                            for (int k = 0; k < path.size(); k++) {
                                if (g.getTempPath().get(j).id == path.get(k).id) {
                                    add = false;
                                    break;
                                }
                            }
                        }
                        // If no elements are in original cycle, add to it
                        if (add) {
                            for (int r = 0; r < g.getTempPath().size(); r++) {
                                path.add(i + r, g.getTempPath().get(r));
                            }
                        }
                    }
                }
            }

        }

        g.printPath(path);
        System.out.println(path.size());

    }
}