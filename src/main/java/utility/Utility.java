package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Utility {

    public static String extractLanguage(String run_type) throws Exception {
        int start = run_type.indexOf('_');
        int end = run_type.length();

        if (run_type.substring(start + 1, end).length() < 2) {
            throw new Exception("Language not found");
        }

        return run_type.substring(start + 1, end);
    }

    public static int extractRunNo(String runType) {
        int start = runType.indexOf('-');
        int end = runType.length() - 3;
        String s = runType.substring(start + 1, end);

        return Integer.parseInt(s);
    }

    public static void writeToFile(Map map, String output_Filename) {
        final int TOP_RESULTS = 1000;
        String stringToBeStored = "";
        int counter = 0;
        OutputStream writer = null;

        Set<Double> keySet = map.keySet();

        System.out.print("Writing to file...");

        try {
            writer = new FileOutputStream(new File("results/" + output_Filename));

            for (Double key : keySet) {

                // Limit to top 1000 results
                if (counter >= TOP_RESULTS) {
                    return;
                }
                ArrayList<String> collection_Value = (ArrayList<String>) map.get(key);
                stringToBeStored = "";
                stringToBeStored += collection_Value.get(0) + " 0 " + collection_Value.get(1) + " " + counter + " " + key + " baseline" + "\r\n";
                writer.write(stringToBeStored.getBytes(), 0, stringToBeStored.length());
//                System.out.println(stringToBeStored);
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done...");
    }

    public static void iterateCollection(Map map) {
        String stringToStore = "";
        Set<Double> keySet = map.keySet();

        for (Double key : keySet) {
            ArrayList<String> collection_Value = (ArrayList<String>) map.get(key);
            stringToStore = "";
            stringToStore += collection_Value.get(0) + "   " + collection_Value.get(1) + "   " + key;
            System.out.println(stringToStore);
        }
    }
}
