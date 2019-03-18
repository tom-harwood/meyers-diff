package diff;
import java.io.*;
import java.util.*;

class Main
{
    public static boolean verbose = false;
    public static boolean quiet = false;
    public static boolean patch = false;

    public static void main(String[] args)
    throws Exception
    {
        int idx = 0;

        String filename1 = null;
        String filename2 = null;

        for (String arg: args) {
            if (arg.equals("-v")) {
                verbose = true;
            } else if (arg.equals("-q")) {
                quiet = true;
            } else if (arg.equals("-patch")) {
                patch = true;
            } else if (filename1 == null) {
                filename1 = arg;
            } else if (filename2 == null) {
                filename2 = arg;
            } else {
                throw new IllegalArgumentException("Unknown arg " + arg);
            }
        }

        if (filename1 == null || filename2 == null) {
            throw new IllegalArgumentException("You must specify two filenames.");
        }

        List<String> l1 = read(filename1);
        List<String> l2 = read(filename2);

        List<Edit<String>> edits = new MyersDiff<String>(l1, l2).editScript();

        if (!quiet) {
            if (patch) {
                System.out.printf("--- %s\n", filename1);
                System.out.printf("+++ %s\n", filename2);
            }

            for (Edit<String> e: edits) {
                System.out.print(e);
            }
        }

        System.exit(edits.size());
    }

    private static List<String> read(String filename)
    throws Exception
    {
        List<String> result = new ArrayList<String>();
        BufferedReader r = new BufferedReader(new FileReader(filename));

        String s = r.readLine();

        while (s != null) {
            result.add(s);
            s = r.readLine();
        }

        return result;
    }

    public static void vprintf(String format, Object ... args)
    {
        if (verbose) {
            System.out.printf(format, args);
        }
    }
}
