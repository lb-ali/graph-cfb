import java.util.*;

// import org.graalvm.compiler.graph.Node.EdgeVisitor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        // Graph g = new Graph

        // for (int i = 0; i < 5; i++) {
        // Vertex temp = new Vertex(i);
        // teams.add(temp);
        // }
        // for (int i = 0; i < 4; i++) {
        // Vertex temp1 = new Vertex(i);
        // Vertex temp2 = new Vertex(i + 1);
        // Edge temp = new Edge(temp1, temp2);
        // games.add(temp);
        // }
        // Create a 5-team tournament graph
        // Vertex one = new Vertex(1);
        // Vertex two = new Vertex(2);
        // Vertex three = new Vertex(3);
        // Vertex four = new Vertex(4);
        // Vertex five = new Vertex(5);
        // teams.add(one);
        // teams.add(two);
        // teams.add(three);
        // teams.add(four);
        // teams.add(five);
        // games.add(new Edge(two, one));
        // games.add(new Edge(three, one));
        // games.add(new Edge(four, one));
        // games.add(new Edge(one, five));

        // games.add(new Edge(two, three));
        // games.add(new Edge(two, five));
        // games.add(new Edge(four, two));

        // games.add(new Edge(three, four));
        // games.add(new Edge(five, three));

        // games.add(new Edge(four, five));

        // Graph g = new seasonGraph(0, teams, games);
        // // g.print();
        // // System.out.println(g.isNeighbor(two, one));
        // // System.out.println(g.isNeighbor(one, two));
        // g.hamCycle(three);
        ArrayList<Vertex> teams = new ArrayList<Vertex>();
        ArrayList<Edge> games = new ArrayList<Edge>();
        Dictionary<Integer, String> idLookup = new Hashtable<Integer, String>();

        String csv = "/home/lbali/Documents/Java/cfb_project/cfb_project/src/2019.csv";
        BufferedReader br = null;
        String line = "";
        String splitBy = ",";
        int count = 0;
        try {
            br = new BufferedReader(new FileReader(csv));
            br.readLine();
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
                // while (enumerator.hasMoreElements()) {
                for (int i = 0; i < teams.size(); i++) {
                    // int tempID = enumerator.nextElement();
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
                // if (teams.size() > 41 && (team1.id == teams.get(39).id || team2.id ==
                // teams.get(39).id)) {
                // tempEdge.print();
                // System.out.println(team1 + "; " + team2);
                // }
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
                count++;
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

        seasonGraph g = new seasonGraph(0, teams, games, idLookup);
        int size = 13;
        int firstSize = 0;
        int expanded = 0;
        Random rand = new Random();
        int seed = rand.nextInt(teams.size());

        ArrayList<Vertex> path = new ArrayList<Vertex>();
        while (expanded - firstSize < 1) {
            if (expanded - firstSize < 1) {
                do {
                    seed = rand.nextInt(teams.size());
                } while (teams.get(seed).neighbors.size() < 2);
            }
            path = g.circle(teams.get(seed), size);
            if (path.size() > size) {
                System.out.println("Finding subpaths, start path (" + path.size() + " units long) is: ");
                g.printPath(path);
                firstSize = path.size();
                // // Expand by finding unique cycles within this cycle
                for (int i = 1; i < path.size() - 1; i++) {
                    // // Find a cycle starting from this point
                    Vertex temp1 = path.get(i);
                    Vertex temp2 = path.get(i + 1);
                    g.vertexPrint(temp1);
                    g.vertexPrint(temp2);
                    System.out.print("Before: ");
                    g.printPath(g.tempPath);
                    g.longestPath(temp1, temp2);
                    // g.printPath(g.tempPath);
                    // ArrayList<Vertex> tempList = g.tempPath;
                    // g.printPath(g.tempPath);
                    System.out.println(g.tempPath.size());
                    // Remove first and last elements
                    if (g.tempPath.size() > 2) {
                        // g.printPath(g.tempPath);
                        g.tempPath.remove(0);
                        g.tempPath.remove(g.tempPath.size() - 1);
                        // g.printPath(g.tempPath);
                        // // // If no elements from this cycle are in original cycle, add to it
                        boolean add = true;
                        for (int j = 0; j < g.tempPath.size(); j++) {
                            for (int k = 0; k < path.size(); k++) {
                                if (g.tempPath.get(j).id == path.get(k).id)
                                    add = false;
                            }
                        }

                        if (add)
                            path.addAll(g.tempPath);
                        System.out.print("Expanded to " + path.size() + " units: ");
                        g.printPath(path);
                    }
                }
                expanded = path.size();
            }
        }

        // boolean t = false;
        // while (!t) {
        // seed = rand.nextInt(teams.size());
        // t = g.circle(teams.get(seed), size);
        // }
        g.printPath(path);
        System.out.println(path.size());

        // int sum = 0;
        // for (int i = 0; i < teams.size(); i++) {
        // sum = sum + teams.get(i).neighbors.size();
        // }
        // System.out.println(sum + "/" + teams.size());
        // System.out.println(sum / teams.size());
        // int t = 99;
        // g.vertexPrint(teams.get(t));
        // for (int i = 0; i < teams.get(t).neighbors.size(); i++) {
        // g.vertexPrint(teams.get(t).neighbors.get(i));
        // System.out.print(", ");
        // }

    }
}
