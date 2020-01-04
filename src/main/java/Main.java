import ir.*;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Getting parameters from args
        String topic_set = args[0];
        String document_set = args[1];
        String run_type = args[2];
        String output_file = args[3];

        Corpus corpus = new Corpus();

        // Extract and process topics
        Topics topic_collection = new Topics(corpus, topic_set, run_type);

        // Extract and process documents
        Docs doc_collection = new Docs(corpus, document_set, run_type);

        ArrayList<Doc> processingList = new ArrayList<>();
        processingList.addAll(topic_collection.getDocuments());
        processingList.addAll(doc_collection.getDocuments());

        // Traverse collection here to check
        System.out.println("!----- Check -----!");
        System.out.println("There are " + topic_collection.getDocuments().size() + " topics");
        System.out.println("There are " + doc_collection.getDocuments().size() + " documents");
        System.out.println("Corpus documents: " + corpus.getDocuments().size() + " documents");
        System.out.println("Corpus words: " + corpus.getInvertedIndex().size() + " words");

        VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);

        // Sort relevance score in Desc order
        Map<Double, ArrayList<String>> sortedCosineCollection = new TreeMap<>(Collections.reverseOrder());

        for (Doc doc : doc_collection.getDocuments()) {
            for (Doc topic : topic_collection.getDocuments()) {
                ArrayList cosineSimilarityPair = new ArrayList<>(Arrays.asList(topic.getDocID(), doc.getDocID()));
                sortedCosineCollection.put(vectorSpace.cosineSimilarity(topic, doc), cosineSimilarityPair);
            }
        }

        writeToFile(sortedCosineCollection, output_file);
    }

    private static void writeToFile(Map map, String output_Filename) {
        final int TOP_RESULTS = 1000;
        String stringToBeStored = "";
        int counter = 0;
        OutputStream writer = null;

        Set<Double> keySet = map.keySet();

        System.out.println("Writing to file...");

        try {
            writer = new FileOutputStream(new File("results/"+output_Filename));

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
    }

    private static void iterateCollection(Map map) {
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
