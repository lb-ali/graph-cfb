import java.lang.Runtime;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class changeTest {
    public static String printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        String text = "";
        while ((line = reader.readLine()) != null) {
            // System.out.println(line);
            text = text + line + "\n";
        }
        return text;
    }

    public static int countInstances(String str, String sub) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {

            lastIndex = str.indexOf(sub, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += sub.length();
            }
        }
        return count;
    }

    public static int[] analysis(String[] counts, String program) {
        int[] c = new int[4];
        for (int i = 0; i < 4; i++) {
            c[i] = countInstances(program, counts[i]);
        }
        return c;
    }

    public static int[] changeAnalysis(String[] counts, String old, String changed) {
        int[] cOld = analysis(counts, old);
        int[] cNew = analysis(counts, changed);
        int[] c = new int[4];
        for (int i = 0; i < 4; i++) {
            c[i] = cNew[i] - cOld[i];
        }
        return c;
    }

    public static void main(String[] args) throws IOException {
        // For file app.java, basic analysis
        // Process logs into an arraylist
        ArrayList<String> logs = new ArrayList<String>();
        File f = new File(".git/logs/HEAD");
        Scanner scnr = new Scanner(f);
        while (scnr.hasNextLine()) {
            String text = scnr.nextLine();
            logs.add(text);
            // System.out.println(text);
        }
        scnr.close();
        // Get 2nd to last log, which is most recent BEFORE current revision
        // Split at space, take first string to get id
        String id = logs.get(logs.size() - 1).split(" ")[0];

        String choice = "App.java";
        // System.out.println(id);
        String command = "git show " + id + ":src/" + choice;
        System.out.println(command);
        // Run command
        Process process = Runtime.getRuntime().exec(command);
        // Get results of command
        String oldProgram = printResults(process);
        // Get current program
        String currentProgram = "";
        File f2 = new File("src/" + choice);
        Scanner scnr2 = new Scanner(f2);
        while (scnr2.hasNextLine()) {
            String text2 = scnr2.nextLine();
            currentProgram = currentProgram + text2 + "\n";
            // System.out.println(text);
        }
        // int[] c = analysis(program);
        String[] counts = { "public", "for", "while", "if" };
        int[] c = changeAnalysis(counts, oldProgram, currentProgram);
        for (int i = 0; i < 4; i++) {
            System.out.println("Changes in occurrences of " + counts[i] + ": " + c[i]);
        }
    }
}
