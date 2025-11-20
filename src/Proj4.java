import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Proj4 {

    public static void main(String[] args) {

        // Check command-line arguments
        if (args.length != 2) {
            System.err.println("Usage: java Proj4 <input file> <number of lines>");
            System.exit(1);
        }

        String inputFileName = "src/" + args[0];
        int numLinesRequested = Integer.parseInt(args[1]);

        ArrayList<String> data = new ArrayList<>();

        // ====== Read data from CSV ======
        try (Scanner inputFileScanner = new Scanner(new FileInputStream(inputFileName))) {

            // Skip header line if present
            if (inputFileScanner.hasNextLine()) {
                inputFileScanner.nextLine();
            }

            int count = 0;
            while (inputFileScanner.hasNextLine() && count < numLinesRequested) {
                String line = inputFileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Split CSV line on comma
                String[] parts = line.split(",");
                if (parts.length == 0) {
                    continue;
                }

                // Use the first column (car name) as the key
                String name = parts[0].trim();
                if (!name.isEmpty()) {
                    data.add(name);
                    count++;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }

        if (data.isEmpty()) {
            System.out.println("No data read from file.");
            return;
        }

        int N = data.size();

        // ====== Build sorted, shuffled, and reversed lists ======
        ArrayList<String> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        ArrayList<String> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled);

        ArrayList<String> reversed = new ArrayList<>(data);
        Collections.sort(reversed, Collections.reverseOrder());

        // Variables to hold timing results (seconds)
        double insertSorted, searchSorted, deleteSorted;
        double insertShuffled, searchShuffled, deleteShuffled;
        double insertReversed, searchReversed, deleteReversed;

        // ====== Timings for already-sorted list ======
        {
            SeparateChainingHashTable<String> table = new SeparateChainingHashTable<>();
            long start, end;

            // Insert
            start = System.nanoTime();
            for (String key : sorted) {
                table.insert(key);
            }
            end = System.nanoTime();
            insertSorted = (end - start) / 1_000_000_000.0;

            // Search
            start = System.nanoTime();
            for (String key : sorted) {
                table.contains(key);
            }
            end = System.nanoTime();
            searchSorted = (end - start) / 1_000_000_000.0;

            // Delete
            start = System.nanoTime();
            for (String key : sorted) {
                table.remove(key);
            }
            end = System.nanoTime();
            deleteSorted = (end - start) / 1_000_000_000.0;
        }

        // ====== Timings for shuffled list ======
        {
            SeparateChainingHashTable<String> table = new SeparateChainingHashTable<>();
            long start, end;

            // Insert
            start = System.nanoTime();
            for (String key : shuffled) {
                table.insert(key);
            }
            end = System.nanoTime();
            insertShuffled = (end - start) / 1_000_000_000.0;

            // Search
            start = System.nanoTime();
            for (String key : shuffled) {
                table.contains(key);
            }
            end = System.nanoTime();
            searchShuffled = (end - start) / 1_000_000_000.0;

            // Delete
            start = System.nanoTime();
            for (String key : shuffled) {
                table.remove(key);
            }
            end = System.nanoTime();
            deleteShuffled = (end - start) / 1_000_000_000.0;
        }

        // ====== Timings for reversed list ======
        {
            SeparateChainingHashTable<String> table = new SeparateChainingHashTable<>();
            long start, end;

            // Insert
            start = System.nanoTime();
            for (String key : reversed) {
                table.insert(key);
            }
            end = System.nanoTime();
            insertReversed = (end - start) / 1_000_000_000.0;

            // Search
            start = System.nanoTime();
            for (String key : reversed) {
                table.contains(key);
            }
            end = System.nanoTime();
            searchReversed = (end - start) / 1_000_000_000.0;

            // Delete
            start = System.nanoTime();
            for (String key : reversed) {
                table.remove(key);
            }
            end = System.nanoTime();
            deleteReversed = (end - start) / 1_000_000_000.0;
        }

        // ====== Print human-readable results ======
        System.out.println("Number of lines evaluated: " + N);
        System.out.println();

        System.out.println("Already sorted list:");
        System.out.printf("  Insert time (s): %.9f%n", insertSorted);
        System.out.printf("  Search time (s): %.9f%n", searchSorted);
        System.out.printf("  Delete time (s): %.9f%n", deleteSorted);
        System.out.println();

        System.out.println("Shuffled list:");
        System.out.printf("  Insert time (s): %.9f%n", insertShuffled);
        System.out.printf("  Search time (s): %.9f%n", searchShuffled);
        System.out.printf("  Delete time (s): %.9f%n", deleteShuffled);
        System.out.println();

        System.out.println("Reversed list:");
        System.out.printf("  Insert time (s): %.9f%n", insertReversed);
        System.out.printf("  Search time (s): %.9f%n", searchReversed);
        System.out.printf("  Delete time (s): %.9f%n", deleteReversed);
        System.out.println();

        // ====== Append CSV results to analysis.txt ======
        long timestamp = System.currentTimeMillis();

        try (PrintWriter out = new PrintWriter(new FileOutputStream("analysis.txt", true))) {
            // Format: timestamp,order,N,insert,search,delete
            out.printf("%d,sorted,%d,%.9f,%.9f,%.9f%n",
                    timestamp, N, insertSorted, searchSorted, deleteSorted);
            out.printf("%d,shuffled,%d,%.9f,%.9f,%.9f%n",
                    timestamp, N, insertShuffled, searchShuffled, deleteShuffled);
            out.printf("%d,reversed,%d,%.9f,%.9f,%.9f%n",
                    timestamp, N, insertReversed, searchReversed, deleteReversed);
        } catch (IOException e) {
            System.err.println("Error writing analysis.txt: " + e.getMessage());
        }
    }
}
